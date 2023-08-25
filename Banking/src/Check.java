/*
Defines the check number and get method for processing the check number for each check transaction
 */

import java.io.Serializable;

public class Check extends Transaction implements Serializable {
    private int checkNumber;

    public Check(int transNumber, int checkId, double checkAmount, int checkNumber) {
        super(transNumber, checkId, checkAmount);
        this.checkNumber = checkNumber;
    }

    public int getCheckNumber() {
        return checkNumber;
    }

    public void setCheckNumber(int checkNumber) {
        this.checkNumber = checkNumber;
    }
}
