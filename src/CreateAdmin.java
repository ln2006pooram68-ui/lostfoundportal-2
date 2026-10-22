
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class CreateAdmin {

    public static void main(String[] args) {

        String email = "admin@lostfound.com";
        String password = "Admin123";

        String checkSql = "SELECT id FROM users WHERE email = ?";
        String insertSql =
            "INSERT INTO users (name, email, password_hash, role) " +
            "VALUES (?, ?, ?, ?)";

        try (Connection con =
                DBConnection.getInstance().getConnection()) {

            try (PreparedStatement check =
                    con.prepareStatement(checkSql)) {

                check.setString(1, email);

                try (ResultSet rs = check.executeQuery()) {
                    if (rs.next()) {
                        System.out.println(
                            "This email already exists. No new admin created."
                        );
                        return;
                    }
                }
            }

            try (PreparedStatement ps =
                    con.prepareStatement(insertSql)) {

                ps.setString(1, "Admin");
                ps.setString(2, email);
                ps.setString(3, password);
                ps.setString(4, "admin");

                ps.executeUpdate();

                System.out.println("Admin account created successfully!");
                System.out.println("Email: " + email);
                System.out.println("Password: " + password);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
