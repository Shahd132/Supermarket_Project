import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.List;

public class CustomerDashboard extends JFrame {
    private Customer customer;
    private JTable productTable;
    private ProductDAO productDAO;

    public CustomerDashboard(Customer customer) {
        this.customer = customer;
        setTitle("Customer Dashboard");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        productDAO = new ProductDAO();

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(245, 255, 245));

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(new Color(0, 102, 0));

        JLabel title = new JLabel("Customer Dashboard", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 28));
        title.setForeground(Color.WHITE);
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        titlePanel.add(title, BorderLayout.CENTER);

        JButton editProfileBtn = new JButton("Edit Profile");
        editProfileBtn.setFont(new Font("Arial", Font.PLAIN, 14));
        editProfileBtn.setBackground(Color.WHITE);
        editProfileBtn.setForeground(new Color(0, 102, 0));
        editProfileBtn.setFocusPainted(false);
        editProfileBtn.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        editProfileBtn.addActionListener(e -> openProfileEditor());
        titlePanel.add(editProfileBtn, BorderLayout.EAST);

        productTable = loadProductTable();
        JScrollPane tableScrollPane = new JScrollPane(productTable);
        tableScrollPane.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        tableScrollPane.getViewport().setBackground(new Color(245, 255, 245));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(245, 255, 245));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));

        JButton viewBtn = createDashboardButton("View Product", e -> onViewProduct());
        JButton orderBtn = createDashboardButton("Place Order", e -> onPlaceOrder());

        buttonPanel.add(viewBtn);
        buttonPanel.add(orderBtn);

        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(tableScrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }

    private JTable loadProductTable() {
        String[] columnNames = {"ID", "Name", "Category", "Price", "Quantity"};
        Object[][] data = {};

        try {
            List<Product> products = productDAO.getAllProducts();
            data = new Object[products.size()][5];
            for (int i = 0; i < products.size(); i++) {
                Product p = products.get(i);
                data[i][0] = p.getId();
                data[i][1] = p.getName();
                data[i][2] = p.getCategoryName();
                data[i][3] = p.getPrice();
                data[i][4] = p.getQuantity();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to load products: " + e.getMessage());
        }

        JTable table = new JTable(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setRowHeight(25);
        return table;
    }

    private JButton createDashboardButton(String text, ActionListener listener) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setBackground(new Color(34, 139, 34));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(true);
        button.setBorder(null);
        button.setPreferredSize(new Dimension(200, 40));
        button.addActionListener(listener);
        return button;
    }

    private void incrementView(int productId, int customerId) {
        try (Connection conn = DriverManager.getConnection(
                "jdbc:sqlserver://localhost:1433;databaseName=Supermarket;integratedSecurity=true;encrypt=true;trustServerCertificate=true")) {

            String checkSql = "SELECT TIMES_VIEWED FROM VIEWS WHERE PRODUCTID = ? AND CUSTOMER_ID = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setInt(1, productId);
                checkStmt.setInt(2, customerId);
                ResultSet rs = checkStmt.executeQuery();

                if (rs.next()) {
                    int timesViewed = rs.getInt("TIMES_VIEWED") + 1;
                    String updateSql = "UPDATE VIEWS SET TIMES_VIEWED = ? WHERE PRODUCTID = ? AND CUSTOMER_ID = ?";
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                        updateStmt.setInt(1, timesViewed);
                        updateStmt.setInt(2, productId);
                        updateStmt.setInt(3, customerId);
                        updateStmt.executeUpdate();
                    }
                } else {
                    String insertSql = "INSERT INTO VIEWS (PRODUCTID, CUSTOMER_ID, TIMES_VIEWED) VALUES (?, ?, 1)";
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                        insertStmt.setInt(1, productId);
                        insertStmt.setInt(2, customerId);
                        insertStmt.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error recording view: " + e.getMessage());
        }
    }

    private void onViewProduct() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a product to view.");
            return;
        }

        int productId = (int) productTable.getValueAt(selectedRow, 0);
        int customerId = customer.getId();
        incrementView(productId, customerId);

        String name = (String) productTable.getValueAt(selectedRow, 1);
        String category = (String) productTable.getValueAt(selectedRow, 2);
        double price = (double) productTable.getValueAt(selectedRow, 3);
        int quantity = (int) productTable.getValueAt(selectedRow, 4);

        JOptionPane.showMessageDialog(this, String.format("""
            Product Details:
            Name: %s
            Category: %s
            Price: %.2f
            Quantity: %d
        """, name, category, price, quantity));
    }

    private void onPlaceOrder() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a product to order.");
            return;
        }

        JOptionPane.showMessageDialog(this, "Order placed successfully!");
    }

    private void openProfileEditor() {
        JDialog dialog = new JDialog(this, "Edit Profile", true);
        dialog.setSize(400, 300);
        dialog.setLayout(new GridBagLayout());
        dialog.setLocationRelativeTo(this);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField nameField = new JTextField(customer.getName(), 20);
        JTextField emailField = new JTextField(customer.getEmail(), 20);
        JTextField phoneField = new JTextField(customer.getPhoneNo(), 20);
        JTextField addressField = new JTextField(customer.getAddress(), 20);

        gbc.gridx = 0; gbc.gridy = 0;
        dialog.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        dialog.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy++;
        dialog.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        dialog.add(emailField, gbc);

        gbc.gridx = 0; gbc.gridy++;
        dialog.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1;
        dialog.add(phoneField, gbc);

        gbc.gridx = 0; gbc.gridy++;
        dialog.add(new JLabel("Address:"), gbc);
        gbc.gridx = 1;
        dialog.add(addressField, gbc);

        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("Save");
        JButton deleteButton = new JButton("Delete Profile");

        saveButton.addActionListener(e -> {
            customer.setName(nameField.getText());
            customer.setEmail(emailField.getText());
            customer.setPhone(phoneField.getText());
            customer.setAddress(addressField.getText());

            String sqlUpdate = "UPDATE Customers SET Name = ?, Email = ?, Phone = ?, Address = ? WHERE CustomerID = ?";
            try (Connection conn = DriverManager.getConnection(
                    "jdbc:sqlserver://localhost:1433;databaseName=Supermarket;integratedSecurity=true;encrypt=true;trustServerCertificate=true")) {

                try (PreparedStatement stmt = conn.prepareStatement(sqlUpdate)) {
                    stmt.setString(1, customer.getName());
                    stmt.setString(2, customer.getEmail());
                    stmt.setString(3, customer.getPhoneNo());
                    stmt.setString(4, customer.getAddress());
                    stmt.setInt(5, customer.getId());
                    int rowsUpdated = stmt.executeUpdate();
                    JOptionPane.showMessageDialog(dialog, rowsUpdated > 0 ? "Profile updated successfully!" : "Failed to update profile.");
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Error updating profile: " + ex.getMessage());
            }
            dialog.dispose();
        });

        deleteButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(dialog, "Are you sure you want to delete your profile?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                String sqlDelete = "DELETE FROM Customers WHERE CustomerID = ?";
                try (Connection conn = DriverManager.getConnection(
                        "jdbc:sqlserver://localhost:1433;databaseName=Supermarket;integratedSecurity=true;encrypt=true;trustServerCertificate=true")) {

                    try (PreparedStatement stmt = conn.prepareStatement(sqlDelete)) {
                        stmt.setInt(1, customer.getId());
                        int rowsDeleted = stmt.executeUpdate();
                        if (rowsDeleted > 0) {
                            JOptionPane.showMessageDialog(dialog, "Profile deleted successfully!");
                            dialog.dispose();
                            dispose();
                        } else {
                            JOptionPane.showMessageDialog(dialog, "Failed to delete profile.");
                        }
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(dialog, "Error deleting profile: " + ex.getMessage());
                }
            }
        });

        buttonPanel.add(saveButton);
        buttonPanel.add(deleteButton);

        gbc.gridx = 0; gbc.gridy++;
        gbc.gridwidth = 2;
        dialog.add(buttonPanel, gbc);

        dialog.setVisible(true);
    }
}
