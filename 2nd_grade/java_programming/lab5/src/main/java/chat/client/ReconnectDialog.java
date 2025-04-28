package chat.client;

import javax.swing.*;
import java.awt.*;

public class ReconnectDialog extends JFrame {
    public ReconnectDialog(String userName, String host, int port) {
        setTitle("Disconnected");
        setSize(300, 150);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JLabel label = new JLabel("Connection lost. What do you want to do?");
        label.setHorizontalAlignment(SwingConstants.CENTER);

        JButton reconnectButton = new JButton("Reconnect");
        JButton mainMenuButton = new JButton("Main Menu");

        reconnectButton.addActionListener(e -> {
            try {
                NetworkManager manager = new NetworkManager(host, port);
                String sessionId = manager.login(userName);
                new ChatWindow(manager, userName, sessionId);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Reconnect failed: " + ex.getMessage());
                new LoginDialog();
            }
            dispose();
        });

        mainMenuButton.addActionListener(e -> {
            new LoginDialog();
            dispose();
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 2));
        buttonPanel.add(reconnectButton);
        buttonPanel.add(mainMenuButton);

        setLayout(new BorderLayout());
        add(label, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }
}
