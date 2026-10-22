
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AdminSetup {

    public static void createAdmin() {
        String sql =
            "INSERT OR IGNORE INTO users " +
            "(name, email, password_hash, role) " +
            "VALUES (?, ?, ?, ?)";

        try (Connection con =
                 DBConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, "Project Admin");
            ps.setString(2, "admin@lostfound.com");
            ps.setString(3, "AdminDemo123");
            ps.setString(4, "admin");

            ps.executeUpdate();

            System.out.println("Shared admin account is ready.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

