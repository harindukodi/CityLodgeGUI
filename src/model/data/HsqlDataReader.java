package model.data;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import model.Exceptions.RoomException;
import model.entities.*;
import org.hsqldb.Server;

public class HsqlDataReader {

    public void init() {
        Server hsqlServer = null;
        Connection connection = null;
        ResultSet rs = null;
        hsqlServer = new Server();
        hsqlServer.setLogWriter(null);
        hsqlServer.setSilent(true);
        hsqlServer.setDatabaseName(0, "cityLodgeDB");
        hsqlServer.setDatabasePath(0, "file:cityLodgeDB");
        hsqlServer.start();
// making a connection
        try {
            Class.forName("org.hsqldb.jdbcDriver");
            connection = getConnection();
            connection.prepareStatement("CREATE TABLE IF NOT EXISTS Room (" +
                    "roomId VARCHAR(50) NOT NULL, " +
                    "numOfBedrooms INTEGER," +
                    "featureSummary VARCHAR(999)," +
                    "roomType INTEGER," +
                    "roomStatus INTEGER," +
                    "lastMaintainanceDate DATE," +
                    "PRIMARY KEY (roomId) );").execute();

            connection.prepareStatement("CREATE TABLE IF NOT EXISTS HiringRecord(" +
                    "roomId VARCHAR(10) NOT NULL," +
                    "customerId VARCHAR(10) NOT NULL," +
                    "rentDate DATE NOT NULL," +
                    "estimatedReturnDate DATE NOT NULL," +
                    "actualReturnDate DATE," +
                    "rentalFee DOUBLE," +
                    "lateFee DOUBLE," +
                    "FOREIGN KEY (roomId) REFERENCES Room(roomId)," +
                    "PRIMARY KEY (roomId, customerId, rentDate));").execute();

            connection.commit();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new RoomException("Could Not Initialize Database");
        } catch (Exception e) {
            throw new RoomException("Could Not Initialize Database");
        }
// end of stub code for in/out stub
    }

    public Connection getConnection() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:hsqldb:cityLodgeDb", "sa", "");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RoomException("Could Not Connect To Database.");
        }
        return connection;
    }

    public void closeConnection(Connection connection) {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RoomException("Could Not Close The Database.");
        }
    }

    private void deleteAll() {
        Connection connection = getConnection();

        try {
            connection.prepareStatement(
                    "DELETE FROM HiringRecord"
            ).execute();

            connection.prepareStatement(
                    "DELETE FROM Room"
            ).execute();

            connection.commit();
            closeConnection(connection);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RoomException("Couldn't clean database");
        }
    }

    public void SaveState(Map<String, Room> rooms) {
        deleteAll();
        rooms.values().forEach(this::insertRoom);
        List<HiringRecord> records = rooms.values()
                .stream()
                .flatMap(room -> room.getHiringRecords().stream())
                .collect(Collectors.toList());
        records.forEach(this::insertRecord);
    }

    public HashMap<String, Room> getRoomData() {
        Connection connection = getConnection();
        HashMap<String, Room> roomCollection = new HashMap<>();
        try (ResultSet rs = connection.prepareStatement("SELECT * FROM Room;").executeQuery()) {
            while (rs.next()) {
                String roomId = rs.getString("roomId");
                Room.roomType roomType = Room.roomType.values()[rs.getInt("roomType")];
                int numBedrooms = rs.getInt("numOfBedrooms");
                String featureSummary = rs.getString("featureSummary");
                Room.roomStatus roomStatus = Room.roomStatus.values()[rs.getInt("roomStatus")];

                if (roomType == Room.roomType.Standard) {
                    Room room = new StandardRoom(roomId, numBedrooms);
                    room.setImgUrl(String.format("/assets/%s.png", room.getRoomId()));
                    room.set_roomType(roomType);
                    room.set_roomStatus(roomStatus);
                    room.setFeatureSummary(featureSummary);
                    room.setHiringRecords(getHiringRecords(roomId));
                    roomCollection.put(roomId, room);
                } else {
                    DateTime lastMD = processDate(rs.getDate("lastMaintainanceDate").toLocalDate());
                    Suite suite = new Suite(roomId, lastMD);
                    suite.set_roomType(roomType);
                    suite.setImgUrl(String.format("/assets/%s.png", suite.getRoomId()));
                    suite.setNumBedRooms(numBedrooms);
                    suite.set_roomStatus(roomStatus);
                    suite.setFeatureSummary(featureSummary);
                    suite.setHiringRecords(getHiringRecords(roomId));
                    roomCollection.put(roomId, suite);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RoomException("Couldn't Load Rooms From Database"
                    + e.getMessage()
            );
        }

        return roomCollection;
    }

    private List<HiringRecord> getHiringRecords(String roomId) {
        Connection connection = getConnection();
        List<HiringRecord> hiringRecords = new ArrayList<>();

        try {
            PreparedStatement recordSelect = connection.prepareStatement("SELECT * FROM HiringRecord WHERE roomId = ?;");
            recordSelect.setString(1, roomId);

            ResultSet rs = recordSelect.executeQuery();

            while (rs.next()) {
                HiringRecord hiringRecord = new HiringRecord();
                String customerId = rs.getString("customerId");
                Date rentDate = rs.getDate("rentDate");
                Date estimatedReturnDate = rs.getDate("estimatedReturnDate");
                Date actualReturnDate = rs.getDate("estimatedReturnDate");
                Double rentalFee = rs.getDouble("rentalFee");
                Double lateFee = rs.getDouble("lateFee");
                DateTime rentDateTime = processDate(rentDate.toLocalDate());

                hiringRecord.setRecordId(String.format("%s_%s_%s", roomId, customerId, rentDateTime.getEightDigitDate()));
                hiringRecord.setRentDate(rentDateTime);
                hiringRecord.setActualReturnDate(processDate(actualReturnDate.toLocalDate()));
                hiringRecord.setEstimatedReturnDate(processDate(estimatedReturnDate.toLocalDate()));
                hiringRecord.setRentalFee(rentalFee);
                hiringRecord.setLateFee(lateFee);

                hiringRecords.add(hiringRecord);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RoomException("Couldn't Load Rooms for" + roomId + " from Database"
                    + e.getMessage()
            );
        }

        return hiringRecords;
    }

    private void insertRecord(HiringRecord hiringRecord) {
        Connection connection = getConnection();
        PreparedStatement insertStatement = null;
        try {
            insertStatement = connection
                    .prepareStatement("INSERT INTO HiringRecord (roomId, customerId, rentDate, estimatedReturnDate)"
                            + "VALUES (?,?, ?, ?);");

            String[] recordIdArr = hiringRecord.getRecordId().split("_");
            String roomId = recordIdArr[0];
            String customerId = recordIdArr[1];

            insertStatement.setString(1, roomId);
            insertStatement.setString(2, customerId);
            insertStatement.setDate(3, hiringRecord.getRentDate().getSqlDate());
            insertStatement.setDate(4, hiringRecord.getEstimatedReturnDate().getSqlDate());

            insertStatement.execute();
            connection.commit();
            closeConnection(connection);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RoomException("Couldn't Save HiringRecord To Database. "
                    + e.getMessage()
            );
        }
    }

    private void insertRoom(Room room) {
        Connection connection = getConnection();
        PreparedStatement insertStatement = null;
        try {
            insertStatement = connection
                    .prepareStatement("INSERT INTO Room (roomId, numOfBedRooms, featureSummary, roomType, roomStatus, lastMaintainanceDate) "
                            + "VALUES (?,?, ?, ?, ? ,?);");
            insertStatement.setString(1, room.getRoomId());
            insertStatement.setInt(2, room.getNumBedRooms());
            insertStatement.setString(3, room.getFeatureSummary());
            insertStatement.setInt(4, room.get_roomType().ordinal());
            insertStatement.setInt(5, room.get_roomStatus().ordinal());

            if (room instanceof Suite) {
                Suite suite = (Suite) room;
                insertStatement.setDate(6, suite.getLastMaintenanceDate().getSqlDate());
            } else {
                insertStatement.setDate(6, null);
            }

            insertStatement.execute();
            connection.commit();
            closeConnection(connection);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RoomException("Couldn't Save HiringRecord To Database. "
                    + e.getMessage()
            );
        }
    }

    private void updateRecord(HiringRecord hiringRecord) {
        Connection connection = getConnection();
        PreparedStatement updateStatement = null;
        try {
            updateStatement = connection
                    .prepareStatement("UPDATE HiringRecord SET actualReturnDate = ?, rentalFee = ?, lateFee = ? " +
                            "WHERE roomId = ? AND customerId = ? AND rentDate = ?");

            String[] recordIdArr = hiringRecord.getRecordId().split("_");
            String roomId = recordIdArr[0];
            String customerId = recordIdArr[1];

            updateStatement.setDate(1, hiringRecord.getActualReturnDate().getSqlDate());
            updateStatement.setDouble(2, hiringRecord.getRentalFee());
            updateStatement.setDouble(3, hiringRecord.getLateFee());
            updateStatement.setString(4, roomId);
            updateStatement.setString(5, customerId);
            updateStatement.setDate(6, hiringRecord.getRentDate().getSqlDate());

            updateStatement.execute();
            connection.commit();
            closeConnection(connection);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RoomException("Couldn't Update HiringRecord In Database. "
                    + e.getMessage()
            );
        }
    }

    private DateTime processDate(LocalDate date) {
        return new DateTime(date.getDayOfMonth(), date.getMonthValue(), date.getYear());
    }
}