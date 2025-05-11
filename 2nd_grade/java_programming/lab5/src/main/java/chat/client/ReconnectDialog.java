package chat.client;

import javax.swing.*;
import java.awt.*;

public class ReconnectDialog extends JFrame {
    public ReconnectDialog(String userName, String host, int port, String protocol) {
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
                if ("json".equals(protocol)) {
                    new ChatWindowJson(userName, host, port);
                } else if ("object".equals(protocol)) {
                    new ChatWindowObject(userName, host, port);
                } else {
                    JOptionPane.showMessageDialog(this, "Unsupported protocol: " + protocol);
                    new LoginDialog(null).setVisible(true);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Reconnect failed: " + ex.getMessage());
                new LoginDialog(null).setVisible(true);
            }
            dispose();
        });

        mainMenuButton.addActionListener(e -> {
            new LoginDialog(null).setVisible(true);
            dispose();
        });

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2));
        buttonPanel.add(reconnectButton);
        buttonPanel.add(mainMenuButton);

        setLayout(new BorderLayout());
        add(label, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }
}
