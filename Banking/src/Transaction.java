/*
Author: Kay Patel

The Transaction class represents an individual transaction within the checking account. It contains attributes such as the transaction number, transaction ID, and transaction amount. The class provides methods to retrieve these attributes, including the formatted transaction amount as a string. The transaction number represents the current value of transCount in the CheckingAccount class, while the transaction ID indicates the type of transaction (1 = check, 2 = deposit, 3 = service charge).
 */

import java.io.Serializable;
import java.text.DecimalFormat;

public class Transaction implements Serializable {

    private final DecimalFormat fmt = (DecimalFormat) DecimalFormat.getInstance();
    private static final String decimal = "$0.00";

    private final int transNumber;
    private final int transId;
    private final double transAmount;

    public Transaction(int number, int id, double amount) {
        transNumber = number; //current value of transCount in the CheckingAccount class
        transId = id; //1 = check; 2 = deposit; 3 = service charge
        transAmount = amount;
    }

    public int getTransNumber() {
        return transNumber;
    }

    public int getTransId() {
        return transId;
    }

    public double getTransAmount() {
        return transAmount;
    }

    public String getFormattedTransAmount() {
        fmt.applyPattern(decimal);
        return fmt.format(transAmount);
    }

}
