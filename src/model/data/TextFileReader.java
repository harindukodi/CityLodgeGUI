package model.data;

import model.Exceptions.RoomException;
import model.entities.*;
import model.entities.Room;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Stream;

public class TextFileReader {

    private final String relName = "rooms.txt";

    public TextFileReader() throws FileNotFoundException {
    }

    public List<HiringRecord> readTextFile() {

        List<HiringRecord> hiringRecords = new ArrayList<>();

        try (Stream<String> stream = Files.lines(Paths.get(relName))) {
            stream.map(line -> line.split(",")).forEach(lineArr -> {
//                HiringRecord hiringRecord = new HiringRecord();
//                hiringRecord.setRecordId(clean(lineArr[0]));
//                hiringRecord.setRentDate(processDate(lineArr[1]));
//                hiringRecord.setEstimatedReturnDate(processDate(lineArr[2]));
//                hiringRecord.setActualReturnDate(processDate(lineArr[3]));
//                hiringRecord.setRentalFee(Double.parseDouble(clean(lineArr[4])));
//                hiringRecords.add(hiringRecord);
            });
            return hiringRecords;
        } catch (IOException e) {
            throw new RoomException("Corrupted rooms.txt file");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RoomException("Invalid data on rooms.txt file. "
                    + e.getMessage()
            );
        }
    }

    private DateTime processDate(String dateString) {
        dateString = clean(dateString);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime dateTime = LocalDateTime.parse(dateString, formatter);
        return new DateTime(dateTime.getDayOfMonth(), dateTime.getMonthValue(), dateTime.getYear());
    }

    private String clean(String dirStr) {
        return dirStr.replace('\t', ' ').trim();
    }

    //Hashtable<String, Room> roomTable = new Hashtable();
    HashMap<String, Room> roomTable = new HashMap<String, Room>();


    public void textReader(String fileName) throws IOException {
        FileReader reader = new FileReader(fileName);
        try (BufferedReader br = new BufferedReader(reader)) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] lineItems = line.split(":");
                String[] firstItemArray = lineItems[0].split("_");
                List<HiringRecord> hiringRecords = new ArrayList<>(10);
                if (firstItemArray.length == 1) {
                    //ROOM MODE
                    String roomId = clean(firstItemArray[0]);
                    int numBedrooms = Integer.parseInt(clean(lineItems[1]));
                    String roomStatus = clean(lineItems[2]);
                    String roomType = clean(lineItems[3]);
                    if (roomType.equals(Room.roomType.Standard.toString())) {
                        Room room = new StandardRoom(roomId, numBedrooms);
                        String featureSummary = clean(lineItems[4]);
                        String imageName = clean(lineItems[5]);
                        room.set_roomType(Room.roomType.Standard);
                        room.set_roomStatus(Room.roomStatus.Available);
                        room.setFeatureSummary(featureSummary);
                        room.setHiringRecords(hiringRecords);
                        roomTable.put(roomId, room);
                    } else {
                        DateTime lastMD = processDate(clean(lineItems[5]));
                        Suite room = new Suite(roomId, lastMD);
                        String featureSummary = clean(lineItems[6]);
                        String imageName = clean(lineItems[7]);
                        room.set_roomType(Room.roomType.Suite);
                        room.setNumBedRooms(numBedrooms);
                        room.set_roomStatus(Room.roomStatus.Available);
                        room.setFeatureSummary(featureSummary);
                        room.setHiringRecords(hiringRecords);
                        roomTable.put(roomId, room);
                    }
                } else {
                    //HIRING RECORD MODE
                    String roomId = clean(firstItemArray[0]);
                    Room room = roomTable.get(roomId);
                    HiringRecord hiringRecord = new HiringRecord();
                    hiringRecord.setRecordId(clean(lineItems[0]));
                    hiringRecord.setRentDate(processDate(clean(lineItems[1])));
                    hiringRecord.setEstimatedReturnDate(processDate(clean(lineItems[2])));
                    hiringRecord.setActualReturnDate(processDate(clean(lineItems[3])));
                    hiringRecord.setRentalFee(Integer.parseInt(clean(lineItems[4])));
                    hiringRecord.setLateFee(Integer.parseInt(clean(lineItems[5])));
                    room.getHiringRecords().add(hiringRecord);
                }
            }
        }
    }


}
