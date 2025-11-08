import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Account {
    private String name;
    private String accountNo;
    private double balance;

    public Account(String name, String accountNo, double balance) {
        this.name = name;
        this.accountNo = accountNo;
        this.balance = balance;
    }

    public String getName() { return name; }
    public String getAccountNo() { return accountNo; }
    public double getBalance() { return balance; }

    // ---------- DATABASE METHODS ----------
    public void deposit(double amount) {
        balance += amount;
        updateBalanceInDB();
        saveTransaction("Deposit", amount);
    }

    public boolean withdraw(double amount) {
        if (amount > balance) return false;
        balance -= amount;
        updateBalanceInDB();
        saveTransaction("Withdraw", amount);
        return true;
    }

    private void updateBalanceInDB() {
        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement(
                    "UPDATE accounts SET balance = ? WHERE account_no = ?"
            );
            ps.setDouble(1, balance);
            ps.setString(2, accountNo);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void saveTransaction(String type, double amount) {
        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO transactions (account_no, type, amount, balance_after) VALUES (?, ?, ?, ?)"
            );
            ps.setString(1, accountNo);
            ps.setString(2, type);
            ps.setDouble(3, amount);
            ps.setDouble(4, balance);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<String> getTransactionHistory() {
        List<String> history = new ArrayList<>();
        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement(
                    "SELECT * FROM transactions WHERE account_no = ? ORDER BY timestamp DESC"
            );
            ps.setString(1, accountNo);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String record = String.format("%s - %s ₹%.2f | Bal: ₹%.2f",
                        rs.getTimestamp("timestamp"),
                        rs.getString("type"),
                        rs.getDouble("amount"),
                        rs.getDouble("balance_after"));
                history.add(record);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return history;
    }

    // ---------- ADD ACCOUNT FEATURE ----------
    public static boolean addNewAccount(String customerName, String accountNo, String password, String type, double balance) {
        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO accounts (name, account_no, password, type, balance) VALUES (?, ?, ?, ?, ?)"
            );
            ps.setString(1, customerName);
            ps.setString(2, accountNo);
            ps.setString(3, password);
            ps.setString(4, type);
            ps.setDouble(5, balance);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
