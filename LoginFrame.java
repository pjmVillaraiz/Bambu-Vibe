import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter; // Added MouseAdapter import
import java.awt.event.MouseEvent; // Added MouseEvent import
import java.util.HashMap;
import java.util.Map;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private Map<String, String> registeredUsers;

    private static final Color PRIMARY_COLOR = new Color(210, 180, 140);
    private static final Color SECONDARY_COLOR = new Color(245, 245, 220);
    private static final Color BUTTON_COLOR = new Color(195, 176, 145);
    private static final Color BORDER_COLOR = new Color(188, 158, 130);
    private static final Color TEXT_COLOR = new Color(101, 67, 33);
    private static final Font MAIN_FONT = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 14);

    public LoginFrame() {
        setTitle("Bambu Vide Login");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        registeredUsers = new HashMap<>();
        registeredUsers.put("admin", "admin123"); // Default user for testing

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(SECONDARY_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(SECONDARY_COLOR);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Login to Inventory", SwingConstants.CENTER);
        titleLabel.setFont(HEADER_FONT.deriveFont(Font.BOLD, 18f));
        titleLabel.setForeground(TEXT_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        formPanel.add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;
        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(MAIN_FONT.deriveFont(Font.BOLD));
        userLabel.setForeground(TEXT_COLOR);
        formPanel.add(userLabel, gbc);

        gbc.gridx = 1;
        usernameField = createStyledTextField("Enter username");
        formPanel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(MAIN_FONT.deriveFont(Font.BOLD));
        passLabel.setForeground(TEXT_COLOR);
        formPanel.add(passLabel, gbc);

        gbc.gridx = 1;
        passwordField = createStyledPasswordField("Enter password");
        formPanel.add(passwordField, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setOpaque(false);

        JButton loginButton = createStyledButton("Login");
        loginButton.addActionListener(e -> loginUser());
        buttonPanel.add(loginButton);

        JButton registerButton = createStyledButton("Register");
        registerButton.addActionListener(e -> navigateToRegistration());
        buttonPanel.add(registerButton);

        formPanel.add(buttonPanel, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);
        add(mainPanel);
    }

    private void loginUser() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username and password cannot be empty.", "Login Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (registeredUsers.containsKey(username) && registeredUsers.get(username).equals(password)) {
            JOptionPane.showMessageDialog(this, "Login successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
            onLoginSuccess();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password.", "Login Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void navigateToRegistration() {
        RegistrationFrame registrationFrame = new RegistrationFrame(this, registeredUsers);
        registrationFrame.setVisible(true);
        setVisible(false);
    }

    private void onLoginSuccess() {
        BambuVideApp inventoryApp = new BambuVideApp();
        inventoryApp.setVisible(true);
        dispose();
    }

    public void setRegisteredUsers(Map<String, String> users) {
        this.registeredUsers = users;
    }

    public void showLoginFrame() {
        setVisible(true);
        usernameField.setText("Enter username");
        usernameField.setForeground(Color.GRAY);
        passwordField.setText("Enter password");
        passwordField.setForeground(Color.GRAY);
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
                if (textField.getText().equals(placeholderText) || textField.getText().equals("Enter password")) {
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

    private JPasswordField createStyledPasswordField(String placeholderText) {
        JPasswordField passwordField = new JPasswordField(20);
        passwordField.setFont(MAIN_FONT);
        passwordField.setForeground(TEXT_COLOR);
        passwordField.setBackground(Color.WHITE);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR.darker(), 1),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        passwordField.setText(placeholderText);
        passwordField.setEchoChar((char) 0);
        passwordField.setForeground(Color.GRAY);
        passwordField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (new String(passwordField.getPassword()).equals(placeholderText)) {
                    passwordField.setText("");
                    passwordField.setEchoChar('*');
                    passwordField.setForeground(TEXT_COLOR);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (new String(passwordField.getPassword()).isEmpty()) {
                    passwordField.setText(placeholderText);
                    passwordField.setEchoChar((char) 0);
                    passwordField.setForeground(Color.GRAY);
                }
            }
        });
        return passwordField;
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

        // Corrected from FocusAdapter to MouseAdapter
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { // Changed 'evt' to 'e' for consistency
                button.setBackground(BUTTON_COLOR.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) { // Changed 'evt' to 'e' for consistency
                button.setBackground(BUTTON_COLOR);
            }
        });
        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
