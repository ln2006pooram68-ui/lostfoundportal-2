import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class Login extends JFrame {

private JTextField emailField = new JTextField(20);
private JPasswordField passwordField = new JPasswordField(20);

public Login() {
setTitle("Lost and Found Portal - Login");
setSize(420, 230);
setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
setLocationRelativeTo(null);

JPanel panel = new JPanel(new BorderLayout(10, 15));
panel.setBorder(
BorderFactory.createEmptyBorder(25, 25, 25, 25)
);

JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 12));

formPanel.add(new JLabel("Email:"));
formPanel.add(emailField);

formPanel.add(new JLabel("Password:"));
formPanel.add(passwordField);

JButton loginButton = new JButton("Login");
JButton registerButton = new JButton("Register");

JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0));
buttonPanel.add(loginButton);
buttonPanel.add(registerButton);

panel.add(formPanel, BorderLayout.CENTER);
panel.add(buttonPanel, BorderLayout.SOUTH);

add(panel);

// Login button listener
loginButton.addActionListener(e -> loginUser());

// Register button listener
registerButton.addActionListener(e -> {
new Register().setVisible(true);
dispose();
});
}

private void loginUser() {
String email = emailField.getText().trim();
String password = new String(passwordField.getPassword());

if (email.isEmpty() || password.isEmpty()) {
JOptionPane.showMessageDialog(
this, "Please fill in all fields."
);
return;
}

String sql = "SELECT id, name, role FROM users "
+ "WHERE email = ? AND password_hash = ?";

try (Connection con =
DBConnection.getInstance().getConnection();
PreparedStatement ps = con.prepareStatement(sql)) {

ps.setString(1, email);
ps.setString(2, password);

try (ResultSet rs = ps.executeQuery()) {
if (rs.next()) {
int userId = rs.getInt("id");
String userName = rs.getString("name");
String userRole = rs.getString("role");

JOptionPane.showMessageDialog(
this, "Login successful!"
);

new Dashboard(
userId, userName, userRole
).setVisible(true);

dispose();
} else {
JOptionPane.showMessageDialog(
this, "Invalid email or password."
);
}
}

} catch (SQLException ex) {
JOptionPane.showMessageDialog(
this, "Database error: " + ex.getMessage()
);
}
}


public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        AdminSetup.createAdmin();
        new Login().setVisible(true);
    });
}

}


