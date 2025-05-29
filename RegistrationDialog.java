import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RegistrationDialog extends JDialog {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton registerButton;
    private JButton cancelButton;

    private boolean registered = false;
    private String registeredUsername;
    private String registeredPassword;

    public RegistrationDialog(JFrame parent) {
        super(parent, "Bambu Vide - User Registration", true);
        setSize(350, 220);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel rootPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Image bambooBackground = new ImageIcon("resources/bamboo_texture.jpg").getImage();
                g.drawImage(bambooBackground, 0, 0, getWidth(), getHeight(), this);
            }
        };
        rootPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel regPanel = new JPanel(new GridBagLayout());
        regPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        regPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        usernameField = new JTextField(15);
        regPanel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        regPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        passwordField = new JPasswordField(15);
        regPanel.add(passwordField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setOpaque(false);
        registerButton = new JButton("Register");
        cancelButton = new JButton("Cancel");

        registerButton.setIcon(new ImageIcon("resources/bamboo_button.png"));
        cancelButton.setIcon(new ImageIcon("resources/bamboo_button.png"));

        registerButton.setHorizontalTextPosition(SwingConstants.CENTER);
        registerButton.setVerticalTextPosition(SwingConstants.CENTER);
        cancelButton.setHorizontalTextPosition(SwingConstants.CENTER);
        cancelButton.setVerticalTextPosition(SwingConstants.CENTER);

        buttonPanel.add(registerButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        regPanel.add(buttonPanel, gbc);

        rootPanel.add(regPanel, BorderLayout.CENTER);
        add(rootPanel);

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                attemptRegister();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registered = false;
                dispose();
            }
        });

        passwordField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                attemptRegister();
            }
        });
    }

    private void attemptRegister() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter both username and password.",
                    "Registration Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        registeredUsername = username;
        registeredPassword = password;
        registered = true;
        JOptionPane.showMessageDialog(this,
                "Registration successful!\nUsername: " + username,
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }

    public boolean isRegistered() {
        return registered;
    }

    public String getRegisteredUsername() {
        return registeredUsername;
    }

    public String getRegisteredPassword() {
        return registeredPassword;
    }

    public boolean isAuthenticated() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'isAuthenticated'");
    }
}