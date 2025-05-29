package bambu.gui;

import javax.swing.*;
import java.awt.*;

public class LogIn extends JFrame {

    public LogIn() {
        setTitle("Login");
        setSize(300, 150);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));

        panel.add(new JLabel("Username:"));
        JTextField username = new JTextField();
        panel.add(username);

        panel.add(new JLabel("Password:"));
        JPasswordField password = new JPasswordField();
        panel.add(password);

        JButton loginButton = new JButton("Login");
        panel.add(loginButton);

        add(panel);
    }
}
