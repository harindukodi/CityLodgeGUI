package model.entities;

import model.Exceptions.*;


import java.util.List;

/**
 * The child class representing the Suite Room in the CityLodge.
 *
 * @author Harindu Kodituwakku
 */

public class Suite extends Room {

    private static final int RENTAL_RATE = 999;
    private static final int LATE_FEE = 1099;

    public DateTime getLastMaintenanceDate() {
        return lastMaintenanceDate;
    }

    public void setLastMaintenanceDate(DateTime lastMaintenanceDate) {
        this.lastMaintenanceDate = lastMaintenanceDate;
    }

    private DateTime lastMaintenanceDate;
    private static final String FEATURE_SUMMARY = "Air Condition - TV - WiFi - Fridge";
    private static final int NUM_BEDROOMS = 6;

    /**
     * Initializes a suite.
     */
    public Suite(String roomId, DateTime lastMaintenanceDate) {
        super(roomId, NUM_BEDROOMS, FEATURE_SUMMARY, roomType.Suite, roomStatus.Available);
        super.featureSummary = FEATURE_SUMMARY;
        this.lastMaintenanceDate = lastMaintenanceDate;

        try {
            double dateDiff = DateTime.diffDays(new DateTime(), lastMaintenanceDate);
            if (dateDiff < 0) {
                throw new RoomException("Future date message");
            }
            if (dateDiff > 10) {
                set_roomStatus(roomStatus.Maintenance);
            }
        } catch (Exception e) {
            throw new RoomException("Enter Valid date!");
        }
    }

    public Suite(){

    }

    @Override
    public boolean rent(String customerId, DateTime rentDate, int numOfRentDays) {

        String hiringRecordId = getRoomId() + "_" + customerId + "_" + rentDate.getEightDigitDate();
        DateTime estimatesReturnDate = super.calEstimatedReturnDate(rentDate, numOfRentDays);

        if (DateTime.diffDays(estimatesReturnDate, lastMaintenanceDate) > 10) {
            throw new RoomException("Unable to rent the suite as it is undergoing maintenance");
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
    public String toString() {
        List<HiringRecord> hiringRecords = getHiringRecords();
        HiringRecord lastHiringRecord = hiringRecords.size() > 0 ? hiringRecords.get(hiringRecords.size() - 1) : null;

        return String.format("%s:%s:%s:%s:%s:%s:%s", getRoomId(), getNumBedRooms(), get_roomStatus(), get_roomType(), lastMaintenanceDate, FEATURE_SUMMARY,
                lastHiringRecord != null ? lastHiringRecord : "No hiring records available for this room yet.");
    }

    @Override
    public double calRentalFee(DateTime estimatesReturnDate, DateTime rentDate) {
        double rentalFee = 0;
        double stay = DateTime.diffDays(estimatesReturnDate, rentDate);
        rentalFee = stay * RENTAL_RATE;
        return rentalFee;
    }

    @Override
    public double calLateFee(DateTime estimatesReturnDate, DateTime actualReturnDate) {
        double lateFee = 0;

        double difference = DateTime.diffDays(actualReturnDate, estimatesReturnDate);
        lateFee = (difference * LATE_FEE);
        return lateFee;
    }

    @Override
    public String getDetails() {
        //Room is being rented.
        StringBuilder builder = new StringBuilder();
        List<HiringRecord> records = getHiringRecords();
        builder.append(String.format("Room ID:                  %s\n", getRoomId()));
        builder.append(String.format("Number of bedrooms:       %s\n", getNumBedRooms()));
        builder.append(String.format("Type:                     %s\n", get_roomType()));
        builder.append(String.format("Status:                   %s\n", get_roomStatus()));
        builder.append(String.format("Last maintenance date:    %s\n", lastMaintenanceDate));
        builder.append(String.format("Feature Summary:          %s\n", featureSummary));
        if (records.size() == 0) {
            builder.append(String.format("RENTAL RECORD         %s\n", "empty"));
        } else {

            builder.append("RENTAL RECORD\n");
            HiringRecord lastRecord = records.get(records.size() - 1);
            builder.append(lastRecord.getDetails());
            if (records.size() >= 2) {
                int i = 0;
                for (HiringRecord record : records) {
                    if (i == records.size() - 1) {
                        i++;
                        continue;
                    }
                    builder.append("------------------------------------------\n");
                    builder.append(record.getDetails());
                    i++;
                }
            }
        }
        return builder.toString();
    }
}
