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
        this.sessionId = ((Map<?, ?>) response.get("success")).get("session").toString();

        setupUI();
        startListening();
    }

    private void setupUI() {
        setTitle("Chat (JSON) - " + userName);
        setLayout(new BorderLayout());

        chatArea.setEditable(false);
        add(new JScrollPane(chatArea), BorderLayout.CENTER);

        userList.setPreferredSize(new Dimension(150, 0));
        add(new JScrollPane(userList), BorderLayout.EAST);

        add(inputField, BorderLayout.SOUTH);

        inputField.addActionListener(e -> {
            String msg = inputField.getText().trim();
            if (!msg.isEmpty()) {
                MessageCommand cmd = new MessageCommand();
                cmd.command = "message";
                cmd.session = sessionId;
                cmd.message = msg;
                try {
                    sendJson(cmd);
                } catch (Exception ignored) {}
                inputField.setText("");
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
                    Map<?, ?> data = receiveJson();
                    if (data.containsKey("event")) {
                        Map<?, ?> event = (Map<?, ?>) data.get("event");
                        String type = (String) event.get("name");

                        switch (type) {
                            case "message" -> {
                                String from = (String) event.get("from");
                                String message = (String) event.get("message");
                                appendMessage(from + ": " + message);
                            }
                            case "userlogin" -> {
                                String name = (String) event.get("user");
                                appendMessage("[joined] " + name);
                                requestUserList();
                            }
                            case "userlogout" -> {
                                String name = (String) event.get("user");
                                appendMessage("[left] " + name);
                                requestUserList();
                            }
                            case "keepalive" -> {
                                KeepOnResponse keep = new KeepOnResponse(sessionId);
                                keep.command = "keeponse";
                                sendJson(keep);
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

    private void requestUserList() {
        try {
            ListCommand cmd = new ListCommand();
            cmd.command = "list";
            cmd.session = sessionId;
            sendJson(cmd);

            Map<?, ?> response = receiveJson();
            List<Map<String, Object>> users =
                    (List<Map<String, Object>>) ((Map<?, ?>) response.get("listusers")).get("user");

            SwingUtilities.invokeLater(() -> {
                userListModel.clear();
                for (Map<String, Object> u : users) {
                    userListModel.addElement((String) u.get("name"));
                }
            });
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
