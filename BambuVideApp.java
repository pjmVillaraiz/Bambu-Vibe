import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import java.util.regex.PatternSyntaxException;

public class BambuVideApp extends JFrame {

    private JTextField itemNameField;
    private JTextField itemQuantityField;
    private JTextField itemPriceField;
    private DefaultTableModel tableModel;
    private JTable inventoryTable;
    private JLabel totalQuantityLabel;

    private JTextField searchField;
    private TableRowSorter<DefaultTableModel> sorter;

    public BambuVideApp() {
        setTitle("Bambu Vide - Inventory Management");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Image bambooBackground = new ImageIcon("resources/bamboo_texture.jpg").getImage();
                g.drawImage(bambooBackground, 0, 0, getWidth(), getHeight(), this);
            }
        };
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBackground(new Color(139, 69, 19));

        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(34, 139, 34), 2),
                "Add/Update Item", 0, 0, new Font("Serif", Font.BOLD, 16), new Color(34, 139, 34)));
        inputPanel.setBackground(new Color(222, 184, 135));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(new JLabel("Item Name:"), gbc);
        gbc.gridx = 1;
        itemNameField = new JTextField(20);
        inputPanel.add(itemNameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        inputPanel.add(new JLabel("Quantity:"), gbc);
        gbc.gridx = 1;
        itemQuantityField = new JTextField(20);
        inputPanel.add(itemQuantityField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        inputPanel.add(new JLabel("Price (₱):"), gbc);
        gbc.gridx = 1;
        itemPriceField = new JTextField(20);
        inputPanel.add(itemPriceField, gbc);

        JPanel crudButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        crudButtonPanel.setBackground(new Color(222, 184, 135));
        JButton addButton = new JButton("Add Item");
        JButton updateButton = new JButton("Update Item");
        JButton clearButton = new JButton("Clear Fields");

        addButton.setIcon(new ImageIcon("resources/bamboo_button.png"));
        updateButton.setIcon(new ImageIcon("resources/bamboo_button.png"));
        clearButton.setIcon(new ImageIcon("resources/bamboo_button.png"));

        addButton.setHorizontalTextPosition(SwingConstants.CENTER);
        addButton.setVerticalTextPosition(SwingConstants.CENTER);
        updateButton.setHorizontalTextPosition(SwingConstants.CENTER);
        updateButton.setVerticalTextPosition(SwingConstants.CENTER);
        clearButton.setHorizontalTextPosition(SwingConstants.CENTER);
        clearButton.setVerticalTextPosition(SwingConstants.CENTER);

        crudButtonPanel.add(addButton);
        crudButtonPanel.add(updateButton);
        crudButtonPanel.add(clearButton);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        inputPanel.add(crudButtonPanel, gbc);

        topPanel.add(inputPanel, BorderLayout.CENTER);

        JPanel searchPanel = new JPanel(new GridBagLayout());
        searchPanel
                .setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(34, 139, 34), 2),
                        "Search Inventory", 0, 0, new Font("Serif", Font.BOLD, 16), new Color(34, 139, 34)));
        searchPanel.setBackground(new Color(222, 184, 135));
        GridBagConstraints searchGbc = new GridBagConstraints();
        searchGbc.insets = new Insets(5, 5, 5, 5);
        searchGbc.fill = GridBagConstraints.HORIZONTAL;

        searchGbc.gridx = 0;
        searchGbc.gridy = 0;
        searchPanel.add(new JLabel("Search Item Name:"), searchGbc);
        searchGbc.gridx = 1;
        searchField = new JTextField(15);
        searchPanel.add(searchField, searchGbc);

        searchGbc.gridx = 0;
        searchGbc.gridy = 1;
        searchGbc.gridwidth = 2;
        JButton searchButton = new JButton("Search");
        searchPanel.add(searchButton, searchGbc);

        searchGbc.gridx = 0;
        searchGbc.gridy = 2;
        JButton clearSearchButton = new JButton("Clear Search");
        searchPanel.add(clearSearchButton, searchGbc);

        topPanel.add(searchPanel, BorderLayout.EAST);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        String[] columnNames = { "Item Name", "Quantity", "Price (₱)" };
        tableModel = new DefaultTableModel(columnNames, 0);
        inventoryTable = new JTable(tableModel);
        sorter = new TableRowSorter<>(tableModel);
        inventoryTable.setRowSorter(sorter);
        JScrollPane scrollPane = new JScrollPane(inventoryTable);

        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout(10, 0));
        bottomPanel.setBackground(new Color(139, 69, 19));

        JPanel actionButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actionButtonPanel.setBackground(new Color(222, 184, 135));
        JButton deleteButton = new JButton("Delete Selected Item");
        JButton increaseQuantityButton = new JButton("Increase Quantity (+1)");
        JButton decreaseQuantityButton = new JButton("Decrease Quantity (-1)");
        JButton generateReportButton = new JButton("Inventory Report");

        actionButtonPanel.add(deleteButton);
        actionButtonPanel.add(increaseQuantityButton);
        actionButtonPanel.add(decreaseQuantityButton);
        actionButtonPanel.add(generateReportButton);
        bottomPanel.add(actionButtonPanel, BorderLayout.WEST);

        JPanel totalQuantityPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        totalQuantityPanel.setBackground(new Color(222, 184, 135));
        totalQuantityLabel = new JLabel("Total Items Quantity: 0");
        totalQuantityLabel.setFont(new Font("Arial", Font.BOLD, 16));
        totalQuantityPanel.add(totalQuantityLabel);
        bottomPanel.add(totalQuantityPanel, BorderLayout.EAST);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);

        updateTotalQuantity();
    }

    private void addItem() {
        String name = itemNameField.getText().trim();
        String quantityStr = itemQuantityField.getText().trim();
        String priceStr = itemPriceField.getText().trim();

        if (name.isEmpty() || quantityStr.isEmpty() || priceStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int quantity = Integer.parseInt(quantityStr);
            double price = Double.parseDouble(priceStr);

            if (quantity < 0 || price < 0) {
                JOptionPane.showMessageDialog(this, "Quantity and Price cannot be negative.", "Input Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            int existingRow = -1;
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                if (tableModel.getValueAt(i, 0).toString().equalsIgnoreCase(name)) {
                    existingRow = i;
                    break;
                }
            }

            if (existingRow != -1) {
                int option = JOptionPane.showConfirmDialog(this,
                        "Item '" + name + "' already exists. Do you want to update its quantity and price?",
                        "Item Exists", JOptionPane.YES_NO_OPTION);
                if (option == JOptionPane.YES_OPTION) {
                    tableModel.setValueAt(quantity, existingRow, 1);
                    tableModel.setValueAt(String.format("%.2f", price), existingRow, 2);
                    JOptionPane.showMessageDialog(this, "Item updated successfully!", "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                Vector<Object> rowData = new Vector<>();
                rowData.add(name);
                rowData.add(quantity);
                rowData.add(String.format("%.2f", price));
                tableModel.addRow(rowData);
                JOptionPane.showMessageDialog(this, "Item added successfully!", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            }
            clearFields();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Quantity must be an integer and Price must be a valid number.",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateItem() {
        int selectedRowView = inventoryTable.getSelectedRow();
        if (selectedRowView == -1) {
            JOptionPane.showMessageDialog(this, "Please select an item from the table to update.", "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        int selectedModelRow = inventoryTable.convertRowIndexToModel(selectedRowView);

        String name = itemNameField.getText().trim();
        String quantityStr = itemQuantityField.getText().trim();
        String priceStr = itemPriceField.getText().trim();

        if (name.isEmpty() || quantityStr.isEmpty() || priceStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields for update.", "Input Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int quantity = Integer.parseInt(quantityStr);
            double price = Double.parseDouble(priceStr);

            if (quantity < 0 || price < 0) {
                JOptionPane.showMessageDialog(this, "Quantity and Price cannot be negative.", "Input Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            for (int i = 0; i < tableModel.getRowCount(); i++) {
                if (i != selectedModelRow && tableModel.getValueAt(i, 0).toString().equalsIgnoreCase(name)) {
                    JOptionPane.showMessageDialog(this, "An item with this name already exists.", "Duplicate Name",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            tableModel.setValueAt(name, selectedModelRow, 0);
            tableModel.setValueAt(quantity, selectedModelRow, 1);
            tableModel.setValueAt(String.format("%.2f", price), selectedModelRow, 2);
            JOptionPane.showMessageDialog(this, "Item updated successfully!", "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            clearFields();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Quantity must be an integer and Price must be a valid number.",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteItem() {
        int selectedRowView = inventoryTable.getSelectedRow();
        if (selectedRowView == -1) {
            JOptionPane.showMessageDialog(this, "Please select an item from the table to delete.", "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        int selectedModelRow = inventoryTable.convertRowIndexToModel(selectedRowView);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete the selected item?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            tableModel.removeRow(selectedModelRow);
            JOptionPane.showMessageDialog(this, "Item deleted successfully!", "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            clearFields();
        }
    }

    private void changeQuantity(int delta) {
        int selectedRowView = inventoryTable.getSelectedRow();
        if (selectedRowView == -1) {
            JOptionPane.showMessageDialog(this, "Please select an item to change its quantity.", "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        int selectedModelRow = inventoryTable.convertRowIndexToModel(selectedRowView);

        try {
            int currentQuantity = (int) tableModel.getValueAt(selectedModelRow, 1);
            int newQuantity = currentQuantity + delta;

            if (newQuantity < 0) {
                JOptionPane.showMessageDialog(this, "Quantity cannot be negative.", "Quantity Error",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            tableModel.setValueAt(newQuantity, selectedModelRow, 1);
            JOptionPane.showMessageDialog(this, "Quantity updated successfully!", "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            clearFields();
        } catch (ClassCastException ex) {
            JOptionPane.showMessageDialog(this, "Error reading quantity. It might not be a valid number.", "Data Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTotalQuantity() {
        int total = 0;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            try {
                total += (int) tableModel.getValueAt(i, 1);
            } catch (ClassCastException e) {
                System.err.println("Error: Quantity at row " + i + " is not an integer. " + e.getMessage());
            }
        }
        totalQuantityLabel.setText("Total Items Quantity: " + total);
    }

    private void filterTable() {
        String searchText = searchField.getText().trim();
        if (searchText.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            try {
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText, 0));
            } catch (PatternSyntaxException e) {
                JOptionPane.showMessageDialog(this, "Invalid search pattern: " + e.getMessage(), "Search Error",
                        JOptionPane.ERROR_MESSAGE);
                sorter.setRowFilter(null);
            }
        }
        clearFields();
    }

    private void generateLowStockReport() {
        String thresholdStr = JOptionPane.showInputDialog(this,
                "Enter the low stock quantity threshold:",
                "Low Stock Report Threshold",
                JOptionPane.QUESTION_MESSAGE);

        if (thresholdStr == null || thresholdStr.trim().isEmpty()) {
            return;
        }

        int threshold;
        try {
            threshold = Integer.parseInt(thresholdStr);
            if (threshold < 0) {
                JOptionPane.showMessageDialog(this, "Threshold cannot be negative.", "Input Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid threshold. Please enter a whole number.", "Input Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        StringBuilder reportContent = new StringBuilder();
        reportContent.append("--- Low Stock Items Report ---\n");
        reportContent.append("Threshold: ").append(threshold).append(" units\n\n");

        boolean foundLowStock = false;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            try {
                String itemName = tableModel.getValueAt(i, 0).toString();
                int quantity = (int) tableModel.getValueAt(i, 1);
                String price = tableModel.getValueAt(i, 2).toString();

                if (quantity <= threshold) {
                    reportContent.append("Item: ").append(itemName)
                            .append(", Quantity: ").append(quantity)
                            .append(", Price: ").append(price).append("\n");
                    foundLowStock = true;
                }
            } catch (ClassCastException e) {
                System.err.println("Error reading data for report at row " + i + ": " + e.getMessage());
            }
        }

        if (!foundLowStock) {
            reportContent.append("No items found below the threshold of ").append(threshold).append(" units.\n");
        }
        reportContent.append("\n------------------------------");

        JDialog reportDialog = new JDialog(this, "Low Stock Report - Bambu Vide", true);
        reportDialog.setSize(500, 400);
        reportDialog.setLocationRelativeTo(this);

        JTextArea reportTextArea = new JTextArea(reportContent.toString());
        reportTextArea.setEditable(false);
        reportTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane reportScrollPane = new JScrollPane(reportTextArea);

        reportDialog.add(reportScrollPane, BorderLayout.CENTER);

        JPanel reportButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton closeReportButton = new JButton("Close");
        closeReportButton.addActionListener(e -> reportDialog.dispose());
        reportButtonPanel.add(closeReportButton);
        reportDialog.add(reportButtonPanel, BorderLayout.SOUTH);

        reportDialog.setVisible(true);
    }

    private void clearFields() {
        itemNameField.setText("");
        itemQuantityField.setText("");
        itemPriceField.setText("");
        inventoryTable.clearSelection();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                BambuVideApp frame = new BambuVideApp();

                // Show registration dialog first
                RegistrationDialog registrationDialog = new RegistrationDialog(frame);
                registrationDialog.setVisible(true);

                if (registrationDialog.isRegistered()) {
                    // Optionally, you can pass the registered username/password to LoginDialog
                    RegistrationDialog loginDialog = new RegistrationDialog(frame);
                    loginDialog.setVisible(true);

                    if (loginDialog.isAuthenticated()) {
                        frame.setVisible(true);
                    } else {
                        System.exit(0);
                    }
                } else {
                    System.exit(0);
                }
            }
        });
    }
}