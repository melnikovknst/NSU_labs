package chat.client;

import chat.protocol.*;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.util.List;

public class ChatWindowObject extends JFrame {
    private final JTextArea chatArea = new JTextArea();
    private final DefaultListModel<String> userListModel = new DefaultListModel<>();
    private final JList<String> userList = new JList<>(userListModel);
    private final JTextField inputField = new JTextField();

    private final Socket socket;
    private final ObjectOutputStream out;
    private final ObjectInputStream in;
    private final String sessionId;
    private final String userName;
    private boolean isExiting = false;

    public ChatWindowObject(String userName, String host, int port) throws Exception {
        this.userName = userName;
        this.socket = new Socket(host, port);
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.in = new ObjectInputStream(socket.getInputStream());

        LoginCommand login = new LoginCommand();
        login.name = userName;
        out.writeObject(login);
        out.flush();

        Object response = in.readObject();
        if (!(response instanceof SuccessResponse ok)) {
            throw new RuntimeException("Login failed or unexpected response.");
        }

        this.sessionId = ok.session;
        requestUserList();

        setupUI();
        startListening();
    }

    private void setupUI() {
        setTitle("Chat (Object) - " + userName);
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        chatArea.setEditable(false);
        JScrollPane chatScroll = new JScrollPane(chatArea);

        JScrollPane userScroll = new JScrollPane(userList);
        userList.setPreferredSize(new Dimension(150, 0));

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, chatScroll, userScroll);
        split.setDividerLocation(400);

        JButton sendButton = new JButton("Send");
        JButton exitButton = new JButton("Exit");

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(inputField, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new GridLayout(1, 2));
        buttons.add(sendButton);
        buttons.add(exitButton);
        bottom.add(buttons, BorderLayout.EAST);

        add(split, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        sendButton.addActionListener(e -> sendMessage());
        inputField.addActionListener(e -> sendMessage());

        exitButton.addActionListener(e -> {
            isExiting = true;
            try {
                out.writeObject(new LogoutCommand(sessionId));
                out.flush();
            } catch (Exception ignored) {}
            dispose();
            new LoginDialog(null).setVisible(true);
        });

        setVisible(true);
    }

    private void sendMessage() {
        String msg = inputField.getText().trim();
        if (msg.isEmpty()) return;
        try {
            out.writeObject(new MessageCommand(sessionId, msg));
            out.flush();
            inputField.setText("");
        } catch (Exception ignored) {}
    }

    private void requestUserList() {
        try {
            out.writeObject(new ListCommand(sessionId));
            out.flush();
        } catch (Exception ignored) {}
    }

    private void appendMessage(String msg) {
        SwingUtilities.invokeLater(() -> {
            chatArea.append(msg + "\n");
            chatArea.setCaretPosition(chatArea.getDocument().getLength());
        });
    }

    private void startListening() {
        new Thread(() -> {
            try {
                while (!socket.isClosed()) {
                    Object obj = in.readObject();

                    if (obj instanceof EventMessage msg) {
                        appendMessage(msg.from + ": " + msg.message);
                    } else if (obj instanceof EventUser evt) {
                        if ("userlogin".equals(evt.command)) {
                            appendMessage(evt.user + " joined");
                            requestUserList();
                        } else if ("userlogout".equals(evt.command)) {
                            appendMessage( evt.user + " left");
                            requestUserList();
                        } else if ("sessiontimeout".equals(evt.command)) {
                            handleConnectionLoss();
                        }
                    } else if (obj instanceof KeepAliveEvent) {
                        out.writeObject(new KeepOnResponse(sessionId));
                        out.flush();
                    } else if (obj instanceof ListUsersResponse list) {
                        SwingUtilities.invokeLater(() -> {
                            userListModel.clear();
                            List<UserInfo> users = list.listusers.get("user");
                            for (UserInfo u : users) {
                                userListModel.addElement(u.name);
                            }
                        });
                    }
                }
            } catch (Exception e) {
                handleConnectionLoss();
            }
        }).start();
    }

    private void handleConnectionLoss() {
        if (isExiting) return;
        SwingUtilities.invokeLater(() -> {
            dispose();
            new ReconnectDialog(userName, socket.getInetAddress().getHostAddress(), socket.getPort(), "object");
        });
    }
}
