package bambu.gui;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableRowSorter;

import bambu.dao.ProductDAO;
import bambu.model.Product;

public class ProductSearch extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField searchField;
    private ProductDAO productdao;
    private JTable table;
    private JScrollPane scrollPane;
    private JMenuBar menuBar;
    private JMenu mnProducts;
    private JMenu mnAccount;
    private JMenuItem mntmAddNewItems;
    private JMenuItem mntmFind;
    private JMenuItem mntmDelete;
    private JMenuItem mntmExit;
    private JMenuItem mntmEditAccount;
    private JPanel reportPanel;
    private JLabel lblReport;
    private JMenuItem mntmUpdate;
    private JMenuItem mntmExport;
    private TableRowSorter<ProductTableModel> sorter;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                ProductSearch frame = new ProductSearch();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public ProductSearch() throws SQLException {
        try {
            productdao = new ProductDAO();
        } catch (Exception e1) {
            JOptionPane.showMessageDialog(this, "Error:" + e1, "Error", JOptionPane.ERROR_MESSAGE);
        }

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 900, 600);

        menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        mnProducts = new JMenu("Products");
        menuBar.add(mnProducts);

        mntmAddNewItems = new JMenuItem("Add");
        mntmAddNewItems.addActionListener(e -> {
            AddNewProductItem dialog = new AddNewProductItem(productdao, ProductSearch.this);
            dialog.setVisible(true);
        });
        mnProducts.add(mntmAddNewItems);

        mntmFind = new JMenuItem("Find");
        mntmFind.addActionListener(e -> JOptionPane.showMessageDialog(null, "Use Search Field to Find Item."));
        mnProducts.add(mntmFind);

        mntmDelete = new JMenuItem("Delete");
        mntmDelete.addActionListener(e -> deleteSelectedProduct());

        mntmUpdate = new JMenuItem("Update");
        mntmUpdate.addActionListener(e -> updateSelectedProduct());
        mnProducts.add(mntmUpdate);

        mntmExport = new JMenuItem("Export to CSV");
        mntmExport.addActionListener(e -> exportToCSV());
        mnProducts.add(mntmExport);

        mnProducts.add(mntmDelete);

        mntmExit = new JMenuItem("Exit");
        mntmExit.addActionListener(e -> System.exit(0));
        mnProducts.add(mntmExit);

        mnAccount = new JMenu("Account");
        menuBar.add(mnAccount);

        mntmEditAccount = new JMenuItem("Edit Account");
        mnAccount.add(mntmEditAccount);

        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JLabel productLabel = new JLabel("Type Product Name");
        panel.add(productLabel);

        searchField = new JTextField();
        searchField.setColumns(20);
        panel.add(searchField);

        JButton btnSearch = new JButton("Search");
        btnSearch.addActionListener(arg0 -> searchProducts());
        panel.add(btnSearch);

        scrollPane = new JScrollPane();

        table = new JTable();
        scrollPane.setViewportView(table);

        JLabel lblCopyright = new JLabel("\u00A9copyright reserved - Achyut Dev, 2015 ");
        lblCopyright.setFont(new Font("Tahoma", Font.PLAIN, 9));

        JButton btnAdd = new JButton("Add New");
        btnAdd.setForeground(new Color(0, 128, 0));
        btnAdd.setFont(new Font("Segoe Script", Font.BOLD, 13));
        btnAdd.addActionListener(arg0 -> {
            AddNewProductItem dialog = new AddNewProductItem(productdao, ProductSearch.this);
            dialog.setVisible(true);
        });

        JButton btnUpdate = new JButton("Update");
        btnUpdate.setForeground(Color.BLUE);
        btnUpdate.setFont(new Font("Segoe Script", Font.BOLD, 13));
        btnUpdate.addActionListener(arg0 -> updateSelectedProduct());

        JButton btnDelete = new JButton("Delete");
        btnDelete.setForeground(Color.RED);
        btnDelete.setFont(new Font("Segoe Script", Font.BOLD, 13));
        btnDelete.addActionListener(e -> deleteSelectedProduct());

        reportPanel = new JPanel();
        lblReport = new JLabel("Report !");
        lblReport.setForeground(new Color(160, 82, 45));
        lblReport.setFont(new Font("Segoe Script", Font.BOLD, 20));
        lblReport.setIcon(new ImageIcon("rsz_billingreport.png"));
        reportPanel.add(lblReport);

        GroupLayout layout = new GroupLayout(contentPane);
        layout.setHorizontalGroup(layout.createParallelGroup(Alignment.LEADING)
                .addComponent(panel, GroupLayout.DEFAULT_SIZE, 884, Short.MAX_VALUE)
                .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 884, Short.MAX_VALUE)
                .addGroup(layout.createSequentialGroup().addContainerGap().addComponent(btnAdd)
                        .addPreferredGap(ComponentPlacement.RELATED).addComponent(btnUpdate)
                        .addPreferredGap(ComponentPlacement.RELATED).addComponent(btnDelete)
                        .addPreferredGap(ComponentPlacement.RELATED, 517, Short.MAX_VALUE)
                        .addComponent(lblCopyright).addContainerGap())
                .addComponent(reportPanel, GroupLayout.DEFAULT_SIZE, 884, Short.MAX_VALUE));
        layout.setVerticalGroup(layout.createParallelGroup(Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                        .addComponent(panel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 300, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                                .addComponent(btnAdd).addComponent(btnUpdate).addComponent(btnDelete).addComponent(lblCopyright))
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(reportPanel, GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)));
        contentPane.setLayout(layout);
        refreshProductView();
    }

    private void searchProducts() {
        String searchtext = searchField.getText();
        try {
            ArrayList<Product> items = (searchtext != null && searchtext.trim().length() > 0)
                    ? productdao.searchProduct(searchtext)
                    : productdao.getAllProduct();
            ProductTableModel model = new ProductTableModel(items);
            table.setModel(model);
            sorter = new TableRowSorter<>(model);
            table.setRowSorter(sorter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void refreshProductView() {
        try {
            ArrayList<Product> items = productdao.getAllProduct();
            ProductTableModel model = new ProductTableModel(items);
            table.setModel(model);
            sorter = new TableRowSorter<>(model);
            table.setRowSorter(sorter);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exportToCSV() {
        JFileChooser fileChooser = new JFileChooser();
        int option = fileChooser.showSaveDialog(this);
        if (option == JFileChooser.APPROVE_OPTION) {
            try (FileWriter fw = new FileWriter(fileChooser.getSelectedFile() + ".csv")) {
                ProductTableModel model = (ProductTableModel) table.getModel();
                for (int i = 0; i < model.getColumnCount(); i++) {
                    fw.append(model.getColumnName(i)).append(",");
                }
                fw.append("\n");
                for (int i = 0; i < model.getRowCount(); i++) {
                    for (int j = 0; j < model.getColumnCount(); j++) {
                        fw.append(model.getValueAt(i, j).toString()).append(",");
                    }
                    fw.append("\n");
                }
                JOptionPane.showMessageDialog(this, "Data exported successfully.");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Export failed: " + e.getMessage());
            }
        }
    }

    private void updateSelectedProduct() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "You must select a Product.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Product tmpProduct = (Product) table.getValueAt(row, ProductTableModel.OBJECT_COL);
        AddNewProductItem dialog = new AddNewProductItem(productdao, this, tmpProduct, true);
        dialog.setVisible(true);
    }

    private void deleteSelectedProduct() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "You must select a Product.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int response = JOptionPane.showConfirmDialog(this, "Delete this product?", "Confirmation",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (response != JOptionPane.YES_OPTION) {
            return;
        }
        Product tmpProduct = (Product) table.getValueAt(row, ProductTableModel.OBJECT_COL);
        try {
            productdao.deleteItem(tmpProduct.getId());
            refreshProductView();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
