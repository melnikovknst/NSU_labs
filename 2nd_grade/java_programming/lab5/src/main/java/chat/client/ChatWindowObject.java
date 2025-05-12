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

    public ChatWindowObject(String userName, String host, int port) throws Exception {
        this.userName = userName;
        this.socket = new Socket(host, port);
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.in = new ObjectInputStream(socket.getInputStream());

        // login
        LoginCommand login = new LoginCommand();
        login.name = userName;
        out.writeObject(login);
        out.flush();

        Object response = in.readObject();
        if (!(response instanceof SuccessResponse ok)) {
            throw new RuntimeException("Login failed or unexpected response.");
        }
        this.sessionId = ok.session;

        setupUI();
        startListening();
    }

    private void setupUI() {
        setTitle("Chat (ObjectStream) - " + userName);
        setLayout(new BorderLayout());

        chatArea.setEditable(false);
        add(new JScrollPane(chatArea), BorderLayout.CENTER);

        userList.setPreferredSize(new Dimension(150, 0));
        add(new JScrollPane(userList), BorderLayout.EAST);

        add(inputField, BorderLayout.SOUTH);

        inputField.addActionListener(e -> {
            String msg = inputField.getText().trim();
            if (!msg.isEmpty()) {
                try {
                    out.writeObject(new MessageCommand(sessionId, msg));
                    out.flush();
                    inputField.setText("");
                } catch (Exception ignored) {}
            }
        });

        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void startListening() {
        new Thread(() -> {
            try {
                while (!socket.isClosed()) {
                    Object obj = in.readObject();

                    if (obj instanceof EventMessage msg) {
                        appendMessage(msg.message);
                    } else if (obj instanceof EventUser evt) {
                        if ("userlogin".equals(evt.command)) {
                            appendMessage("[joined] " + evt.user);
                        } else if ("userlogout".equals(evt.command)) {
                            appendMessage("[left] " + evt.user);
                        } else if ("sessiontimeout".equals(evt.command)) {
                            handleConnectionLoss();
                        }
                        requestUserList();

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

    private void handleConnectionLoss() {
        SwingUtilities.invokeLater(() -> {
            dispose();
            new ReconnectDialog(userName, socket.getInetAddress().getHostAddress(), socket.getPort(), "object");
        });
    }
}
