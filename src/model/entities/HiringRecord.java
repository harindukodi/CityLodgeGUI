package model.entities;

/**
 * The HiringRecord class responsible for maintaining the renting history of the CityLodge Hotel.
 *
 * @author Harindu Kodituwakku
 */

public class HiringRecord {

    private String recordId;
    private DateTime rentDate;
    private DateTime estimatedReturnDate;
    private DateTime actualReturnDate;
    private double rentalFee;
    private double lateFee;

    /**
     * Initializes a HiringRecord.
     */
    public HiringRecord(String recordId, DateTime rentDate, DateTime estimatedReturnDate) {
        this.recordId = recordId;
        this.rentDate = rentDate;
        this.estimatedReturnDate = estimatedReturnDate;
    }

    public HiringRecord(){

    }

    @Override
    public String toString() {
        String actualReturnDateStr = actualReturnDate == null ? "none" : actualReturnDate.toString();
        String rentalFeeStr = rentalFee > 0 ? String.format("%.2f", rentalFee) : "none";
        String lateFeeStr;

        if (actualReturnDate == null) lateFeeStr = "none";
        else {
            if (DateTime.diffDays(actualReturnDate, estimatedReturnDate) == 0) lateFeeStr = String.format("%.2f", 0.00);
            else lateFeeStr = String.format("%.2f", lateFee);
        }
        return String.format("%s:%s:%s:%s:%s:%s", recordId, rentDate, estimatedReturnDate, actualReturnDateStr, rentalFeeStr, lateFeeStr);
    }

    public double getLateFee() {
        return lateFee;
    }


    public String getDetails() {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("Record ID:                %s\n", recordId));
        builder.append(String.format("Rent Date:                %s\n", rentDate));
        builder.append(String.format("Estimated Return Date:    %s\n", estimatedReturnDate));

        if (actualReturnDate != null) {
            builder.append(String.format("Actual Return Date:       %s\n", actualReturnDate));
            builder.append(String.format("Rental Fee:               %.2f\n", rentalFee));
            builder.append(String.format("Late Fee:                 %.2f\n", lateFee));
            builder.append(String.format("------------------------------------------------------------"));
        }
        return builder.toString();
    }

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public DateTime getEstimatedReturnDate() {
        return estimatedReturnDate;
    }

    public void setEstimatedReturnDate(DateTime estimatedReturnDate) {
        this.estimatedReturnDate = estimatedReturnDate;
    }

    public DateTime getActualReturnDate() {
        return actualReturnDate;
    }

    public void setActualReturnDate(DateTime actualReturnDate) {
        this.actualReturnDate = actualReturnDate;
    }

    public DateTime getRentDate() {
        return rentDate;
    }

    public void setRentDate(DateTime rentDate) {
        this.rentDate = rentDate;
    }

    public double getRentalFee() {
        return rentalFee;
    }

    public void setRentalFee(double rentalFee) {
        this.rentalFee = rentalFee;
    }

    public void setLateFee(double lateFee) {
        this.lateFee = lateFee;
    }
}
