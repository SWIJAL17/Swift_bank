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

    public static Account login(String accNo, String password) {
        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement(
                    "SELECT * FROM accounts WHERE account_no = ? AND password = ?");
            ps.setString(1, accNo);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Account(rs.getString("name"), rs.getString("account_no"), rs.getDouble("balance"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void deposit(double amount) {
        balance += amount;
        updateBalance();
        saveTransaction("Deposit", amount);
    }

    public boolean withdraw(double amount) {
        if (amount > balance) return false;
        balance -= amount;
        updateBalance();
        saveTransaction("Withdraw", amount);
        return true;
    }

    private void updateBalance() {
        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement(
                    "UPDATE accounts SET balance = ? WHERE account_no = ?");
            ps.setDouble(1, balance);
            ps.setString(2, accountNo);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveTransaction(String type, double amount) {
        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO transactions (account_no, type, amount, balance_after) VALUES (?, ?, ?, ?)");
            ps.setString(1, accountNo);
            ps.setString(2, type);
            ps.setDouble(3, amount);
            ps.setDouble(4, balance);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<String> getTransactionHistory() {
        List<String> history = new ArrayList<>();
        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement(
                    "SELECT * FROM transactions WHERE account_no = ? ORDER BY timestamp DESC");
            ps.setString(1, accountNo);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String record = rs.getTimestamp("timestamp") + " - " +
                        rs.getString("type") + " ₹" + rs.getDouble("amount") +
                        " | Bal: ₹" + rs.getDouble("balance_after");
                history.add(record);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return history;
    }
    public static Account loadAccount(Connection con, int customerId) {
        try {
            PreparedStatement ps = con.prepareStatement(
                    "SELECT a.account_no, a.balance, c.name " +
                            "FROM accounts a JOIN customers c ON a.customer_id = c.id " +
                            "WHERE c.id = ? LIMIT 1");
            ps.setInt(1, customerId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String name = rs.getString("name");
                String accNo = rs.getString("account_no");
                double balance = rs.getDouble("balance");
                return new Account(name, accNo, balance);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
