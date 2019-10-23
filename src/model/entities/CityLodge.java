package model.entities;
import model.Exceptions.*;
import model.data.HsqlDataReader;

import java.text.ParseException;
import java.util.*;

/**
 * CityLodge class handles all the menus and host a single room collection of both standard and suite rooms
 *
 * @author Harindu Kodituwakku
 */

public class CityLodge {
    /**
     * Creating an HashMap to store all the rooms
     */

    /**
     * Gets all the rooms in the CityLodge Hotel
     *
     * @return The list of rooms.
     */
    public static HashMap<String, Room> getRooms() {
        return roomCollection;
    }

    public static void init(){
        hsqlDataReader.init();
        roomCollection = hsqlDataReader.getRoomData();
    }

    public static HashMap<String, Room> roomCollection = new HashMap<String, Room>(50);

    public static HsqlDataReader hsqlDataReader = new HsqlDataReader();


    public static void Start() {
        Scanner input = new Scanner(System.in);
        String choice;
        boolean exit = false;

        while (true) {
            PrintMainMenu();
            choice = input.nextLine();
            try {
                switch (choice) {
                    case "1": // adds a room
                        addRoom();
                        break;
                    case "2": // rents a room
                        rentRoom();
                        break;
                    case "3": // returns a room
                        returnRoom();
                        break;
                    case "4": // perform room maintenance
                        performMaintenance();
                        break;
                    case "5": // complete room maintenance
                        completeMaintenance();
                        break;
                    case "6": // display all rooms
                        displayAllRooms();
                        break;
                    case "7":
                        System.out.println("Goodbye!");
                        exit = true;
                        break;
                    default:
                        System.out.println("Invalid entry, please choose another option.\n");
                }
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
            if (exit) break;
        }
    }

    /**
     * Prints the CityLodge main menu
     */
    private static void PrintMainMenu() {
        System.out.println("=========================================================");
        System.out.println("           ****  CITYLODGE MAIN MENU  ****");
        System.out.println("=========================================================");
        System.out.println("Add Room:                   1");
        System.out.println("Rent Room:                  2");
        System.out.println("Return Room:                3");
        System.out.println("Room Maintenance:           4");
        System.out.println("Complete Maintenance:       5");
        System.out.println("Display All Rooms:          6");
        System.out.println("Exit Program:               7");
        System.out.print("Enter your choice: ");
    }

    private static void printAddRoomSubMenu() {
        System.out.println("--------------------------------------------------------");
        System.out.println("Add a Standard Room:          1");
        System.out.println("Add a Suite Room:             2");
        System.out.println("Go back to the main menu:     3");
        System.out.print("Enter your choice: ");
    }

    private static void addRoom() {
        Scanner input = new Scanner(System.in);
        String choice;
        while (true) {
            printAddRoomSubMenu();
            choice = input.nextLine();
            boolean exit = false;

            switch (choice) {
                case "1": // adds a Standard Room
                    addStandardRoom();
                    break;
                case "2": // adds a Suite Room
                    addSuiteRoom();
                    break;
                case "3": // returns to the main menu
                    return;
                default:
                    System.out.println("Invalid entry, please choose another option.\n");
                    break;
            }
        }
    }

    private static void validateRoomId(String rRoomId, Room.roomType roomType) {
        String roomPrefix;
        String roomName;
        if(rRoomId == null) {
            throw new RoomException("Room ID cannot be empty!");
        }
        rRoomId = rRoomId.toUpperCase();

        if (roomType == Room.roomType.Suite) {
            roomPrefix = "S";
            roomName = "Suite";
        } else {
            roomPrefix = "R";
            roomName = "Standard Room";
        }
        if (!rRoomId.startsWith(roomPrefix) || roomCollection.containsKey(rRoomId)) {
            throw new RoomException(String.format("This %s either exists or an invalid room Id", roomName));
        }
    }

    private static void validateNumBedRooms(int numBedroom) {
        if (!(numBedroom == 1 || numBedroom == 2 || numBedroom == 4)) {
            throw new RoomException("Standard rooms can have either 1,2 or 4 rooms!");
        }
    }

    public static void addStandardRoom() {
        Scanner input = new Scanner(System.in);
        System.out.print("Enter Room ID: ");
        String rRoomId = input.nextLine();
        try {
            validateRoomId(rRoomId.toUpperCase(), Room.roomType.Standard);
            System.out.print("Number of bed rooms (1, 2 or 4): ");
            String numBedroom = input.nextLine();
            processStandardRoom(rRoomId, numBedroom);
        } catch (RoomException e) {
            PrintAndHandleExceptions(input, e);
            return;
        }
    }

    public static Room addStandardRoomGUI(Room room) {
        validateRoomId(room.getRoomId(), Room.roomType.Standard);
        processStandardRoom(room.getRoomId(), String.valueOf(room.getNumBedRooms()));
        return roomCollection.get(room.getRoomId());
    }

    private static void processStandardRoom(String rRoomId, String numBedroom) {
        if (numBedroom.equals("1") || numBedroom.equals("2") || numBedroom.equals("4")) {
            validateNumBedRooms(Integer.parseInt(numBedroom));
            roomCollection.put(rRoomId, new StandardRoom(rRoomId, Integer.parseInt(numBedroom)));
            System.out.println("Standard room " + rRoomId + " successfully added!");
        } else {
            throw new RoomException("Invalid entry. Number of bedrooms could be 1, 2 or 4.");
        }
    }

    private static void PrintAndHandleExceptions(Scanner input, RoomException e) {
        System.out.println(e.getMessage());
    }

    public static void addSuiteRoom() {
        Scanner input = new Scanner(System.in);
        System.out.print("Enter Room ID: ");
        String sRoomId = input.nextLine();
        try {
            validateRoomId(sRoomId.toUpperCase(), Room.roomType.Suite);
            System.out.print("Last Maintenance date (dd/mm/yyyy): ");
            String dateStr = input.nextLine();
            int[] lastMaintenanceDate = buildDate(dateStr);
            DateTime maintenanceDate = new DateTime(lastMaintenanceDate[0], lastMaintenanceDate[1], lastMaintenanceDate[2]);
            roomCollection.put(sRoomId, new Suite(sRoomId, maintenanceDate));
            System.out.println("Suite room " + sRoomId + " successfully added!");
        } catch (RoomException e) {
            PrintAndHandleExceptions(input, e);
            return;
        }
    }

    public static Room addSuiteRoomGUI(Room room, String dateStr) {
        validateRoomId(room.getRoomId(), Room.roomType.Suite);
        int[] lastMaintenanceDate = buildDate(dateStr);
        DateTime maintenanceDate = new DateTime(lastMaintenanceDate[2], lastMaintenanceDate[1], lastMaintenanceDate[0]);
        roomCollection.put(room.getRoomId(), new Suite(room.getRoomId(), maintenanceDate));
        return roomCollection.get(room.getRoomId());
    }

    public static void rentRoom() throws ParseException {
        Scanner input = new Scanner(System.in);

        System.out.print("Room ID: ");
        String roomId = input.nextLine();

        if (isAvailable(roomId)) {
            System.out.print("Customer ID: ");
            String customerId = input.nextLine();
            try {
                System.out.print("Rent date (dd/mm/yyyy): ");
                String dateStr = input.nextLine();
                int[] returnDate = buildDate(dateStr);
                DateTime rentDate = new DateTime(returnDate[0], returnDate[1], returnDate[2]);

                System.out.print("How  many days?: ");
                int numOfRentDay = input.nextInt();

                Room selectedRoom = roomCollection.get(roomId);
                String roomType = selectedRoom.get_roomType() == Room.roomType.Standard ? "Room" : "Suite";
                if (selectedRoom.rent(customerId, rentDate, numOfRentDay))
                    System.out.println(String.format("%s %s is now rented by customer %s", roomType, roomId, customerId));
            } catch (RoomException e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.out.println("Room " + roomId + " is not available at the moment.");
        }
    }

    private static boolean isAvailable(String roomId) {
        try {
            if (roomCollection.containsKey(roomId)) {
                Room availableRoom = roomCollection.get(roomId);
                return roomCollection.containsKey(roomId) && availableRoom.get_roomStatus() == Room.roomStatus.Available;
            } else {
                return false;
            }
        } catch (RoomException e) {
            System.out.println(e);
        }
        return false;
    }

    public static void returnRoom() {
        Scanner input = new Scanner(System.in);

        try {
            System.out.print("Room ID: ");
            String roomId = input.nextLine();
            if (roomCollection.containsKey(roomId)) {
                Room selectedRoom = roomCollection.get(roomId);
                if (selectedRoom.get_roomStatus() == Room.roomStatus.Rented) {
                    System.out.print("Return date (dd/mm/yyyy): ");
                    String returnDateStr = input.nextLine();
                    int[] returnDate = buildDate(returnDateStr);
                    DateTime formattedReturnDate = new DateTime(returnDate[0], returnDate[1], returnDate[2]);

                    if (selectedRoom.returnRoom(formattedReturnDate)) {
                        System.out.println(selectedRoom);
                    } else {
                        System.out.println("Invalid entry. Return date is prior to the rent date!");
                    }
                } else {
                    System.out.println("Only rented rooms can be returned!");
                }
            } else {
                System.out.println("This room doesn't contain in the CityLodge.");
                return;
            }
        } catch (RoomException e) {
            System.out.println(e);
        }
    }

    private static void performMaintenance() {
        try {
            Scanner input = new Scanner(System.in);

            System.out.print("Room ID: ");
            String roomId = input.nextLine();
            if (roomCollection.containsKey(roomId)) {
                Room selectedRoom = roomCollection.get(roomId);
                if (selectedRoom.get_roomStatus() == Room.roomStatus.Available) {
                    if (selectedRoom.performMaintenance()) {
                        System.out.println(String.format("Room %s is now under maintenance.", roomId));
                    }
                } else if (selectedRoom.get_roomStatus() == Room.roomStatus.Maintenance) {
                    System.out.println(String.format("Room %s is already in maintenance", roomId));
                    return;
                } else {
                    System.out.println("Maintenance cannot be performed when the room is rented! ");
                    return;
                }
            } else {
                System.out.println(String.format("Room %s is not a registered room in the CityLodge Hotel", roomId));
                return;
            }
        } catch (RoomException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void completeMaintenance() {
        Scanner input = new Scanner(System.in);

        System.out.print("Enter room Id: ");
        String roomId = input.nextLine();
        if (roomCollection.containsKey(roomId)) {
            Room selectedRoom = roomCollection.get(roomId);
            if (selectedRoom.get_roomStatus() == Room.roomStatus.Maintenance) {
                if (selectedRoom.getRoomId().startsWith("S")) {
                    System.out.print("Maintenance completion date (dd/mm/yyyy): ");
                    String returnDateStr = input.nextLine();
                    int[] returnDate = buildDate(returnDateStr);
                    DateTime formattedReturnDate = new DateTime(returnDate[0], returnDate[1], returnDate[2]);
                    if (selectedRoom.completeMaintenance(formattedReturnDate)) {
                        System.out.println(String.format("Suite %s has all maintenance operations completed and is now ready for rent. ", roomId));
                    }
                } else {
                    System.out.println(String.format("Room %s has all maintenance operations completed and is now ready for rent. ", roomId));
                }

            } else {
                System.out.println(String.format("Room %s is not under maintenance to complete this action. ", roomId));
            }
        } else {
            System.out.println(String.format("Room %s is not a registered room in the CityLodge Hotel", roomId));
        }
    }

    private static void displayAllRooms() {
        roomCollection.values().forEach(room -> System.out.println(room.getDetails()));
    }

    /**
     * buildDate method responsible to initialize date when a string is given as the input
     */

    private static int[] buildDate(String returnDateStr) {
        try {
            String[] dateComponents = returnDateStr.split("/");
            int dateInt = Integer.parseInt(dateComponents[0]);
            int monthInt = Integer.parseInt(dateComponents[1]);
            int yearInt = Integer.parseInt(dateComponents[2]);

            return new int[]{dateInt, monthInt, yearInt};
        } catch (Exception e) {
            throw new RoomException("Enter a valid date.");
        }
    }
}
