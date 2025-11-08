import java.sql.Connection;

public class TestDB {
    public static void main(String[] args) {
        Connection con = DBConnection.getConnection();
        if (con != null) {
            System.out.println("✅ Connected to bankdb successfully!");
        } else {
            System.out.println("❌ Connection failed!");
        }
    }
}
