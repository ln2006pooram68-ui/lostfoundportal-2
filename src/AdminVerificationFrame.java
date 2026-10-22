
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class AdminVerificationFrame extends JFrame {

    private JTable table;
    private DefaultTableModel model;

    private final VerificationDAO dao = new VerificationDAO();

    private static final double MIN_SCORE = 60.0;
    private static final int MIN_VOTES = 3;

    public AdminVerificationFrame() {
        setTitle("Admin - Listing Verification");
        setSize(950, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel heading = new JLabel(
            "Low Credibility Listings",
            SwingConstants.CENTER
        );
        heading.setFont(new Font("Arial", Font.BOLD, 20));
        add(heading, BorderLayout.NORTH);

        model = new DefaultTableModel(
            new String[]{
                "ID", "Item Name", "Location", "Type",
                "Genuine Votes", "Suspicious Votes",
                "Score (%)", "Status"
            }, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JButton refresh = new JButton("Refresh");
        JButton approve = new JButton("Approve");
        JButton reject = new JButton("Reject");

        JPanel panel = new JPanel();
        panel.add(refresh);
        panel.add(approve);
        panel.add(reject);
        add(panel, BorderLayout.SOUTH);

        refresh.addActionListener(e -> loadItems());
        approve.addActionListener(e -> updateStatus("APPROVED"));
        reject.addActionListener(e -> updateStatus("REJECTED"));

        loadItems();
    }

    private void loadItems() {
        model.setRowCount(0);

        String sql =
            "SELECT i.id, i.name, i.location, i.type, i.status, " +
            "COUNT(v.item_id) AS total_votes, " +
            "COUNT(CASE WHEN v.vote = 'GENUINE' THEN 1 END) AS genuine, " +
            "COUNT(CASE WHEN v.vote = 'SUSPICIOUS' THEN 1 END) AS suspicious " +
            "FROM items i " +
            "JOIN verification_votes v ON i.id = v.item_id " +
            "WHERE UPPER(i.status) = 'PENDING' " +
            "GROUP BY i.id, i.name, i.location, i.type, i.status " +
            "HAVING COUNT(v.item_id) >= ? " +
            "AND (100.0 * COUNT(CASE WHEN v.vote = 'GENUINE' THEN 1 END) " +
            "/ COUNT(v.item_id)) < ?";

        try (Connection con =
                 DBConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, MIN_VOTES);
            ps.setDouble(2, MIN_SCORE);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int itemId = rs.getInt("id");
                    double score = dao.getCredibilityScore(itemId);

                    model.addRow(new Object[]{
                        itemId,
                        rs.getString("name"),
                        rs.getString("location"),
                        rs.getString("type"),
                        rs.getInt("genuine"),
                        rs.getInt("suspicious"),
                        String.format("%.1f", score),
                        rs.getString("status")
                    });
                }
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                this,
                "Database error: " + ex.getMessage()
            );
        }
    }

    private void updateStatus(String status) {
        int row = table.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(
                this, "Please select an item first."
            );
            return;
        }

        int id = ((Number) model.getValueAt(row, 0)).intValue();

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Confirm " + status + " for item ID " + id + "?",
            "Confirm Action",
            JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        String sql =
            "UPDATE items SET status = ? " +
            "WHERE id = ? AND UPPER(status) = 'PENDING'";

        try (Connection con =
                 DBConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, id);

            if (ps.executeUpdate() > 0) {
                JOptionPane.showMessageDialog(
                    this, "Listing updated: " + status
                );
                loadItems();
            } else {
                JOptionPane.showMessageDialog(
                    this, "Listing status was not changed."
                );
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                this,
                "Could not update listing: " + ex.getMessage()
            );
        }
        ex.printStackTrace();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
            new AdminVerificationFrame().setVisible(true)
        );
    }
}
