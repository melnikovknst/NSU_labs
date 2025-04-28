package chat.server;

import chat.protocol.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.*;
import java.util.stream.Collectors;

public class ServerCore {
    private final int port;
    private final boolean loggingEnabled;
    private final Logger logger = Logger.getLogger("ChatServer");
    private static final String LOG_FOLDER = "logs/";

    private final Map<Socket, ClientSession> clients = new ConcurrentHashMap<>();

    public ServerCore(int port, boolean loggingEnabled) {
        this.port = port;
        this.loggingEnabled = loggingEnabled;
        if (loggingEnabled) {
            try {
                Files.createDirectories(Paths.get(LOG_FOLDER));
                FileHandler fh = new FileHandler(LOG_FOLDER + "chat_server.log", true);
                fh.setFormatter(new SimpleFormatter());
                logger.addHandler(fh);
                logger.setUseParentHandlers(false);
            } catch (IOException ignored) {}
        }
    }

    public void start() throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        log("Server started on port " + port);
        startTimeoutChecker();

        while (true) {
            Socket clientSocket = serverSocket.accept();
            ClientSession session = new ClientSession(clientSocket, "anon");
            clients.put(clientSocket, session);
            log("Client connected: " + clientSocket.getRemoteSocketAddress());

            new Thread(() -> handleClient(session)).start();
        }
    }

    private void handleClient(ClientSession session) {
        try (Socket socket = session.getSocket()) {
            var in = socket.getInputStream();
            var out = socket.getOutputStream();
            var mapper = new ObjectMapper();

            while (!socket.isClosed()) {
                if (in.available() >= 4) {
                    byte[] lenBuf = in.readNBytes(4);
                    int length = ByteBuffer.wrap(lenBuf).getInt();

                    byte[] body = in.readNBytes(length);
                    session.updateLastSeen();

                    String json = new String(body);
                    JsonNode root = mapper.readTree(json);

                    String command = root.path("command").asText();

                    if ("login".equals(command)) {
                        LoginCommand login = mapper.treeToValue(root, LoginCommand.class);
                        String sessionId = UUID.randomUUID().toString();

                        session.setName(login.name);
                        session.setSessionId(sessionId);

                        SuccessResponse response = new SuccessResponse(sessionId);
                        sendJson(out, mapper, response);

                        broadcastUserEvent(mapper, "userlogin", session.getName());
                        log("User logged in: " + login.name);

                    } else if ("message".equals(command)) {
                        MessageCommand msgCmd = mapper.treeToValue(root, MessageCommand.class);
                        ClientSession sender = findBySessionId(msgCmd.session);
                        if (sender == null) {
                            sendError(out, mapper, "Invalid session");
                            continue;
                        }

                        broadcastMessage(mapper, msgCmd.message, sender.getName());
                        log("Message from " + sender.getName() + ": " + msgCmd.message);

                    } else if ("list".equals(command)) {
                        ListCommand listCmd = mapper.treeToValue(root, ListCommand.class);
                        ClientSession sender = findBySessionId(listCmd.session);
                        if (sender == null) {
                            sendError(out, mapper, "Invalid session");
                            continue;
                        }

                        List<UserInfo> users = clients.values().stream()
                                .map(c -> new UserInfo(c.getName(), "java-server"))
                                .collect(Collectors.toList());

                        ListUsersResponse response = new ListUsersResponse(users);
                        sendJson(out, mapper, response);

                    } else if ("logout".equals(command)) {
                        ClientSession sender = findBySessionId(root.path("session").asText());
                        if (sender != null) {
                            clients.remove(sender.getSocket());
                            sender.getSocket().close();
                            broadcastUserEvent(mapper, "userlogout", sender.getName());
                            log("User logged out: " + sender.getName());
                        }
                    } else {
                        sendError(out, mapper, "Unsupported command: " + command);
                    }
                }
                Thread.sleep(50);
            }
        } catch (IOException | InterruptedException e) {
            log("Client error: " + e.getMessage());
        } finally {
            clients.remove(session.getSocket());
            log("Client removed: " + session.getSocket().getRemoteSocketAddress());
        }
    }

    private void broadcastUserEvent(ObjectMapper mapper, String eventName, String user) {
        try {
            String json = mapper.writeValueAsString(Map.of(
                    "event", Map.of(
                            "name", eventName,
                            "user", user
                    )
            ));
            byte[] data = json.getBytes();
            for (ClientSession s : clients.values()) {
                sendWithLengthPrefix(s.getSocket().getOutputStream(), data);
            }
        } catch (IOException ignored) {}
    }

    private void broadcastMessage(ObjectMapper mapper, String msg, String from) {
        try {
            String json = mapper.writeValueAsString(Map.of(
                    "event", Map.of(
                            "name", "message",
                            "message", msg,
                            "from", from
                    )
            ));
            byte[] data = json.getBytes();
            for (ClientSession s : clients.values()) {
                sendWithLengthPrefix(s.getSocket().getOutputStream(), data);
            }
        } catch (IOException ignored) {}
    }

    private void sendJson(OutputStream out, ObjectMapper mapper, Object obj) throws IOException {
        byte[] responseBytes = mapper.writeValueAsBytes(obj);
        sendWithLengthPrefix(out, responseBytes);
    }

    private ClientSession findBySessionId(String sessionId) {
        for (ClientSession session : clients.values()) {
            if (sessionId.equals(session.getSessionId())) {
                return session;
            }
        }
        return null;
    }

    private void sendError(OutputStream out, ObjectMapper mapper, String msg) throws IOException {
        ErrorResponse error = new ErrorResponse(msg);
        sendJson(out, mapper, error);
    }

    private void sendWithLengthPrefix(OutputStream out, byte[] data) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(4 + data.length);
        buffer.putInt(data.length);
        buffer.put(data);
        out.write(buffer.array());
        out.flush();
    }

    private void startTimeoutChecker() {
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            for (ClientSession session : clients.values()) {
                if (session.isTimedOut()) {
                    try {
                        sendSessionTimeout(session);
                        broadcastUserEvent(new ObjectMapper(), "userlogout", session.getName());
                        session.getSocket().close();
                        log("Session timeout: " + session.getSocket().getRemoteSocketAddress());
                    } catch (IOException ignored) {}
                }

            }
        }, 30, 1, TimeUnit.SECONDS);
    }

    private void sendSessionTimeout(ClientSession session) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(Map.of(
                    "event", Map.of(
                            "name", "sessiontimeout"
                    )
            ));
            byte[] data = json.getBytes();
            sendWithLengthPrefix(session.getSocket().getOutputStream(), data);
        } catch (IOException ignored) {}
    }


    public void log(String message) {
        if (loggingEnabled) {
            logger.info(message);
        } else {
            System.out.println(message);
        }
    }
}
