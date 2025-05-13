package chat.client;

import chat.protocol.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.swing.*;
import java.awt.*;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;

public class ChatWindowJson extends JFrame {
    private final JTextArea chatArea = new JTextArea();
    private final DefaultListModel<String> userListModel = new DefaultListModel<>();
    private final JList<String> userList = new JList<>(userListModel);
    private final JTextField inputField = new JTextField();

    private final Socket socket;
    private final OutputStream out;
    private final InputStream in;
    private final ObjectMapper mapper = new ObjectMapper();
    private final String sessionId;
    private final String userName;
    private boolean isExiting = false;


    public ChatWindowJson(String userName, String host, int port) throws Exception {
        this.userName = userName;
        this.socket = new Socket(host, port);
        this.out = socket.getOutputStream();
        this.in = socket.getInputStream();

        // login
        LoginCommand login = new LoginCommand();
        login.command = "login";
        login.name = userName;
        sendJson(login);

        Map<?, ?> response = receiveJson();
        if (response.containsKey("error")) {
            throw new RuntimeException("Login failed: " + response.get("error"));
        }

        this.sessionId = response.get("session").toString();
        System.out.println("id: " + this.sessionId);

        // запрос списка
        requestUserList();

        setupUI();
        System.out.println("setupUI() called");
        startListening();
    }

    private void setupUI() {
        setTitle("Chat (JSON) - " + userName);
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        chatArea.setEditable(false);
        JScrollPane chatScroll = new JScrollPane(chatArea);

        JButton sendButton = new JButton("Send");
        JButton exitButton = new JButton("Exit");

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(inputField, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2));
        buttonPanel.add(sendButton);
        buttonPanel.add(exitButton);
        bottomPanel.add(buttonPanel, BorderLayout.EAST);

        JScrollPane userScroll = new JScrollPane(userList);
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, chatScroll, userScroll);
        splitPane.setDividerLocation(400);

        add(splitPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        sendButton.addActionListener(e -> sendMessage());
        inputField.addActionListener(e -> sendMessage());
        exitButton.addActionListener(e -> {
            isExiting = true;
            LogoutCommand logout = new LogoutCommand(sessionId);
            try {
                sendJson(logout);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            dispose();
            new LoginDialog(null).setVisible(true);;
        });

        setVisible(true);
    }

    private void startListening() {
        new Thread(() -> {
            try {
                while (!socket.isClosed()) {
                    Map<?, ?> data = receiveJson();
                    if (data.containsKey("command")) {
                        String type = (String) data.get("command");
                        System.out.println("PACKET " + type);

                        switch (type) {
                            case "message" -> {
                                String from = (String) data.get("from");
                                String msg = (String) data.get("message");
                                appendMessage(from + ": " + msg);
                            }
                            case "userlogin" -> {
                                String user = (String) data.get("user");
                                appendMessage(user + " joined");
                                requestUserList();
                            }
                            case "userlogout" -> {
                                String user = (String) data.get("user");
                                appendMessage(user + " left");
                                requestUserList();
                            }
                            case "keepalive" -> {
                                System.out.println("keep");
                                KeepOnResponse keep = new KeepOnResponse(sessionId);
                                keep.command = "keeponse";
                                sendJson(keep);
                            }
                            case "history" -> {
                                String msg = (String) data.get("message");
                                appendMessage(msg);
                            }
                            case "listusers" -> {
                                SwingUtilities.invokeLater(() -> {
                                    userListModel.clear();
                                    Map<String, Object> listusers = (Map<String, Object>) data.get("listusers");
                                    List<Map<String, Object>> users = (List<Map<String, Object>>) listusers.get("user");
                                    for (Map<String, Object> user : users) {
                                        userListModel.addElement((String) user.get("name"));
                                    }
                                });
                            }
                            case "sessiontimeout" -> handleConnectionLoss();
                        }
                    }
                }
            } catch (Exception e) {
                handleConnectionLoss();
            }
        }).start();
    }

    private void sendMessage() {
        String msg = inputField.getText().trim();
        if (msg.isEmpty()) return;

        MessageCommand message = new MessageCommand();
        message.session = sessionId;
        message.message = msg;

        try {
            sendJson(message);
            inputField.setText("");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void requestUserList() {
        try {
            ListCommand cmd = new ListCommand();
            cmd.command = "list";
            cmd.session = sessionId;
            sendJson(cmd);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void appendMessage(String msg) {
        SwingUtilities.invokeLater(() -> {
            chatArea.append(msg + "\n");
            chatArea.setCaretPosition(chatArea.getDocument().getLength());
        });
    }

    private void handleConnectionLoss() {
        if (isExiting) return;
        SwingUtilities.invokeLater(() -> {
            dispose();
            new ReconnectDialog(userName, socket.getInetAddress().getHostAddress(), socket.getPort(), "json");
        });
    }

    private void sendJson(Object obj) throws Exception {
        byte[] data = mapper.writeValueAsBytes(obj);
        ByteBuffer buf = ByteBuffer.allocate(4 + data.length);
        buf.putInt(data.length);
        buf.put(data);
        out.write(buf.array());
        out.flush();
    }

    private Map<?, ?> receiveJson() throws Exception {
        byte[] lenBuf = in.readNBytes(4);
        int length = ByteBuffer.wrap(lenBuf).getInt();
        byte[] body = in.readNBytes(length);
        return mapper.readValue(body, Map.class);
    }
}
