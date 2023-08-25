/*
Serves as a parent to the  CheckingAccount class
 */
import java.io.Serializable;
import java.text.DecimalFormat;

public class Account implements Serializable {
    private final DecimalFormat fmt = (DecimalFormat) DecimalFormat.getInstance();
    private static final String negative = "#,##0.00;($#,##0.00)";
    private static final String positive = "$#,##0.00;$#,##0.00";
    protected String name;
    protected double balance;

    public Account(String acctName, double initBalance) {
        name = acctName;
        balance = initBalance;
    }

    public String getName() {
        return name;
    }

    public double getBalance() {
        return balance;
    }

    public String getFormatedBalance() {
        if(balance < 0) {
            fmt.applyPattern(negative);
        } else {
            fmt.applyPattern(positive);
        }
        return fmt.format(balance);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBalance(int transactionCode, double amount) {
        if (transactionCode == 1) {
            balance -= amount;
        } else {
            balance += amount;
        }
    }
}
