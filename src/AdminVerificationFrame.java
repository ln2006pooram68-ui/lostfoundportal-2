
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Vector;

public class AdminVerificationFrame extends JFrame {

    private JTable itemTable;
    private DefaultTableModel tableModel;

    private ItemSearchDAO searchDAO;
    private ItemDAO itemDAO;

    public AdminVerificationFrame() {

        searchDAO = new ItemSearchDAO();
        itemDAO = new ItemDAO();

        setTitle("Admin - Item Verification");
        setSize(1000, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JLabel heading = new JLabel(
                "Low-Credibility Items - Admin Review",
                SwingConstants.CENTER
        );

        heading.setFont(new Font("Arial", Font.BOLD, 20));

        tableModel = new DefaultTableModel(
                new String[]{
                    "ID", "Name", "Description", "Location",
                    "Date", "Type", "Status", "Votes", "Credibility %"
                }, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        itemTable = new JTable(tableModel);
        itemTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        JScrollPane scrollPane = new JScrollPane(itemTable);

        JButton approveButton = new JButton("Approve");
        JButton rejectButton = new JButton("Reject");
        JButton refreshButton = new JButton("Refresh");

        approveButton.addActionListener(e -> approveSelectedItem());
        rejectButton.addActionListener(e -> rejectSelectedItem());
        refreshButton.addActionListener(e -> loadFlaggedItems());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(approveButton);
        buttonPanel.add(rejectButton);
        buttonPanel.add(refreshButton);

        setLayout(new BorderLayout(10, 10));
        add(heading, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        loadFlaggedItems();
    }

    private void loadFlaggedItems() {

        tableModel.setRowCount(0);

        Vector<Vector<Object>> items = searchDAO.getFlaggedItems();

        for (Vector<Object> item : items) {
            tableModel.addRow(item);
        }

        if (items.isEmpty()) {
            setTitle("Admin - Item Verification (No flagged items)");
        } else {
            setTitle("Admin - Item Verification (" +
                    items.size() + " flagged items)");
        }
    }

    private int getSelectedItemId() {

        int selectedRow = itemTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select an item first."
            );
            return -1;
        }

        return Integer.parseInt(
                tableModel.getValueAt(selectedRow, 0).toString()
        );
    }

    private void approveSelectedItem() {

        int itemId = getSelectedItemId();

        if (itemId == -1) {
            return;
        }

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to approve this item?",
                "Confirm Approval",
                JOptionPane.YES_NO_OPTION
        );

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        if (itemDAO.approveItem(itemId)) {
            JOptionPane.showMessageDialog(this, "Item approved.");
            loadFlaggedItems();
        } else {
            JOptionPane.showMessageDialog(
                    this, "Could not approve the item."
            );
        }
    }

    private void rejectSelectedItem() {

        int itemId = getSelectedItemId();

        if (itemId == -1) {
            return;
        }

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to reject this item?",
                "Confirm Rejection",
                JOptionPane.YES_NO_OPTION
        );

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        if (itemDAO.rejectItem(itemId)) {
            JOptionPane.showMessageDialog(this, "Item rejected.");
            loadFlaggedItems();
        } else {
            JOptionPane.showMessageDialog(
                    this, "Could not reject the item."
            );
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
                new AdminVerificationFrame().setVisible(true)
        );
    }
}
