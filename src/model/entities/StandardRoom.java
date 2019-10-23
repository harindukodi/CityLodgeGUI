package model.entities;

import model.Exceptions.*;

import java.util.List;

/**
 * The child class representing the Standard Room in the CityLodge.
 *
 * @author Harindu Kodituwakku
 */

public class StandardRoom extends Room {

    private static final double RENTAL_RATE_ONE = 59;
    private static final double RENTAL_RATE_TWO = 99;
    private static final double RENTAL_RATE_FOUR = 199;
    private static final double LATE_RENTAL_RATE = 135;
    private static final String FEATURE_SUMMARY = "Air Condition - TV - WiFi - Fridge";

    /**
     * Initializes a standard room.
     */
    public StandardRoom(String roomId, int numBedRooms) {
        super(roomId, numBedRooms, FEATURE_SUMMARY, roomType.Standard, roomStatus.Available);
        super.featureSummary = FEATURE_SUMMARY;
    }

    public StandardRoom() {
        super();
    }

    @Override
    public boolean rent(String customerId, DateTime rentDate, int numOfRentDays) {
        String hiringRecordId = getRoomId() + "_" + customerId + "_" + rentDate.getEightDigitDate();
        DateTime estimatesReturnDate = super.calEstimatedReturnDate(rentDate, numOfRentDays);

        if (!rentDate.getNameOfDay().equals("Saturday") && !rentDate.getNameOfDay().equals("Sunday")) {
            if (numOfRentDays < 2) {
                throw new RoomException("A minimum of 2 days of rent is required for weekdays.");
            }
        } else {
            if (numOfRentDays < 3) {
                throw new RoomException("A minimum of 3 days of rent is required for the weekend.");
            }
        }

        if (numOfRentDays > 10) {
            throw new RoomException("Cannot rent rooms for more than 10 days.");
        }

        HiringRecord newRecord = new HiringRecord(hiringRecordId, rentDate, estimatesReturnDate);
        List<HiringRecord> records = getHiringRecords();

        if (records.size() == 10) {
            records.remove(0);
        }

        records.add(newRecord);
        super.set_roomStatus(roomStatus.Rented);
        return true;
    }

    @Override
    public boolean returnRoom(DateTime returnDate) {
        // Last record of the hiringRecord ArrayList will always be the active hiring record.
        List<HiringRecord> hiringRecords = getHiringRecords();
        HiringRecord lastHiringRecord = hiringRecords.get(hiringRecords.size() - 1);
        if (DateTime.diffDays(returnDate, lastHiringRecord.getRentDate()) > 0) {
            double rentalFee = calRentalFee(lastHiringRecord.getEstimatedReturnDate(), lastHiringRecord.getRentDate());
            double lateFee = calLateFee(lastHiringRecord.getEstimatedReturnDate(), returnDate);
            lastHiringRecord.setRentalFee(rentalFee);
            lastHiringRecord.setLateFee(lateFee);
            lastHiringRecord.setActualReturnDate(returnDate);
            set_roomStatus(roomStatus.Available);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean performMaintenance() {
        set_roomStatus(roomStatus.Maintenance);
        return true;
    }


    @Override
    public boolean completeMaintenance(DateTime completionDate) {
        set_roomStatus(roomStatus.Available);
        return true;
    }

    @Override
    public double calRentalFee(DateTime estimatesReturnDate, DateTime rentDate) {
        double rentalFee = 0;
        double extraStay = DateTime.diffDays(estimatesReturnDate, rentDate);
        if (getNumBedRooms() == 1)
            rentalFee = extraStay * RENTAL_RATE_ONE;
        else if (getNumBedRooms() == 2)
            rentalFee = extraStay * RENTAL_RATE_TWO;
        else
            rentalFee = extraStay * RENTAL_RATE_FOUR;
        return rentalFee;
    }

    @Override
    public double calLateFee(DateTime estimatesReturnDate, DateTime actualReturnDate) {
        double lateFee = 0;
        double extraStay = DateTime.diffDays(actualReturnDate, estimatesReturnDate);
        if (getNumBedRooms() == 1)
            lateFee = ((extraStay * LATE_RENTAL_RATE) / 100) * RENTAL_RATE_ONE;
        else if (getNumBedRooms() == 2)
            lateFee = ((extraStay * LATE_RENTAL_RATE) / 100) * RENTAL_RATE_TWO;
        else
            lateFee = ((extraStay * LATE_RENTAL_RATE) / 100) * RENTAL_RATE_FOUR;
        return lateFee;
    }

    public void standardRoomSpecificFunction() {
        System.out.println("Unique behaviour.");
    }

    public boolean validateReturnDate() {
        List<HiringRecord> records = getHiringRecords();
        return true;
        //(rooms.stream().anyMatch(room -> room.getRoomId().equals(roomId)))
    }

    public String toString() {
        List<HiringRecord> hiringRecords = getHiringRecords();
        HiringRecord lastHiringRecord = hiringRecords.size() > 0 ? hiringRecords.get(hiringRecords.size() - 1) : null;

        return String.format("%s:%s:%s:%s:%s:%s", getRoomId(), getNumBedRooms(), get_roomStatus(), get_roomType(), FEATURE_SUMMARY,
                lastHiringRecord != null ? lastHiringRecord : "No hiring records available for this room yet.");
    }
}
