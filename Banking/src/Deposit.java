/*
Defines the cash and check amounts that comprise the total deposit transaction amount
 */

import java.io.Serializable;
import java.text.DecimalFormat;

public class Deposit extends Transaction implements Serializable {
    private final DecimalFormat fmt = (DecimalFormat) DecimalFormat.getInstance();
    private static final String negative = "#,##0.00;($#,##0.00)";
    private static final String positive = "$#,##0.00;$#,##0.00";
    private double cashAmount;
    private double checkAmount;

    public Deposit(int transNumber, int depositId, double cashAmount, double checkAmount) {
        super(transNumber, depositId, cashAmount + checkAmount);
        this.cashAmount = cashAmount;
        this.checkAmount = checkAmount;
    }

    public double getCashAmount() {
        return cashAmount;
    }

    public String getFormatedCashAmount() {
        if(cashAmount >= 0) {
            fmt.applyPattern(positive);
        } else {
            fmt.applyPattern(negative);
        }
        return fmt.format(cashAmount);
    }

    public double getCheckAmount() {
        return checkAmount;
    }

    public String getFormatedCheckAmount() {
        if(checkAmount >= 0) {
            fmt.applyPattern(positive);
        } else {
            fmt.applyPattern(negative);
        }
        return fmt.format(checkAmount);
    }
}
