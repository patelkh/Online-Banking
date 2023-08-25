/*
Author: Kay Patel
Assignment #6
The Main class represents the main entry point of a program that simulates a banking application. It includes a graphical user interface (GUI) implemented using Swing components. The program allows users to perform various actions on a checking account, such as entering transactions, listing all transactions, listing checks, and listing deposits.

The class initializes the GUI components, sets up the checking account object, and defines various service charges and minimum balance requirements. The program continuously displays the options panel until the user closes the application window.

The OptionsPanel class is an inner class that extends JPanel and represents the panel displaying the available actions. It includes radio buttons for different actions and handles their selection using an ActionListener. It also includes a helper method, showTransactionGrid, which displays transaction information based on the selected action.

The Main class also includes other helper methods such as getTransCode and getTransAmt, which prompt the user for transaction details, and computeCheckCharges, computeDepositCharges, and computeCharges, which calculate and update service charges based on the transaction type and account balance.

The processCheck, processDeposit, and processCancel methods handle the processing of check and deposit transactions and the cancellation of a transaction, respectively. They update the account balance, add transactions to the account, calculate service charges, and display transaction details.

Additionally, the program provides functionalities to save and load account data from a file. The user is prompted to save the account data if there are pending changes before exiting the application.

Please note that the program contains some graphical components (GUI) implemented using Swing. It allows the user to interact with the application using radio buttons and input dialogs. Transactions such as checks and deposits can be entered, and the program displays information about all transactions, checks, and deposits.

The program also calculates service charges based on the type of transaction and the account balance. It provides a simple banking simulation experience, where the user can manage transactions and view account details.

*/



import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.text.DecimalFormat;
import java.util.Objects;
import java.util.Vector;


public class Main {
    private static final int CHECK_ID = 1;
    private static final int DEPOSIT_ID = 2;
    private static final int SERVICE_CHARGE_ID = 3;
    private static final DecimalFormat fmt = (DecimalFormat) DecimalFormat.getInstance();
    private static final String decimal = "$0.00";
    private static final double depositCharge = 0.10; // Service charge for deposit transactions
    private static final double checkCharge = 0.15; // Service charge for check transactions
    private static final double belowZeroCharge = 10.00; // Service charge for a balance below $0
    private static final double below500Charge = 5.00; // Service charge for a balance below $500
    private static final double minimumBalance = 50.00; // Minimum required balance for the account
    private static final JFrame frame = new JFrame();
    private static CheckingAccount account;
    private static final String fileName = "account.txt";
    private static boolean pendingSave = false;
    private static Vector<CheckingAccount> checkingAccountList = new Vector<>();

    public static void main(String[] args) {
        fmt.applyPattern(decimal);
        frame.setSize(400, 500);
        do {
            MainPanel panel = new MainPanel();
            frame.getContentPane().add(panel);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        } while (frame.isActive());
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                if(pendingSave) {
                    int option = JOptionPane.showConfirmDialog(new JFrame(), "The data in the application is not saved. \n Would you like to save it before exiting the application?", "Select an Option", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if(option == JOptionPane.YES_OPTION) {
                        saveHandler();
                        if(!pendingSave) {
                            System.exit(0);
                        } else {
                            frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                        }
                    }
                    else if(option == JOptionPane.NO_OPTION) {
                        System.exit(0);
                    }
                    else if(option == JOptionPane.CANCEL_OPTION) {
                        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                    }
                } else {
                    System.exit(0);
                }
            }
        });
    }

    //GUI panel for entering cash and check amount for a deposit.
    public static class DepositPanel extends JPanel {
        private static JTextField cashField;
        private static JTextField checkField;
        public DepositPanel(JFrame parentFrame) {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            JLabel cashLabel = new JLabel(" Cash");
            cashField = new JTextField();
            JLabel checkLabel = new JLabel(" Check");
            checkField = new JTextField();
            //left align labels and fields
            cashLabel.setAlignmentX(LEFT_ALIGNMENT);
            cashField.setAlignmentX(LEFT_ALIGNMENT);
            checkLabel.setAlignmentX(LEFT_ALIGNMENT);
            checkField.setAlignmentX(LEFT_ALIGNMENT);
            //add labels and fields to panel
            add(cashLabel);
            add(cashField);
            add(checkLabel);
            add(checkField);
        }

        public String getCashAmount() {
            String result = cashField.getText();
            if(Objects.equals(result, "")) {
                return "0";
            }
            return result;
        }
        public String getCheckAmount() {
            String result = checkField.getText();
            if(Objects.equals(result, "")) {
                return "0";
            }
            return result;
        }
    }

    public static class MainPanel extends JPanel {
        private final JMenuItem openFile;
        private final JMenuItem saveFile;
        private final JMenuItem addAccount;
        private final JMenuItem listTransactions;
        private final JMenuItem listChecks;
        private final JMenuItem listDeposits;
        private final JMenuItem listServiceCharges;
        private final JMenuItem findAccount;
        private final JMenuItem listAccounts;
        private final JMenuItem addTransaction;
        private final JTextArea textArea;

        public MainPanel() {
            setLayout(new BorderLayout());
            JMenuBar menuBar = new JMenuBar();
            JMenu fileMenu = new JMenu("File");
            openFile = new JMenuItem("Open File");
            saveFile = new JMenuItem("Save File");

            JMenu accountMenu = new JMenu("Accounts");
            addAccount = new JMenuItem("Add New Account");
            listTransactions = new JMenuItem("List All Transactions");
            listChecks = new JMenuItem("List All Checks");
            listDeposits = new JMenuItem("List All Deposits");
            listServiceCharges = new JMenuItem("List All Service Charges");
            findAccount = new JMenuItem("Find An Account");
            listAccounts = new JMenuItem("List All Accounts");

            JMenu transactionMenu = new JMenu("Transactions");
            addTransaction = new JMenuItem("Enter Transaction");


            fileMenu.add(openFile);
            fileMenu.add(saveFile);

            accountMenu.add(addAccount);
            accountMenu.add(listTransactions);
            accountMenu.add(listChecks);
            accountMenu.add(listDeposits);
            accountMenu.add(listServiceCharges);
            accountMenu.add(findAccount);
            accountMenu.add(listAccounts);

            transactionMenu.add(addTransaction);

            menuBar.add(fileMenu);
            menuBar.add(accountMenu);
            menuBar.add(transactionMenu);

            OptionListener listener = new OptionListener();
            openFile.addActionListener(listener);
            saveFile.addActionListener(listener);
            addAccount.addActionListener(listener);
            listTransactions.addActionListener(listener);
            addTransaction.addActionListener(listener);
            listChecks.addActionListener(listener);
            listDeposits.addActionListener(listener);
            listServiceCharges.addActionListener(listener);
            findAccount.addActionListener(listener);
            listAccounts.addActionListener(listener);

            textArea = new JTextArea();
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            textArea.setEditable(false);

            add(menuBar, BorderLayout.NORTH);
            add(new JScrollPane(textArea));
        }
        private class OptionListener implements ActionListener {
            public void actionPerformed(ActionEvent event) {
                Object source = event.getSource();
                frame.setVisible(false);
                if(source == openFile) {
                    openHandler();
                }
                else if(source == saveFile) {
                    saveHandler();
                } else if(source == addAccount) {
                    String name = getAccountName(frame);
                    if(name != null) {
                        String initBalance = getInitialBalance(frame);
                        if(initBalance != null) {
                            account = new CheckingAccount(name, Double.parseDouble(initBalance));
                            checkingAccountList.add(account);
                            textArea.setText("New account added for " + account.getName());
                            pendingSave = true;
                        }
                    }
                } else if(source == listTransactions) {
                    textArea.setText("List All Transactions\n" +
                            "Name: " + account.getName() +"\n" +
                            "Balance: " + account.getFormatedBalance() + "\n" +
                            "Total Service Charge: " + account.getFormattedTotalServiceCharge() + "\n\n" +
                            "ID\tType\tAmount\n"
                            );
                    for(Transaction transaction: account.getTransactions()) {
                        String type = "";
                        int transactionId = transaction.getTransId();
                        if (transactionId == CHECK_ID) {
                            type = "Check";
                        } else if (transactionId == DEPOSIT_ID) {
                            type = "Deposit";
                        } else if (transactionId == SERVICE_CHARGE_ID) {
                            type = "Svc. Chg.";
                        }
                        textArea.append(transaction.getTransNumber() + "\t" + type + "\t" + transaction.getFormattedTransAmount()+ "\n");
                    }
                } else if(source == listChecks) {
                    textArea.setText("List All Checks\n" +
                            "Name: " + account.getName() +"\n" +
                            "Balance: " + account.getFormatedBalance() + "\n" +
                            "Total Service Charge: " + account.getFormattedTotalServiceCharge() + "\n\n" +
                            "ID\tCheck\tAmount\n"
                    );
                    for(Object obj: account.getTransactions()) {
                        if(obj instanceof Check) {
                            textArea.append(((Check) obj).getTransNumber() + "\t" + String.valueOf(((Check) obj).getCheckNumber()) + "\t" + ((Check) obj).getFormattedTransAmount() + "\n");
                        }
                    }
                } else if(source == listDeposits) {
                    textArea.setText("List All Deposits\n" +
                            "Name: " + account.getName() +"\n" +
                            "Balance: " + account.getFormatedBalance() + "\n" +
                            "Total Service Charge: " + account.getFormattedTotalServiceCharge() + "\n\n" +
                            "ID\tCash\tCheck\tAmount\n"
                    );
                    for(Object obj: account.getTransactions()) {
                        if (obj instanceof Deposit) {
                            textArea.append(((Deposit) obj).getTransNumber() + "\t" + String.valueOf(((Deposit) obj).getFormatedCashAmount()) + "\t" + String.valueOf(((Deposit) obj).getFormatedCheckAmount()) + "\t" + ((Deposit) obj).getFormattedTransAmount() + "\n");
                        }
                    }
                } else if(source == listServiceCharges) {
                    textArea.setText("List All Service Charges\n" +
                            "Name: " + account.getName() +"\n" +
                            "Balance: " + account.getFormatedBalance() + "\n" +
                            "Total Service Charge: " + account.getFormattedTotalServiceCharge() + "\n\n" +
                            "ID\tAmount\n"
                    );
                    for(Transaction transaction: account.getTransactions()) {
                        int transactionId = transaction.getTransId();
                        if(transactionId == SERVICE_CHARGE_ID) {
                            textArea.append(transaction.getTransNumber()+ "\t" + transaction.getFormattedTransAmount()+ "\n");
                        }
                    }
                } else if(source == listAccounts) {
                    textArea.setText("List of All Accounts\n\n");
                    for(CheckingAccount account: checkingAccountList) {
                        textArea.append("Name: " + account.getName() + "\n" +
                                "Balance: " + account.getFormatedBalance() + "\n" +
                                "Total Service Charge: " + account.getFormattedTotalServiceCharge() + "\n\n");
                    }
                } else if(source == findAccount) {
                    String name = getAccountName(frame);
                    boolean foundAccount = false;
                    for(CheckingAccount acct: checkingAccountList) {
                        if(Objects.equals(acct.getName(), name)) {
                            textArea.setText("Found account for " + name);
                            account = acct;
                            foundAccount = true;
                        }
                    }
                    if(checkingAccountList.isEmpty() || !foundAccount) {
                        textArea.setText("Account not found for " + name);
                    }
                } else if(source == addTransaction) {
                    if(checkingAccountList.isEmpty()) {
                        JOptionPane.showMessageDialog(frame, "You must select an account first.", "Message", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        double transactionAmount;
                        int transactionCode = getTransCode(frame);
                        if (transactionCode == 0) {
                            processCancel(fmt, frame, account);
                        } else if (transactionCode == CHECK_ID) {
                            int checkNumber = getCheckNumber(frame);
                            if (checkNumber >= 0) {
                                transactionAmount = getTransAmt(frame);
                                if (transactionAmount > 0) {
                                    account.setTransCount();
                                    processCheck(fmt, frame, transactionCode, transactionAmount, checkNumber, account);
                                    pendingSave = true;
                                }
                            }
                        } else if (transactionCode == DEPOSIT_ID) {
                            DepositPanel depositPanel = new DepositPanel(frame);
                            JOptionPane.showOptionDialog(frame, depositPanel, "Deposit Window", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
                            account.setTransCount();
                            double cashAmount = Double.parseDouble(depositPanel.getCashAmount());
                            double checkAmount = Double.parseDouble(depositPanel.getCheckAmount());
                            if (cashAmount > 0.00 || checkAmount > 0.00) {
                                processDeposit(fmt, frame, transactionCode, cashAmount, checkAmount, account);
                                pendingSave = true;
                            }
                        }
                    }
                }
                frame.setVisible(true);
            }
        }
    }

    public static void openHandler() {
        try {
            int option = JOptionPane.showConfirmDialog(new JFrame(), "Would you like to use the current default file: \n" + fileName, "Select an Option", JOptionPane.YES_NO_CANCEL_OPTION);
            String inFile = null;
            if(option == JOptionPane.YES_OPTION) {
                inFile = fileName;
            } else if(option == JOptionPane.NO_OPTION) {
                FileDialog fileDialog = new FileDialog(new JFrame(), "Load Account", FileDialog.LOAD);
                fileDialog.setVisible(true);
                inFile = fileDialog.getFile();
            }
            if (inFile != null) {
                loadFromFile(inFile);
            }
        } catch(Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public static void saveHandler() {
        if(pendingSave) {
            int option = JOptionPane.showConfirmDialog(new JFrame(), "Would you like to use the current default file: \n" + fileName, "Select an Option", JOptionPane.YES_NO_CANCEL_OPTION);
            if(option == JOptionPane.YES_OPTION) {
                saveToFile(fileName);
            } else if(option == JOptionPane.NO_OPTION) {
                FileDialog fileDialog = new FileDialog(new JFrame(), "Save", FileDialog.SAVE);
                fileDialog.setVisible(true);
                String selectedFile = fileDialog.getFile();
                if (selectedFile != null) {
                    saveToFile(selectedFile);
                }
            }
        }
    }

    public static void loadFromFile(String fileName) {
        try {
            FileInputStream fileInputStream = new FileInputStream(fileName);
            ObjectInputStream inFile = new ObjectInputStream(fileInputStream);
            checkingAccountList = (Vector<CheckingAccount>) inFile.readObject();
            inFile.close();
            if (!checkingAccountList.isEmpty()) {
                account = checkingAccountList.get(0); // Get the first account from the list
                System.out.println(account);
            } else {
                System.out.println("No account found in the file.");
            }
            pendingSave = false;

        } catch (ClassNotFoundException | IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public static void saveToFile(String fileName)  {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(fileName);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(checkingAccountList);
            objectOutputStream.close();
            pendingSave = false;
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public static String getAccountName(JFrame frame) {
        while(true) {
            String input = JOptionPane.showInputDialog(
                    frame, "Enter the account name (cannot be blank):", "Input", JOptionPane.QUESTION_MESSAGE);
            if(input == null) {
                return null;
            }
            else if(!input.trim().isEmpty()){
                return input;
            }
        }
    }

    public static String getInitialBalance(JFrame frame) {
        while(true) {
            String input = JOptionPane.showInputDialog(frame, "Enter the initial amount (must be greater than 0):", "Input", JOptionPane.QUESTION_MESSAGE);
            if(input == null) {
                return null;
            } else if(!input.trim().isEmpty() && Double.parseDouble(input) > 0.00){
                return input;
            }
        }
    }

    public static int getTransCode(JFrame frame) {
        while(true) {
            String input = JOptionPane.showInputDialog(frame, "Enter your transaction code (0, 1, or 2):", "Input", JOptionPane.QUESTION_MESSAGE);
            if(input == null) {
                return -1;
            }
            try {
                int transactionCode = Integer.parseInt(input);
                if(transactionCode >= 0) {
                    return transactionCode;
                }
            } catch(NumberFormatException e) {
                JOptionPane.showMessageDialog(frame, "Please enter \n 0 to view balance \n 1 to write a check \n 2 to make a deposit.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static int getCheckNumber(JFrame frame) {
        while(true) {
            String input = JOptionPane.showInputDialog(frame, "Enter your check number (greater or equal to 0):", "Input", JOptionPane.QUESTION_MESSAGE);
            if(input == null ) {
                return -1;
            }
            try {
                int checkNumber = Integer.parseInt(input);
                if(checkNumber >= 0) {
                    return checkNumber;
                }
            } catch(NumberFormatException e) {
                JOptionPane.showMessageDialog(frame, "Please enter an amount greater than 0.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static double getTransAmt(JFrame frame) {
        while(true) {
            String input = JOptionPane.showInputDialog(frame, "Enter the transaction amount (greater than 0):", "Input", JOptionPane.QUESTION_MESSAGE);
            if(input == null) {
                return -1;
            }
            try {
                int transactionAmount = Integer.parseInt(input);
                if(transactionAmount > 0) {
                    return transactionAmount;
                }
            } catch(NumberFormatException e) {
                JOptionPane.showMessageDialog(frame, "Please enter an amount greater than 0.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static String computeCheckCharges(String message, CheckingAccount account) {
        account.setTotalServiceCharge(checkCharge);
        account.setTransCount();
        Transaction checkChargeTrans = new Transaction(account.getTransCount(), SERVICE_CHARGE_ID, checkCharge);
        account.addTrans(checkChargeTrans);
        message += "Service Charge: Check --- charge $0.15 \n";
        if (account.getBalance() < 500.00 && account.getNumberOfViolations() == 0) {
            account.setTotalServiceCharge(below500Charge);
            account.setTransCount();
            Transaction below500ChargeTrans = new Transaction(account.getTransCount(), SERVICE_CHARGE_ID, below500Charge);
            account.addTrans(below500ChargeTrans);
            message += "Service Charge: Below $500 --- charge $5.00\n";
            account.setNumberOfViolations();
        }
        if (account.getBalance() < minimumBalance) {
            message += "Warning: Balance below $50\n";
        }
        if (account.getBalance() < 0.00) {
            account.setTotalServiceCharge(belowZeroCharge);
            account.setTransCount();
            Transaction belowZeroChargeTrans = new Transaction(account.getTransCount(), SERVICE_CHARGE_ID, belowZeroCharge);
            account.addTrans(belowZeroChargeTrans);
            message += "Service Charge: Below $0 --- charge $10.00\n";
        }
        return message;
    }

    public static String computeDepositCharges(String message, CheckingAccount account) {
        account.setTotalServiceCharge(depositCharge);
        account.setTransCount();
        Transaction depositChargeTrans = new Transaction(account.getTransCount(), SERVICE_CHARGE_ID, depositCharge);
        account.addTrans(depositChargeTrans);
        message += "Service Charge: Deposit --- charge $0.10\n";
        return message;
    }

    public static String computeCharges(int transactionCode, CheckingAccount account) {
        String message = "";
        if (transactionCode == CHECK_ID) {
            message += computeCheckCharges(message, account);
        } else if (transactionCode == DEPOSIT_ID) {
            message += computeDepositCharges(message, account);
        }
        message += "Total Service Charge: " + fmt.format(account.getTotalServiceCharge()) + "\n";
        return message;
    }

    public static void processCheck(DecimalFormat fmt, JFrame frame, int transactionCode, double transactionAmount, int checkNumber, CheckingAccount account) {
        String message = "";
        Check newCheck = new Check(account.getTransCount(), CHECK_ID, transactionAmount, checkNumber);
        account.addCheck(newCheck);
        account.setBalance(transactionCode, transactionAmount);
        message += account.getName() + "'s Account\n";
        message += "Transaction: Check #" + checkNumber + " in Amount of " + fmt.format(transactionAmount) + "\n";
        message += "Current Balance: " + account.getFormatedBalance() + "\n";
        message += computeCharges(transactionCode, account);
        JOptionPane.showMessageDialog(frame, message, "Message", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void processDeposit(DecimalFormat fmt, JFrame frame, int transactionCode, double cashAmount, double checkAmount, CheckingAccount account) {
        String message = "";
        double transactionAmount = cashAmount + checkAmount;
        Deposit newDeposit = new Deposit(account.getTransCount(), DEPOSIT_ID, cashAmount, checkAmount);
        account.addDeposit(newDeposit);
        account.setBalance(transactionCode, transactionAmount);
        message += account.getName() + "'s Account\n";
        message += "Transaction: Deposit in Amount of " + fmt.format(transactionAmount) + "\n";
        message += "Current Balance: " + account.getFormatedBalance() + "\n";
        message += computeCharges(transactionCode, account);
        JOptionPane.showMessageDialog(frame, message, "Message", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void processCancel(DecimalFormat fmt, JFrame frame, CheckingAccount account) {
        String message = "";
        message += "Transaction: End\n" +
                "Current Balance: " + account.getFormatedBalance() + "\n" +
                "Total Service Charge: " + fmt.format(account.getTotalServiceCharge()) + "\n" +
                "Final Balance: " + account.getFinalBalance() + "\n";
        JOptionPane.showMessageDialog(frame, message, "Message", JOptionPane.INFORMATION_MESSAGE);
    }
}