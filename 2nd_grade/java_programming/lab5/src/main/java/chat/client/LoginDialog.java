package chat.client;

import javax.swing.*;
import java.awt.*;

public class LoginDialog extends JFrame {
    private JTextField nameField;
    private JTextField hostField;
    private JTextField portField;
    private JButton connectButton;

    public LoginDialog() {
        setTitle("Login");
        setSize(300, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(7, 1));

        panel.add(new JLabel("Username:"));
        nameField = new JTextField("Enter your name");
        panel.add(nameField);

        panel.add(new JLabel("Host:"));
        hostField = new JTextField("localhost");
        panel.add(hostField);

        panel.add(new JLabel("Port:"));
        portField = new JTextField("12345");
        panel.add(portField);

        connectButton = new JButton("Connect");
        panel.add(connectButton);

        connectButton.addActionListener(e -> tryConnect());

        add(panel);
        setVisible(true);
    }

    private void tryConnect() {
        String name = nameField.getText().trim();
        String host = hostField.getText().trim();
        int port = Integer.parseInt(portField.getText().trim());

        if (name.isEmpty() || host.isEmpty()) return;

        try {
            NetworkManager manager = new NetworkManager(host, port);
            String sessionId = manager.login(name);

            new ChatWindow(manager, name, sessionId);
            dispose();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Connection failed: " + ex.getMessage());
        }
    }
}
