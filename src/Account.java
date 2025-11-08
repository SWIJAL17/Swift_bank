import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Account {
    private String accountNo;
    private String name;
    private double balance;
    private String password;
    private String type;
    private final List<String> transactionHistory = new ArrayList<>();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

    public Account(String name, String accountNo, double balance) {
        this.name = name;
        this.accountNo = accountNo;
        this.balance = balance;
        transactionHistory.add(timestamp() + " | Account created with balance ₹" + balance);
    }

    public Account(String name, String accountNo, String password, String type, double balance) {
        this.name = name;
        this.accountNo = accountNo;
        this.password = password;
        this.type = type;
        this.balance = balance;
        transactionHistory.add(timestamp() + " | Account created with balance ₹" + balance);
    }

    // 🕓 Helper to get timestamp
    private String timestamp() {
        return "[" + dateFormat.format(new Date()) + "]";
    }

    // 💵 Deposit
    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
            transactionHistory.add(timestamp() + " ✅ Deposited ₹" + amount + " | New Balance: ₹" + balance);
        }
    }

    // 💳 Withdraw
    public boolean withdraw(double amount) {
        if (amount > 0 && amount <= balance) {
            balance -= amount;
            transactionHistory.add(timestamp() + " ⚠️ Withdrew ₹" + amount + " | New Balance: ₹" + balance);
            return true;
        } else {
            transactionHistory.add(timestamp() + " ❌ Failed Withdrawal ₹" + amount + " | Insufficient Balance");
        }
        return false;
    }

    // 🧾 Get Transaction History
    public List<String> getTransactionHistory() {
        return transactionHistory;
    }

    // 👤 Getters
    public String getName() { return name; }
    public String getAccountNo() { return accountNo; }
    public double getBalance() { return balance; }
    public String getType() { return type; }

    // ➕ Mock DB Method
    public static boolean addNewAccount(String name, String accNo, String password, String type, double balance) {
        System.out.println("✅ New account created for " + name + " (" + accNo + ")");
        return true;
    }
}
