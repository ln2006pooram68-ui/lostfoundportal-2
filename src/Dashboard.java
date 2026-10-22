
import javax.swing.*;
import java.awt.*;

public class Dashboard extends JFrame {

    private int userId;
    private String userName;
    private String userRole;

    public Dashboard(int userId, String userName, String userRole) {
        this.userId = userId;
        this.userName = userName;
        this.userRole = userRole;

        setTitle("Lost and Found Portal");
        setSize(450, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel welcomeLabel = new JLabel(
                "Welcome, " + userName + "!",
                SwingConstants.CENTER
        );

        JLabel roleLabel = new JLabel(
                "Role: " + userRole,
                SwingConstants.CENTER
        );

        JButton logoutButton = new JButton("Logout");

        logoutButton.addActionListener(e -> {
            dispose();
            new Login().setVisible(true);
        });

        JButton itemsButton = new JButton("Items Menu");

        itemsButton.addActionListener(e -> {
            new ItemMenuFrame(userId).setVisible(true);
        });

        JPanel panel = new JPanel(new GridLayout(5, 1, 10, 10));
        panel.setBorder(
                BorderFactory.createEmptyBorder(30, 30, 30, 30)
        );

        panel.add(welcomeLabel);
        panel.add(roleLabel);
        panel.add(itemsButton);
        panel.add(logoutButton);

       
if ("admin".equalsIgnoreCase(userRole)) {
    JButton adminButton = new JButton("Admin Panel");

    adminButton.addActionListener(e -> {
        new AdminVerificationFrame().setVisible(true);
    });

    panel.add(adminButton);
}

        add(panel);
    }
}
