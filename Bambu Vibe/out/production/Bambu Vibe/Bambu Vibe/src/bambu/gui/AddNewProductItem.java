package bambu.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import bambu.dao.ProductDAO;
import bambu.model.Product;
import bambu.util.SecureRandomTagGen;

public class AddNewProductItem extends JDialog {

    private static final long serialVersionUID = 1L;
    private final JPanel contentPanel = new JPanel();
    private JTextField productNameTextField;
    private JTextField brandTextField;
    private JTextField tagNumtextField;
    private JTextField cpTextField;
    private JSlider profitMargin;
    private JPanel addTitleField;

    private String tagNum;

    private ProductDAO productdao;
    private ProductSearch productsearch;

    private Product previousProduct = null;
    private boolean updateMode = false;

    private JTextField sptextField;

    public AddNewProductItem(ProductDAO productdao, ProductSearch productsearch, Product previousProduct,
                             boolean updateMode) {
        this();
        this.productdao = productdao;
        this.productsearch = productsearch;
        this.previousProduct = previousProduct;
        this.updateMode = updateMode;

        if (updateMode) {
            setTitle("Update Item");
            populateGui(previousProduct);

            //
            JLabel lblAddNewItem = new JLabel("   Update Product Details");
            lblAddNewItem.setFont(new Font("Segoe Script", Font.BOLD, 18));
            lblAddNewItem.setIcon(new ImageIcon("rsz_update-icon.png"));
            addTitleField.add(lblAddNewItem);
        } else {
            JLabel lblAddNewItem = new JLabel("   Add Product Details");
            lblAddNewItem.setFont(new Font("Segoe Script", Font.BOLD, 18));
            lblAddNewItem.setIcon(new ImageIcon("rsz_1add-item-icon.png"));
            addTitleField.add(lblAddNewItem);
        }
    }

    public AddNewProductItem(ProductDAO productdao, ProductSearch productsearch) {
        this(productdao, productsearch, null, false);
    }

    private void populateGui(Product previousProduct) {
        productNameTextField.setText(previousProduct.getName());
        brandTextField.setText(previousProduct.getCompany());
        cpTextField.setText(previousProduct.getCp() + "");
        sptextField.setText(previousProduct.getSp() + "");
        tagNumtextField.setText(previousProduct.getTag());
        profitMargin.setValue(getProfitPercentage(previousProduct.getSp(), previousProduct.getCp()));
    }

    private int getProfitPercentage(double d, double e) {
        return (int) ((d - e) * 100 / e);
    }

    public AddNewProductItem() {
        setTitle("Add New Item");
        setBounds(100, 100, 450, 448);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);

        addTitleField = new JPanel();

        JPanel inputDetailsPanel = new JPanel();
        GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
        gl_contentPanel.setHorizontalGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_contentPanel.createSequentialGroup().addGap(47)
                        .addComponent(addTitleField, GroupLayout.DEFAULT_SIZE, 317, Short.MAX_VALUE).addGap(58))
                .addGroup(gl_contentPanel.createSequentialGroup().addGap(23)
                        .addComponent(inputDetailsPanel, GroupLayout.PREFERRED_SIZE, 368, GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(31, Short.MAX_VALUE)));
        gl_contentPanel.setVerticalGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_contentPanel.createSequentialGroup().addContainerGap()
                        .addComponent(addTitleField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
                                GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(ComponentPlacement.UNRELATED)
                        .addComponent(inputDetailsPanel, GroupLayout.DEFAULT_SIZE, 313, Short.MAX_VALUE).addGap(7)));

        JLabel lblproductName = new JLabel("Product Name");

        productNameTextField = new JTextField();
    }
}
