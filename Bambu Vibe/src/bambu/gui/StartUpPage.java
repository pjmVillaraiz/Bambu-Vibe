package bambu.gui;

import bambu.dao.ProductDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;

import static java.awt.EventQueue.invokeLater;

public class StartUpPage extends JFrame {

    private JPanel contentPane;
    private JTextField adminUsername;
    private JPasswordField adminPassword;
    private JTextField logopath;
    private String companylogopath = "bambu_logo.png";

    public static void main(String[] args) {
        invokeLater(() -> {
            try {
                if (isThisANewCompany()) {
                    StartUpPage frame = new StartUpPage();
                    frame.setVisible(true);
                } else {
                    LogIn logIn = new LogIn();
                    logIn.setVisible(true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private static boolean isThisANewCompany() {
        try {
            ProductDAO dao = new ProductDAO();
            return dao.getAllProduct().isEmpty();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public StartUpPage() {
        setIconImage(Toolkit.getDefaultToolkit().getImage("easy/resource/rsz_easyInlogo.png"));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 450, 300);

        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);

        JPanel titlePanel = new JPanel();
        contentPane.add(titlePanel, BorderLayout.NORTH);

        JLabel lblEasyInventory = new JLabel("- Bambu Vibes Inventory");
        lblEasyInventory.setFont(new Font("Segoe Script", Font.BOLD, 20));
        ImageIcon iconEI = new ImageIcon("bambu_logo.png");
        lblEasyInventory.setIcon(iconEI);
        titlePanel.add(lblEasyInventory);

        JPanel adminPanel = new JPanel();
        contentPane.add(adminPanel, BorderLayout.CENTER);
        adminPanel.setLayout(new BoxLayout(adminPanel, BoxLayout.Y_AXIS));

        JPanel panelCompanyName = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        adminPanel.add(panelCompanyName);

        JLabel lblCompanyName = new JLabel("Company Name");
        panelCompanyName.add(lblCompanyName);

        JTextField companyName = new JTextField("Bambu Vibes");
        companyName.setEditable(false);
        companyName.setColumns(20);
        panelCompanyName.add(companyName);

        JPanel panelUsername = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        adminPanel.add(panelUsername);

        JLabel lblAdminUsername = new JLabel("Admin Username");
        panelUsername.add(lblAdminUsername);

        adminUsername = new JTextField();
        adminUsername.setColumns(20);
        panelUsername.add(adminUsername);

        JPanel panelPassword = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        adminPanel.add(panelPassword);

        JLabel lblAdminPassword = new JLabel("Admin Password");
        panelPassword.add(lblAdminPassword);

        adminPassword = new JPasswordField();
        adminPassword.setColumns(20);
        panelPassword.add(adminPassword);

        JPanel panelLogo = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        adminPanel.add(panelLogo);

        JLabel lblCompanyLogo = new JLabel("Company Logo");
        panelLogo.add(lblCompanyLogo);

        logopath = new JTextField(companylogopath);
        logopath.setColumns(12);
        logopath.setEditable(false);
        panelLogo.add(logopath);

        JButton btnBrowse = new JButton("Browse");
        btnBrowse.addActionListener(e -> {
            JFileChooser fileDialog = new JFileChooser();
            int returnVal = fileDialog.showOpenDialog(contentPane);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                java.io.File file = fileDialog.getSelectedFile();
                companylogopath = file.getAbsolutePath();
                logopath.setText(companylogopath);
            } else {
                JOptionPane.showMessageDialog(null, "Select Company Logo [format - .jpg or .png only]");
            }
        });
        panelLogo.add(btnBrowse);

        JPanel controlButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        contentPane.add(controlButtonPanel, BorderLayout.SOUTH);

        JButton resetBtn = new JButton("Reset");
        resetBtn.setFont(new Font("Tahoma", Font.BOLD, 13));
        resetBtn.addActionListener(e -> {
            adminUsername.setText("");
            adminPassword.setText("");
            logopath.setText(companylogopath);
        });
        controlButtonPanel.add(resetBtn);

        JButton nextBtn = new JButton("Next");
        nextBtn.setFont(new Font("Tahoma", Font.BOLD, 13));
        nextBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(null, "Proceeding with setup for Bambu Vibes.");
        });
        controlButtonPanel.add(nextBtn);

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setFont(new Font("Tahoma", Font.BOLD, 13));
        cancelBtn.addActionListener(e -> System.exit(0));
        controlButtonPanel.add(cancelBtn);
    }
}
