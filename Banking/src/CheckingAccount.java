/*
Author: Kay Patel

The CheckingAccount class represents a bank checking account and offers functionalities to manage the account balance, track transactions, and calculate service charges. It maintains attributes such as the current balance, total service charges, transaction history, transaction count, and the number of violations (balance below $500). The class provides methods to retrieve the current balance in a formatted string, get the final balance after deducting service charges, access transaction details, and retrieve the number of transactions and violations. Additionally, it includes methods to update the account balance, add transactions, and increment counts.
 */

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;


public class CheckingAccount extends Account implements Serializable {
    private final DecimalFormat fmt = (DecimalFormat) DecimalFormat.getInstance();
    private static final String negative = "#,##0.00;($#,##0.00)";
    private static final String positive = "$#,##0.00;$#,##0.00";

    double totalServiceCharge;
    ArrayList<Transaction> transactions;
    int transCount;
    int numberOfViolations; // Tracks the number of violations (balance below $500)

    public CheckingAccount() {
        super("", 0.0);
        totalServiceCharge = 0.00;
        transactions = new ArrayList<>();
        transCount = -1;
        numberOfViolations = 0;
    }

    public CheckingAccount(String name, double initialBalance) {
        super(name, initialBalance);
        totalServiceCharge = 0.00;
        transactions = new ArrayList<>();
        transCount = -1;
        numberOfViolations = 0; // Tracks the number of violations (balance below $500)
    }

    //calculates and displays formatted final balance
    public String getFinalBalance() {
        double finalBalance = super.getBalance() + (-1 * getTotalServiceCharge());
        if (finalBalance >= 0) {
            fmt.applyPattern(positive);
        } else {
            fmt.applyPattern(negative);
        }
        return fmt.format(finalBalance);
    }

    public double getTotalServiceCharge() {
        return totalServiceCharge;
    }

    public String getFormattedTotalServiceCharge() {
        if(totalServiceCharge >= 0) {
            fmt.applyPattern(positive);
        } else {
            fmt.applyPattern(negative);
        }
        return fmt.format(totalServiceCharge);
    }

    public ArrayList<Transaction> getTransactions() {
        return transactions;
    }

    public Transaction getTrans(int i) {
        return transactions.get(i);
    }

    public int getTransCount() {
        return transCount;
    }

    public int getNumberOfViolations() {
        return numberOfViolations;
    }

    //adds bank fees based on transaction and account balance
    public void setTotalServiceCharge(double serviceCharge) {
        totalServiceCharge += serviceCharge;
    }

    public void addTrans(Transaction newTrans) {
        transactions.add(newTrans);
    }

    public void addDeposit(Deposit newDeposit) {
        transactions.add(newDeposit);
    }

    public void addCheck(Check newCheck) {
        transactions.add(newCheck);
    }

    //counts successfully completed transactions
    public void setTransCount() {
        transCount++;
    }

    public void setNumberOfViolations() {
        numberOfViolations += 1;
    }
}
