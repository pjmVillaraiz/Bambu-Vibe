import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JCheckBox showPasswordCheckBox;
    private Map<String, UserData> registeredUsers;
    private static final String USERS_FILE = "users.csv";

    private static final Color LOGIN_TEXT_COLOR = new Color(50, 80, 45);
    private static final Color LOGIN_BUTTON_COLOR = new Color(180, 150, 110);
    private static final Color LOGIN_BORDER_COLOR = new Color(101, 67, 33);
    private static final Font LOGIN_FONT = new Font("Georgia", Font.PLAIN, 14);

    public LoginFrame() {
        setTitle("Login - Bambu Vibe");
        setIconImage(new ImageIcon("myicon.png").getImage());
        setSize(400, 450);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);

        registeredUsers = new HashMap<>();
        loadRegisteredUsers();

        if (registeredUsers.isEmpty()) {
            registeredUsers.put("admin", new UserData("admin", "admin123", "Admin User", 30, "123 Admin St", "admin@example.com", "09123456789"));
            saveRegisteredUsers();
        }

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                saveRegisteredUsers();
                dispose();
            }
        });

        setContentPane(new JLabel(new ImageIcon("Background.png")));
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setOpaque(false);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel logoLabel = new JLabel("Bambu Vibe", SwingConstants.CENTER);
        logoLabel.setFont(new Font("Sunday", Font.BOLD, 32));
        logoLabel.setForeground(LOGIN_TEXT_COLOR);
        mainPanel.add(logoLabel, BorderLayout.NORTH);

        JPanel loginInputPanel = new JPanel();
        loginInputPanel.setLayout(new BoxLayout(loginInputPanel, BoxLayout.Y_AXIS));
        loginInputPanel.setOpaque(false);
        loginInputPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LOGIN_BORDER_COLOR, 2),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        usernameField = createStyledTextField("Enter username");
        passwordField = createStyledPasswordField("Enter password");
        showPasswordCheckBox = new JCheckBox("Show Password");
        showPasswordCheckBox.setOpaque(false);
        showPasswordCheckBox.setFont(LOGIN_FONT);
        showPasswordCheckBox.setForeground(LOGIN_TEXT_COLOR);
        showPasswordCheckBox.addActionListener(e -> togglePasswordVisibility());

        loginInputPanel.add(createLabeledPanel("Username:", usernameField));
        loginInputPanel.add(Box.createVerticalStrut(10));
        loginInputPanel.add(createLabeledPanel("Password:", passwordField));
        loginInputPanel.add(showPasswordCheckBox);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setOpaque(false);

        JButton loginButton = createStyledButton("Login");
        loginButton.addActionListener(e -> attemptLogin());
        getRootPane().setDefaultButton(loginButton);

        JButton registerButton = createStyledButton("Register");
        registerButton.addActionListener(e -> {
            hideLoginFrame();
            new RegistrationFrame(LoginFrame.this, registeredUsers).setVisible(true);
        });

        JButton clearButton = createStyledButton("Clear");
        clearButton.addActionListener(e -> {
            usernameField.setText("");
            passwordField.setText("");
        });

        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        buttonPanel.add(clearButton);

        mainPanel.add(loginInputPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel createLabeledPanel(String labelText, JComponent field) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Georgia", Font.BOLD, 14));
        label.setForeground(Color.BLACK);

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        panel.setOpaque(false);
        panel.add(label);
        panel.add(field);
        return panel;
    }

    private JTextField createStyledTextField(String placeholder) {
        JTextField textField = new JTextField(15);
        textField.setText(placeholder);
        textField.setFont(LOGIN_FONT);
        textField.setForeground(Color.GRAY);
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LOGIN_BORDER_COLOR.darker(), 1),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        textField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (textField.getText().equals(placeholder)) {
                    textField.setText("");
                    textField.setForeground(Color.BLACK);
                }
            }

            public void focusLost(FocusEvent e) {
                if (textField.getText().isEmpty()) {
                    textField.setText(placeholder);
                    textField.setForeground(Color.GRAY);
                }
            }
        });
        return textField;
    }

    private JPasswordField createStyledPasswordField(String placeholder) {
        JPasswordField passwordField = new JPasswordField(15);
        passwordField.setText(placeholder);
        passwordField.setFont(LOGIN_FONT);
        passwordField.setForeground(Color.GRAY);
        passwordField.setEchoChar((char) 0);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LOGIN_BORDER_COLOR.darker(), 1),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        passwordField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (new String(passwordField.getPassword()).equals(placeholder)) {
                    passwordField.setText("");
                    passwordField.setForeground(Color.BLACK);
                    passwordField.setEchoChar('•');
                }
            }

            public void focusLost(FocusEvent e) {
                if (new String(passwordField.getPassword()).isEmpty()) {
                    passwordField.setText(placeholder);
                    passwordField.setForeground(Color.GRAY);
                    passwordField.setEchoChar((char) 0);
                }
            }
        });
        return passwordField;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(LOGIN_FONT.deriveFont(Font.BOLD));
        button.setBackground(LOGIN_BUTTON_COLOR);
        button.setForeground(LOGIN_TEXT_COLOR);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LOGIN_BORDER_COLOR),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void togglePasswordVisibility() {
        String placeholder = "Enter password";
        if (showPasswordCheckBox.isSelected()) {
            passwordField.setEchoChar((char) 0); // Show password
        } else {
            if (!new String(passwordField.getPassword()).equals(placeholder)) {
                passwordField.setEchoChar('•'); // Mask password
            }
        }
    }

    private void attemptLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (registeredUsers.containsKey(username)) {
            UserData user = registeredUsers.get(username);
            if (Objects.equals(user.getPassword(), password)) {
                JOptionPane.showMessageDialog(this, "Login Successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                new BambuVibeApp().setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid Username or Password", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Invalid Username or Password", "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveRegisteredUsers() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USERS_FILE))) {
            writer.write("username,password,name,age,address,email,phoneNumber\n");
            for (UserData user : registeredUsers.values()) {
                writer.write(user.toString() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadRegisteredUsers() {
        File file = new File(USERS_FILE);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean isHeader = true;
            while ((line = reader.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }
                String[] parts = line.split(",", -1);
                if (parts.length == 7) {
                    String username = parts[0];
                    String password = parts[1];
                    String name = parts[2];
                    int age = Integer.parseInt(parts[3]);
                    String address = parts[4];
                    String email = parts[5];
                    String phone = parts[6];
                    registeredUsers.put(username, new UserData(username, password, name, age, address, email, phone));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveUsersToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USERS_FILE))) {
            writer.write("username,password,name,age,address,email,phoneNumber\n");
            for (UserData user : registeredUsers.values()) {
                writer.write(user.toString() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showLoginFrame() {
        setVisible(true);
    }

    public void hideLoginFrame() {
        setVisible(false);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
