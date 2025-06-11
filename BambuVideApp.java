import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

public class BambuVideApp extends JFrame {
    private DefaultTableModel tableModel;
    private JTable inventoryTable;
    private JTextField itemNameField;
    private JTextField itemQuantityField;
    private JTextField itemPriceField;
    private JTextField searchField;
    private JLabel totalQuantityLabel;
    private TableRowSorter<DefaultTableModel> sorter;

    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static final Color PRIMARY_COLOR = new Color(210, 180, 140);
    private static final Color SECONDARY_COLOR = new Color(245, 245, 220);
    private static final Color BUTTON_COLOR = new Color(195, 176, 145);
    private static final Color BORDER_COLOR = new Color(188, 158, 130);
    private static final Color TEXT_COLOR = new Color(101, 67, 33);
    private static final Font MAIN_FONT = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 14);

    public BambuVideApp() {
        setTitle("Bambu Vide Inventory Management");
        setSize(700, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

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
        contentPanel.add(createTotalPanel());

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        add(mainPanel);
        setupTableSelectionListener();
        setupSearch();
        updateTotalQuantity();
    }

    private void initializeTable() {
        String[] columnNames = {"Item Name", "Quantity", "Price (₱)", "Date Added", "Date Updated"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int column) {
                switch (column) {
                    case 1: return Integer.class;
                    case 2: return Double.class;
                    case 3:
                    case 4: return String.class;
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
        inventoryTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        inventoryTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        inventoryTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        inventoryTable.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
    }

    private JPanel createInputPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(SECONDARY_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        itemNameField = createStyledTextField("e.g., Bambu Filaments");
        itemQuantityField = createStyledTextField("e.g., 50");
        itemPriceField = createStyledTextField("e.g., 1250.75");

        panel.add(createFieldWithLabel("Item Name:", itemNameField));
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
        panel.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));

        JScrollPane scrollPane = new JScrollPane(inventoryTable);
        scrollPane.setPreferredSize(new Dimension(0, 250));
        scrollPane.getViewport().setBackground(SECONDARY_COLOR);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createTotalPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 0));
        panel.setBackground(SECONDARY_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        totalQuantityLabel = new JLabel("Total Items: 0");
        totalQuantityLabel.setFont(HEADER_FONT.deriveFont(Font.BOLD, 16f));
        totalQuantityLabel.setForeground(TEXT_COLOR);
        panel.add(totalQuantityLabel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        buttonPanel.setOpaque(false);

        JButton addButton = createStyledButton("Add Item");
        JButton updateButton = createStyledButton("Update Item");
        JButton clearButton = createStyledButton("Clear Fields");
        JButton reportButton = createStyledButton("Generate Report");

        addButton.addActionListener(e -> addItem());
        updateButton.addActionListener(e -> updateItem());
        clearButton.addActionListener(e -> clearFields());
        reportButton.addActionListener(e -> generateReport());

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
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

    private JPanel createFieldWithLabel(String labelText, JTextField textField) {
        JPanel panel = new JPanel(new BorderLayout(5, 0));
        panel.setOpaque(false);
        JLabel label = new JLabel(labelText);
        label.setFont(MAIN_FONT.deriveFont(Font.BOLD));
        label.setForeground(TEXT_COLOR);
        panel.add(label, BorderLayout.WEST);
        panel.add(textField, BorderLayout.CENTER);
        return panel;
    }

    private void addItem() {
        try {
            String itemName = itemNameField.getText().trim();
            if (itemName.isEmpty() || itemName.equals("e.g., Bambu Filaments")) {
                JOptionPane.showMessageDialog(this, "Please enter a valid item name.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            for (int i = 0; i < tableModel.getRowCount(); i++) {
                if (tableModel.getValueAt(i, 0).toString().equalsIgnoreCase(itemName)) {
                    JOptionPane.showMessageDialog(this, "An item with this name already exists.", "Duplicate Item", JOptionPane.ERROR_MESSAGE);
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
                return;
            }

            String currentDateTime = dateFormatter.format(new Date());

            tableModel.addRow(new Object[]{itemName, quantity, price, currentDateTime, currentDateTime});
            clearFields();
            updateTotalQuantity();
            JOptionPane.showMessageDialog(this, "Item added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "An unexpected error occurred: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void updateItem() {
        int selectedRow = inventoryTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an item to update.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = inventoryTable.convertRowIndexToModel(selectedRow);

        try {
            String oldItemName = tableModel.getValueAt(modelRow, 0).toString();
            String newItemName = itemNameField.getText().trim();

            if (newItemName.isEmpty() || newItemName.equals("e.g., Bambu Filaments")) {
                JOptionPane.showMessageDialog(this, "Please enter a valid item name.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            for (int i = 0; i < tableModel.getRowCount(); i++) {
                if (i != modelRow && tableModel.getValueAt(i, 0).toString().equalsIgnoreCase(newItemName)) {
                    JOptionPane.showMessageDialog(this, "An item with this name already exists.", "Duplicate Item", JOptionPane.ERROR_MESSAGE);
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
                return;
            }

            String currentDateTime = dateFormatter.format(new Date());

            tableModel.setValueAt(newItemName, modelRow, 0);
            tableModel.setValueAt(newQuantity, modelRow, 1);
            tableModel.setValueAt(newPrice, modelRow, 2);
            tableModel.setValueAt(currentDateTime, modelRow, 4);

            clearFields();
            updateTotalQuantity();
            JOptionPane.showMessageDialog(this, "Item updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "An unexpected error occurred during update: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void clearFields() {
        itemNameField.setText("e.g., Bambu Filaments");
        itemNameField.setForeground(Color.GRAY);
        itemQuantityField.setText("e.g., 50");
        itemQuantityField.setForeground(Color.GRAY);
        itemPriceField.setText("e.g., 1250.75");
        itemPriceField.setForeground(Color.GRAY);
        inventoryTable.clearSelection();
    }

    private void updateTotalQuantity() {
        int total = 0;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            total += (Integer) tableModel.getValueAt(i, 1);
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
                        String quantity = tableModel.getValueAt(modelRow, 1).toString();
                        String price = tableModel.getValueAt(modelRow, 2).toString();

                        itemNameField.setText(name);
                        itemNameField.setForeground(TEXT_COLOR);
                        itemQuantityField.setText(quantity);
                        itemQuantityField.setForeground(TEXT_COLOR);
                        itemPriceField.setText(price);
                        itemPriceField.setForeground(TEXT_COLOR);
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
                    RowFilter<DefaultTableModel, Object> rowFilter = RowFilter.regexFilter("(?i)" + Pattern.quote(searchText));
                    sorter.setRowFilter(rowFilter);
                }
            }
        });
    }

    private void generateReport() {
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Inventory is empty. Nothing to report.", "No Data", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Inventory Report");
        fileChooser.setSelectedFile(new java.io.File("Inventory_Report_" + dateFormatter.format(new Date()).replace(" ", "_").replace(":", "-") + ".txt"));

        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            java.io.File fileToSave = fileChooser.getSelectedFile();

            if (!fileToSave.getName().toLowerCase().endsWith(".txt")) {
                fileToSave = new java.io.File(fileToSave.getAbsolutePath() + ".txt");
            }

            try (FileWriter writer = new FileWriter(fileToSave)) {
                writer.write("Bambu Vide Inventory Report\n");
                writer.write("Generated On: " + dateFormatter.format(new Date()) + "\n");
                writer.write("--------------------------------------------------------------------------------------------------------------------------------\n");

                for (int i = 0; i < tableModel.getColumnCount(); i++) {
                    writer.write(String.format("%-25s", tableModel.getColumnName(i)));
                }
                writer.write("\n");
                writer.write("--------------------------------------------------------------------------------------------------------------------------------\n");

                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    writer.write(String.format("%-25s", tableModel.getValueAt(i, 0).toString()));
                    writer.write(String.format("%-25s", tableModel.getValueAt(i, 1).toString()));
                    writer.write(String.format("%-25s", tableModel.getValueAt(i, 2).toString()));
                    writer.write(String.format("%-25s", tableModel.getValueAt(i, 3).toString()));
                    writer.write(String.format("%-25s", tableModel.getValueAt(i, 4).toString()));
                    writer.write("\n");
                }
                writer.write("--------------------------------------------------------------------------------------------------------------------------------\n");
                writer.write("Total Items in Inventory: " + totalQuantityLabel.getText().replaceAll("\\D+","") + "\n");
                writer.write("--------------------------------------------------------------------------------------------------------------------------------\n");


                JOptionPane.showMessageDialog(this, "Report saved successfully to:\n" + fileToSave.getAbsolutePath(), "Report Generated", JOptionPane.INFORMATION_MESSAGE);

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error saving report: " + ex.getMessage(), "File Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}