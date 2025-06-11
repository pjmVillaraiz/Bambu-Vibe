import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*; // Import for file operations
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;
import java.util.ArrayList; // For dynamic category list
import java.util.HashSet;   // For unique categories
import java.util.Vector;    // For table row data

public class BambuVibeApp extends JFrame {
    private DefaultTableModel tableModel;
    private JTable inventoryTable;
    private JTextField itemNameField;
    private JComboBox<String> itemCategoryField; // Changed to JComboBox
    private JTextField itemQuantityField;
    private JTextField itemPriceField;
    private JTextField searchField;
    private JLabel totalQuantityLabel;
    private TableRowSorter<DefaultTableModel> sorter;
    private JLabel statusBarLabel; // New status bar label

    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final String INVENTORY_FILE = "inventory.csv"; // CSV file for persistence

    // Constants for action messages/button texts
    private static final String ADD_ITEM_TEXT = "Add Item";
    private static final String UPDATE_ITEM_TEXT = "Update Item";
    private static final String DELETE_ITEM_TEXT = "Delete Item"; // New constant
    private static final String CLEAR_FIELDS_TEXT = "Clear Fields"; // Renamed from "Clear"
    private static final String GENERATE_REPORT_TEXT = "Generate Report";

    // UPDATED BAMBOO-INSPIRED COLOR PALETTE (Copied from previous response)
    private static final Color PRIMARY_COLOR = new Color(92, 148, 86);   // Darker Bamboo Green
    private static final Color SECONDARY_COLOR = new Color(220, 240, 210); // Lighter, Fresh Bamboo Green
    private static final Color BUTTON_COLOR = new Color(180, 150, 110);  // Earthy Tan/Brown
    private static final Color BORDER_COLOR = new Color(101, 67, 33);   // Deep Earthy Brown
    private static final Color TEXT_COLOR = new Color(50, 80, 45);     // Dark Forest Green
    private static final Font MAIN_FONT = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 14);

    public BambuVibeApp() {
        setTitle("Bambu Vibe Logistics");
        setSize(700, 650);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // Handle closing manually for saving
        setLocationRelativeTo(null);

        // Add window listener to save data on close
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                saveInventory();
                dispose(); // Close the frame after saving
            }
        });

        initializeTable();

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(SECONDARY_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(SECONDARY_COLOR);

        contentPanel.add(createInputPanel());
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        contentPanel.add(createSearchPanel());
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        contentPanel.add(createTablePanel());
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        contentPanel.add(createStatusBarPanel()); // Use the new status bar panel

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        add(mainPanel);

        setupTableSelectionListener();
        setupTablePopupMenu(); // Setup right-click menu
        setupSearch();

        loadInventory(); // Load data on startup
        updateTotalQuantity(); // Update total quantity after loading
    }

    private void initializeTable() {
        String[] columnNames = {"Item Name", "Category", "Quantity", "Price (₱)", "Date Added", "Date Updated"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int column) {
                switch (column) {
                    case 2: return Integer.class; // Quantity
                    case 3: return Double.class;  // Price
                    case 4:
                    case 5: return String.class; // Date Added, Date Updated
                    default: return String.class;
                }
            }
        };

        inventoryTable = new JTable(tableModel);
        sorter = new TableRowSorter<>(tableModel);
        inventoryTable.setRowSorter(sorter);

        inventoryTable.setRowHeight(30);
        inventoryTable.setFont(MAIN_FONT);
        inventoryTable.getTableHeader().setFont(HEADER_FONT);
        inventoryTable.getTableHeader().setForeground(TEXT_COLOR);
        inventoryTable.getTableHeader().setBackground(BUTTON_COLOR);
        inventoryTable.setSelectionBackground(PRIMARY_COLOR.darker());
        inventoryTable.setSelectionForeground(Color.WHITE);
        inventoryTable.setBackground(SECONDARY_COLOR);
        inventoryTable.setFillsViewportHeight(true);
        inventoryTable.setGridColor(BORDER_COLOR.brighter());
        inventoryTable.setShowGrid(true);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        inventoryTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer); // Quantity
        inventoryTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer); // Price
        inventoryTable.getColumnModel().getColumn(4).setCellRenderer(centerRenderer); // Date Added
        inventoryTable.getColumnModel().getColumn(5).setCellRenderer(centerRenderer); // Date Updated
    }

    private JPanel createInputPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(SECONDARY_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 2),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        itemNameField = createStyledTextField("e.g., Essential Oil");

        // Initial categories for the JComboBox
        String[] initialCategories = {"Wellness", "Home Goods", "Decor", "Personal Care", "Gardening", "Other"};
        itemCategoryField = createStyledComboBox(initialCategories);


        itemQuantityField = createStyledTextField("e.g., 25");
        itemPriceField = createStyledTextField("e.g., 1250.55");

        panel.add(createFieldWithLabel("Item Name:", itemNameField));
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
        panel.add(createFieldWithLabel("Category:", itemCategoryField)); // Use the JComboBox
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
        panel.add(createFieldWithLabel("Quantity:", itemQuantityField));
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
        panel.add(createFieldWithLabel("Price (₱):", itemPriceField));
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(createButtonPanel());

        return panel;
    }

    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 0));
        panel.setBackground(SECONDARY_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));

        searchField = createStyledTextField("Search items...");
        searchField.setFont(MAIN_FONT.deriveFont(Font.ITALIC));
        panel.add(searchField, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(SECONDARY_COLOR);
        panel.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 2));

        JScrollPane scrollPane = new JScrollPane(inventoryTable);
        scrollPane.setPreferredSize(new Dimension(0, 250));
        scrollPane.getViewport().setBackground(SECONDARY_COLOR);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createStatusBarPanel() { // Renamed from createTotalPanel
        JPanel panel = new JPanel(new BorderLayout(5, 0));
        panel.setBackground(SECONDARY_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        totalQuantityLabel = new JLabel("Total Items: 0");
        totalQuantityLabel.setFont(HEADER_FONT.deriveFont(Font.BOLD, 16f));
        totalQuantityLabel.setForeground(TEXT_COLOR);
        panel.add(totalQuantityLabel, BorderLayout.WEST); // Place total quantity to the left

        statusBarLabel = new JLabel("Ready."); // Initialize status bar message
        statusBarLabel.setFont(MAIN_FONT.deriveFont(Font.ITALIC));
        statusBarLabel.setForeground(TEXT_COLOR.darker());
        panel.add(statusBarLabel, BorderLayout.EAST); // Place status to the right

        return panel;
    }

    private void updateStatusBar(String message, Color color) {
        statusBarLabel.setText(message);
        statusBarLabel.setForeground(color);
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new GridLayout(5, 1, 5, 5)); // Increased grid rows
        buttonPanel.setOpaque(false);

        JButton addButton = createStyledButton(ADD_ITEM_TEXT);
        JButton updateButton = createStyledButton(UPDATE_ITEM_TEXT);
        JButton deleteButton = createStyledButton(DELETE_ITEM_TEXT); // New delete button
        JButton clearButton = createStyledButton(CLEAR_FIELDS_TEXT);
        JButton reportButton = createStyledButton(GENERATE_REPORT_TEXT);

        addButton.addActionListener(e -> addItem());
        updateButton.addActionListener(e -> updateItem());
        deleteButton.addActionListener(e -> deleteItem()); // Add action listener for delete
        clearButton.addActionListener(e -> clearFields());
        reportButton.addActionListener(e -> generateReport());

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton); // Add delete button to panel
        buttonPanel.add(clearButton);
        buttonPanel.add(reportButton);

        return buttonPanel;
    }

    private JTextField createStyledTextField(String placeholderText) {
        JTextField textField = new JTextField(20);
        textField.setFont(MAIN_FONT);
        textField.setForeground(TEXT_COLOR);
        textField.setBackground(Color.WHITE);
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR.darker(), 1),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        textField.setText(placeholderText);
        textField.setForeground(Color.GRAY);
        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (textField.getText().equals(placeholderText)) {
                    textField.setText("");
                    textField.setForeground(TEXT_COLOR);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (textField.getText().isEmpty()) {
                    textField.setText(placeholderText);
                    textField.setForeground(Color.GRAY);
                }
            }
        });
        return textField;
    }

    // New method to create styled JComboBox
    private JComboBox<String> createStyledComboBox(String[] items) {
        JComboBox<String> comboBox = new JComboBox<>(items);
        comboBox.setFont(MAIN_FONT);
        comboBox.setForeground(TEXT_COLOR);
        comboBox.setBackground(Color.WHITE);
        comboBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR.darker(), 1),
                BorderFactory.createEmptyBorder(3, 5, 3, 5) // Slightly less padding for dropdown
        ));
        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                label.setForeground(TEXT_COLOR); // Ensure text color is consistent in dropdown
                return label;
            }
        });
        return comboBox;
    }


    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(HEADER_FONT);
        button.setBackground(BUTTON_COLOR);
        button.setForeground(TEXT_COLOR.darker());
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(BUTTON_COLOR.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(BUTTON_COLOR);
            }
        });
        return button;
    }

    private JPanel createFieldWithLabel(String labelText, JComponent field) { // Changed field type to JComponent
        JPanel panel = new JPanel(new BorderLayout(5, 0));
        panel.setOpaque(false);
        JLabel label = new JLabel(labelText);
        label.setFont(MAIN_FONT.deriveFont(Font.BOLD));
        label.setForeground(TEXT_COLOR);
        panel.add(label, BorderLayout.WEST);
        panel.add(field, BorderLayout.CENTER); // Now accepts JTextField or JComboBox
        return panel;
    }

    private void addItem() {
        try {
            String itemName = itemNameField.getText().trim();
            // Get category from JComboBox
            String itemCategory = (String) itemCategoryField.getSelectedItem();

            if (itemName.isEmpty() || itemName.equals("e.g., Essential Oil")) {
                JOptionPane.showMessageDialog(this, "Please enter a valid item name.", "Input Error", JOptionPane.ERROR_MESSAGE);
                updateStatusBar("Error: Item name required.", Color.RED);
                return;
            }
            if (itemCategory == null || itemCategory.isEmpty() || itemCategory.equals("Select Category")) { // Added check for JComboBox
                JOptionPane.showMessageDialog(this, "Please select a valid item category.", "Input Error", JOptionPane.ERROR_MESSAGE);
                updateStatusBar("Error: Category required.", Color.RED);
                return;
            }

            for (int i = 0; i < tableModel.getRowCount(); i++) {
                if (tableModel.getValueAt(i, 0).toString().equalsIgnoreCase(itemName)) {
                    JOptionPane.showMessageDialog(this, "An item with this name already exists.", "Duplicate Item", JOptionPane.ERROR_MESSAGE);
                    updateStatusBar("Error: Duplicate item name.", Color.RED);
                    return;
                }
            }

            int quantity;
            try {
                quantity = Integer.parseInt(itemQuantityField.getText().trim());
                if (quantity < 0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Please enter a valid positive quantity (e.g., 50).", "Input Error", JOptionPane.ERROR_MESSAGE);
                updateStatusBar("Error: Invalid quantity.", Color.RED);
                return;
            }

            double price;
            try {
                price = Double.parseDouble(itemPriceField.getText().trim());
                if (price < 0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Please enter a valid positive price (e.g., 1250.75).", "Input Error", JOptionPane.ERROR_MESSAGE);
                updateStatusBar("Error: Invalid price.", Color.RED);
                return;
            }

            String currentDateTime = dateFormatter.format(new Date());

            tableModel.addRow(new Object[]{itemName, itemCategory, quantity, price, currentDateTime, currentDateTime});
            clearFields();
            updateTotalQuantity();
            updateStatusBar("Item '" + itemName + "' added successfully!", PRIMARY_COLOR.darker());
            // Add new category to JComboBox if it's not already there
            addCategoryToComboBox(itemCategory);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "An unexpected error occurred: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            updateStatusBar("Error: " + e.getMessage(), Color.RED);
            e.printStackTrace();
        }
    }

    private void updateItem() {
        int selectedRow = inventoryTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an item to update.", "No Selection", JOptionPane.WARNING_MESSAGE);
            updateStatusBar("Warning: No item selected for update.", Color.ORANGE);
            return;
        }

        int modelRow = inventoryTable.convertRowIndexToModel(selectedRow);

        try {
            String oldItemName = tableModel.getValueAt(modelRow, 0).toString();
            String newItemName = itemNameField.getText().trim();
            String newItemCategory = (String) itemCategoryField.getSelectedItem(); // Get category from JComboBox

            if (newItemName.isEmpty() || newItemName.equals("e.g., Essential Oil")) {
                JOptionPane.showMessageDialog(this, "Please enter a valid item name.", "Input Error", JOptionPane.ERROR_MESSAGE);
                updateStatusBar("Error: Item name required.", Color.RED);
                return;
            }
            if (newItemCategory == null || newItemCategory.isEmpty() || newItemCategory.equals("Select Category")) {
                JOptionPane.showMessageDialog(this, "Please select a valid item category.", "Input Error", JOptionPane.ERROR_MESSAGE);
                updateStatusBar("Error: Category required.", Color.RED);
                return;
            }

            for (int i = 0; i < tableModel.getRowCount(); i++) {
                if (i != modelRow && tableModel.getValueAt(i, 0).toString().equalsIgnoreCase(newItemName)) {
                    JOptionPane.showMessageDialog(this, "An item with this name already exists.", "Duplicate Item", JOptionPane.ERROR_MESSAGE);
                    updateStatusBar("Error: Duplicate item name.", Color.RED);
                    return;
                }
            }

            int newQuantity;
            try {
                newQuantity = Integer.parseInt(itemQuantityField.getText().trim());
                if (newQuantity < 0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Please enter a valid positive quantity (e.g., 50).", "Input Error", JOptionPane.ERROR_MESSAGE);
                updateStatusBar("Error: Invalid quantity.", Color.RED);
                return;
            }

            double newPrice;
            try {
                newPrice = Double.parseDouble(itemPriceField.getText().trim());
                if (newPrice < 0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Please enter a valid positive price (e.g., 1250.75).", "Input Error", JOptionPane.ERROR_MESSAGE);
                updateStatusBar("Error: Invalid price.", Color.RED);
                return;
            }

            String currentDateTime = dateFormatter.format(new Date());

            tableModel.setValueAt(newItemName, modelRow, 0);
            tableModel.setValueAt(newItemCategory, modelRow, 1);
            tableModel.setValueAt(newQuantity, modelRow, 2);
            tableModel.setValueAt(newPrice, modelRow, 3);
            tableModel.setValueAt(currentDateTime, modelRow, 5);

            clearFields();
            updateTotalQuantity();
            updateStatusBar("Item '" + newItemName + "' updated successfully!", PRIMARY_COLOR.darker());
            // Add new category to JComboBox if it's not already there
            addCategoryToComboBox(newItemCategory);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "An unexpected error occurred during update: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            updateStatusBar("Error during update: " + e.getMessage(), Color.RED);
            e.printStackTrace();
        }
    }

    private void deleteItem() {
        int selectedRow = inventoryTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an item to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            updateStatusBar("Warning: No item selected for deletion.", Color.ORANGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete the selected item?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            int modelRow = inventoryTable.convertRowIndexToModel(selectedRow);
            String itemName = tableModel.getValueAt(modelRow, 0).toString(); // Get name before deleting
            tableModel.removeRow(modelRow);
            clearFields();
            updateTotalQuantity();
            updateStatusBar("Item '" + itemName + "' deleted successfully.", PRIMARY_COLOR.darker());
        } else {
            updateStatusBar("Deletion cancelled.", TEXT_COLOR);
        }
    }

    private void clearFields() {
        itemNameField.setText("e.g., Essential Oil");
        itemNameField.setForeground(Color.GRAY);
        itemCategoryField.setSelectedItem("Wellness"); // Reset to a default category
        itemQuantityField.setText("e.g., 25");
        itemQuantityField.setForeground(Color.GRAY);
        itemPriceField.setText("e.g., 1250.55");
        itemPriceField.setForeground(Color.GRAY);
        inventoryTable.clearSelection();
        updateStatusBar("Fields cleared.", TEXT_COLOR);
    }

    private void updateTotalQuantity() {
        int total = 0;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            total += (Integer) tableModel.getValueAt(i, 2);
        }
        totalQuantityLabel.setText("Total Items: " + total);
    }

    private void setupTableSelectionListener() {
        inventoryTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int selectedRow = inventoryTable.getSelectedRow();
                    if (selectedRow != -1) {
                        int modelRow = inventoryTable.convertRowIndexToModel(selectedRow);

                        String name = tableModel.getValueAt(modelRow, 0).toString();
                        String category = tableModel.getValueAt(modelRow, 1).toString();
                        String quantity = tableModel.getValueAt(modelRow, 2).toString();
                        String price = tableModel.getValueAt(modelRow, 3).toString();

                        itemNameField.setText(name);
                        itemNameField.setForeground(TEXT_COLOR);
                        itemCategoryField.setSelectedItem(category); // Set selected item in JComboBox
                        itemQuantityField.setText(quantity);
                        itemQuantityField.setForeground(TEXT_COLOR);
                        itemPriceField.setText(price);
                        itemPriceField.setForeground(TEXT_COLOR);
                    }
                }
            }
        });
    }

    // New method to set up the right-click pop-up menu for the table
    private void setupTablePopupMenu() {
        JPopupMenu popupMenu = new JPopupMenu();

        JMenuItem updateMenuItem = new JMenuItem(UPDATE_ITEM_TEXT);
        updateMenuItem.addActionListener(e -> updateItem());
        popupMenu.add(updateMenuItem);

        JMenuItem deleteMenuItem = new JMenuItem(DELETE_ITEM_TEXT);
        deleteMenuItem.addActionListener(e -> deleteItem());
        popupMenu.add(deleteMenuItem);

        inventoryTable.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    int row = inventoryTable.rowAtPoint(e.getPoint());
                    if (row >= 0 && row < inventoryTable.getRowCount()) {
                        inventoryTable.setRowSelectionInterval(row, row);
                        popupMenu.show(e.getComponent(), e.getX(), e.getY());
                    }
                }
            }

            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    int row = inventoryTable.rowAtPoint(e.getPoint());
                    if (row >= 0 && row < inventoryTable.getRowCount()) {
                        inventoryTable.setRowSelectionInterval(row, row);
                        popupMenu.show(e.getComponent(), e.getX(), e.getY());
                    }
                }
            }
        });
    }


    private void setupSearch() {
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filterTable();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filterTable();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filterTable();
            }

            private void filterTable() {
                String searchText = searchField.getText().trim();
                if (searchText.equals("Search items...")) {
                    searchText = "";
                }

                if (searchText.length() == 0) {
                    sorter.setRowFilter(null);
                } else {
                    // Search across Item Name (0) and Category (1) columns
                    RowFilter<DefaultTableModel, Object> rowFilter = RowFilter.regexFilter("(?i)" + Pattern.quote(searchText), 0, 1);
                    sorter.setRowFilter(rowFilter);
                }
            }
        });
    }

    private void generateReport() {
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Inventory is empty. Nothing to report.", "No Data", JOptionPane.INFORMATION_MESSAGE);
            updateStatusBar("Report: Inventory is empty.", Color.ORANGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Inventory Report");
        fileChooser.setSelectedFile(new java.io.File("BambuVibe_Inventory_Report_" + dateFormatter.format(new Date()).replace(" ", "_").replace(":", "-") + ".txt"));

        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            java.io.File fileToSave = fileChooser.getSelectedFile();

            if (!fileToSave.getName().toLowerCase().endsWith(".txt")) {
                fileToSave = new java.io.File(fileToSave.getAbsolutePath() + ".txt");
            }

            try (FileWriter writer = new FileWriter(fileToSave)) {
                writer.write("=======================================================================\n");
                writer.write("                       Bambu Vibe Inventory Report                     \n");
                writer.write("=======================================================================\n");
                writer.write("Generated On: " + dateFormatter.format(new Date()) + "\n");
                writer.write("-----------------------------------------------------------------------\n");

                // Write header with formatting
                for (int i = 0; i < tableModel.getColumnCount(); i++) {
                    writer.write(String.format("%-20s", tableModel.getColumnName(i)));
                }
                writer.write("\n");
                writer.write("-----------------------------------------------------------------------\n");

                // Write data rows with formatting
                double totalValue = 0.0;
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    String itemName = tableModel.getValueAt(i, 0).toString();
                    String category = tableModel.getValueAt(i, 1).toString();
                    int quantity = (Integer) tableModel.getValueAt(i, 2);
                    double price = (Double) tableModel.getValueAt(i, 3);
                    String dateAdded = tableModel.getValueAt(i, 4).toString();
                    String dateUpdated = tableModel.getValueAt(i, 5).toString();

                    writer.write(String.format("%-20s", itemName));
                    writer.write(String.format("%-20s", category));
                    writer.write(String.format("%-20d", quantity));
                    writer.write(String.format("%-20.2f", price));
                    writer.write(String.format("%-20s", dateAdded));
                    writer.write(String.format("%-20s", dateUpdated));
                    writer.write("\n");

                    totalValue += (quantity * price); // Calculate total inventory value
                }
                writer.write("-----------------------------------------------------------------------\n");
                writer.write("Summary:\n");
                writer.write("Total Unique Items: " + tableModel.getRowCount() + "\n");
                writer.write("Total Quantity of All Items: " + totalQuantityLabel.getText().replaceAll("\\D+","") + "\n");
                writer.write(String.format("Estimated Total Inventory Value: ₱%.2f\n", totalValue));
                writer.write("=======================================================================\n");


                JOptionPane.showMessageDialog(this, "Report saved successfully to:\n" + fileToSave.getAbsolutePath(), "Report Generated", JOptionPane.INFORMATION_MESSAGE);
                updateStatusBar("Report generated successfully to " + fileToSave.getName(), PRIMARY_COLOR.darker());

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error saving report: " + ex.getMessage(), "File Error", JOptionPane.ERROR_MESSAGE);
                updateStatusBar("Error saving report: " + ex.getMessage(), Color.RED);
                ex.printStackTrace();
            }
        } else {
            updateStatusBar("Report generation cancelled.", TEXT_COLOR);
        }
    }

    // --- Persistent Data Storage (CSV) Methods ---
    private void saveInventory() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(INVENTORY_FILE))) {
            // Write header
            for (int i = 0; i < tableModel.getColumnCount(); i++) {
                writer.write(tableModel.getColumnName(i));
                if (i < tableModel.getColumnCount() - 1) {
                    writer.write(",");
                }
            }
            writer.newLine();

            // Write data rows
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                for (int j = 0; j < tableModel.getColumnCount(); j++) {
                    Object value = tableModel.getValueAt(i, j);
                    writer.write(value != null ? value.toString() : ""); // Handle nulls
                    if (j < tableModel.getColumnCount() - 1) {
                        writer.write(",");
                    }
                }
                writer.newLine();
            }
            updateStatusBar("Inventory saved to " + INVENTORY_FILE, PRIMARY_COLOR.darker());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving inventory: " + e.getMessage(), "Save Error", JOptionPane.ERROR_MESSAGE);
            updateStatusBar("Error saving inventory: " + e.getMessage(), Color.RED);
            e.printStackTrace();
        }
    }

    private void loadInventory() {
        File file = new File(INVENTORY_FILE);
        if (!file.exists()) {
            updateStatusBar("No existing inventory file found. Starting fresh.", TEXT_COLOR);
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(INVENTORY_FILE))) {
            String line;
            boolean isHeader = true;
            HashSet<String> uniqueCategories = new HashSet<>(); // To collect categories for JComboBox

            while ((line = reader.readLine()) != null) {
                if (isHeader) {
                    isHeader = false; // Skip header line
                    continue;
                }

                String[] parts = line.split(",", -1); // -1 to keep trailing empty strings
                if (parts.length == tableModel.getColumnCount()) {
                    // Convert types back
                    String itemName = parts[0];
                    String category = parts[1];
                    int quantity = Integer.parseInt(parts[2]);
                    double price = Double.parseDouble(parts[3]);
                    String dateAdded = parts[4];
                    String dateUpdated = parts[5];

                    tableModel.addRow(new Object[]{itemName, category, quantity, price, dateAdded, dateUpdated});
                    uniqueCategories.add(category); // Add category to set
                } else {
                    System.err.println("Skipping malformed row: " + line);
                    updateStatusBar("Warning: Skipped malformed row in " + INVENTORY_FILE, Color.ORANGE);
                }
            }

            // After loading, update the JComboBox with unique categories
            for (String category : uniqueCategories) {
                addCategoryToComboBox(category);
            }

            updateStatusBar("Inventory loaded from " + INVENTORY_FILE, PRIMARY_COLOR.darker());
        } catch (IOException | NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Error loading inventory: " + e.getMessage() + "\nFile might be corrupted. Starting fresh.", "Load Error", JOptionPane.ERROR_MESSAGE);
            updateStatusBar("Error loading inventory: " + e.getMessage(), Color.RED);
            tableModel.setRowCount(0); // Clear table if loading fails
            e.printStackTrace();
        }
    }

    // Helper method to add category to JComboBox if not already present
    private void addCategoryToComboBox(String category) {
        if (category == null || category.trim().isEmpty()) {
            return;
        }
        boolean exists = false;
        for (int i = 0; i < itemCategoryField.getItemCount(); i++) {
            if (itemCategoryField.getItemAt(i).equalsIgnoreCase(category)) {
                exists = true;
                break;
            }
        }
        if (!exists) {
            itemCategoryField.addItem(category);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}