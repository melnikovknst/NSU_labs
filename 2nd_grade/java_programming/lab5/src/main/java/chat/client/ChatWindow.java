package chat.client;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.Map;

public class ChatWindow extends JFrame {
    private final NetworkManager networkManager;
    private final String userName;
    private final String sessionId;
    private final String host;
    private final int port;

    private JTextArea chatArea;
    private JTextField inputField;
    private JButton sendButton;
    private JButton logoutButton;
    private JPanel userPanel;

    public ChatWindow(NetworkManager manager, String userName, String sessionId) {
        this.networkManager = manager;
        this.userName = userName;
        this.sessionId = sessionId;
        this.host = manager.getSocket().getInetAddress().getHostAddress();
        this.port = manager.getSocket().getPort();

        setTitle("Chat - " + userName);
        setSize(700, 500);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane chatScroll = new JScrollPane(chatArea);

        inputField = new JTextField();
        sendButton = new JButton("Send");
        logoutButton = new JButton("Logout");

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(inputField, BorderLayout.CENTER);

        JPanel buttonsPanel = new JPanel(new GridLayout(1, 2));
        buttonsPanel.add(sendButton);
        buttonsPanel.add(logoutButton);
        inputPanel.add(buttonsPanel, BorderLayout.EAST);

        userPanel = new JPanel();
        userPanel.setLayout(new BoxLayout(userPanel, BoxLayout.Y_AXIS));
        JScrollPane userScroll = new JScrollPane(userPanel);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, chatScroll, userScroll);
        splitPane.setDividerLocation(450);

        add(splitPane, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);

        sendButton.addActionListener(e -> sendMessage());
        inputField.addActionListener(e -> sendMessage());
        logoutButton.addActionListener(e -> logout());

        setVisible(true);
        sendListRequest();
        startListening();
    }

    private void sendMessage() {
        String text = inputField.getText().trim();
        if (text.isEmpty()) return;

        try {
            Map<String, Object> msg = Map.of(
                    "command", "message",
                    "session", sessionId,
                    "message", text
            );

            byte[] data = networkManager.getMapper().writeValueAsBytes(msg);
            networkManager.sendWithLengthPrefix(data);
            inputField.setText("");

        } catch (IOException e) {
            chatArea.append("Send error: " + e.getMessage() + "\n");
        }
    }

    private void sendListRequest() {
        try {
            Map<String, Object> listCmd = Map.of(
                    "command", "list",
                    "session", sessionId
            );

            byte[] data = networkManager.getMapper().writeValueAsBytes(listCmd);
            networkManager.sendWithLengthPrefix(data);

        } catch (IOException e) {
            chatArea.append("Failed to request user list: " + e.getMessage() + "\n");
        }
    }

    private void startListening() {
        new Thread(() -> {
            try {
                while (true) {
                    Map<String, Object> incoming = networkManager.receiveJson();

                    if (incoming.containsKey("event")) {
                        Map<String, Object> event = (Map<String, Object>) incoming.get("event");
                        String eventName = (String) event.get("name");

                        if ("message".equals(eventName)) {
                            String from = (String) event.get("from");
                            String message = (String) event.get("message");
                            chatArea.append(from + ": " + message + "\n");

                        } else if ("keepalive".equals(eventName)) {
                            sendKeepAliveResponse();
                        }
                        else if ("userlogin".equals(eventName)) {
                            String user = (String) event.get("user");
                            chatArea.append("* " + user + " connected *\n");
                            sendListRequest();

                        } else if ("userlogout".equals(eventName)) {
                            String user = (String) event.get("user");
                            chatArea.append("* " + user + " disconnected *\n");
                            sendListRequest();

                        } else if ("sessiontimeout".equals(eventName)) {
                            chatArea.append("* Session timed out *\n");
                            handleConnectionLoss();
                            break;
                        }

                    } else if (incoming.containsKey("success") && incoming.containsKey("listusers")) {
                        updateUserList(incoming);
                    }
                }
            } catch (IOException e) {
                handleConnectionLoss();
            }
        }).start();
    }

    private void updateUserList(Map<String, Object> incoming) {
        userPanel.removeAll();
        Map<String, Object> listusers = (Map<String, Object>) incoming.get("listusers");
        var users = (List<Map<String, Object>>) listusers.get("user");

        for (Map<String, Object> user : users) {
            JLabel label = new JLabel((String) user.get("name"));
            label.setAlignmentX(Component.LEFT_ALIGNMENT);
            userPanel.add(label);
        }

        userPanel.revalidate();
        userPanel.repaint();
    }

    private void handleConnectionLoss() {
        SwingUtilities.invokeLater(() -> {
            dispose();
            new ReconnectDialog(userName, host, port);
        });
    }

    private void logout() {
        try {
            Map<String, Object> logoutCmd = Map.of(
                    "command", "logout",
                    "session", sessionId
            );

            byte[] data = networkManager.getMapper().writeValueAsBytes(logoutCmd);
            networkManager.sendWithLengthPrefix(data);
        } catch (IOException ignored) {}

        dispose();
        new LoginDialog();
    }

    private void sendKeepAliveResponse() {
        try {
            Map<String, Object> response = Map.of(
                    "command", "keeponse",
                    "session", sessionId
            );
            byte[] data = networkManager.getMapper().writeValueAsBytes(response);
            networkManager.sendWithLengthPrefix(data);
        } catch (IOException e) {
            chatArea.append("Failed to send keeponse: " + e.getMessage() + "\n");
        }
    }

}
