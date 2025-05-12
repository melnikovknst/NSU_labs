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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.stream.Collectors;

public class ServerCoreJson {
    private final int port;
    private final boolean loggingEnabled;
    private final Logger logger = Logger.getLogger("ChatServer");
    private static final String LOG_FOLDER = "logs/";

    private final Map<Socket, ClientSession> clients = new ConcurrentHashMap<>();
    private final List<String> history = new ArrayList<>();

    public ServerCoreJson(int port, boolean loggingEnabled) {
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
        log("Server started on port " + port + " [JSON mode]");
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
                    System.out.println("SERVER received command: " + command);


                    switch (command) {
                        case "login" -> {
                            LoginCommand login = mapper.treeToValue(root, LoginCommand.class);
                            String sessionId = UUID.randomUUID().toString();
                            session.setName(login.name);
                            session.setSessionId(sessionId);
                            clients.put(session.getSocket(), session);

                            SuccessResponse response = new SuccessResponse(sessionId);
                            sendWithLengthPrefix(out, mapper.writeValueAsBytes(response));

                            sendHistory(out);
                            broadcast(new EventUser("userlogin", login.name));
                            sendUserListToAll();

                            log("User logged in: " + login.name);
                        }

                        case "message" -> {
                            System.out.println("Session id for message = " + session.getSessionId());
                            MessageCommand msg = mapper.treeToValue(root, MessageCommand.class);
                            log("[" + session.getName() + "]: " + msg.message);
                            history.add(session.getName() + ": " + msg.message);
                            broadcast(new EventMessage(session.getName(), msg.message));
                        }

                        case "keeponse" -> session.updateLastSeen();

                        case "logout" -> {
                            log(session.getName() + " exited via Exit button");
                            socket.close();
                        }

                        case "list" -> {
                            ListUsersResponse list = new ListUsersResponse(
                                    clients.values().stream()
                                            .filter(s -> s.getSessionId() != null)
                                            .map(s -> new UserInfo(s.getName()))
                                            .collect(Collectors.toList())
                            );
                            sendWithLengthPrefix(out, mapper.writeValueAsBytes(list));
                        }

                        default -> {
                            ErrorResponse err = new ErrorResponse("Unknown command: " + command);
                            sendWithLengthPrefix(out, mapper.writeValueAsBytes(err));
                        }
                    }
                }

                Thread.sleep(50);
            }
        } catch (IOException | InterruptedException e) {
            log("Client error: " + e.getMessage());
        } finally {
            clients.remove(session.getSocket());
            if (session.getName() != null) {
                broadcast(new EventUser("userlogout", session.getName()));
                sendUserListToAll();
            }
            log("Client removed: " + session.getSocket().getRemoteSocketAddress());
        }
    }

    private void sendWithLengthPrefix(OutputStream out, byte[] data) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(4 + data.length);
        buffer.putInt(data.length);
        buffer.put(data);
        out.write(buffer.array());
        out.flush();
    }

    private void broadcast(Object message) {
        try {
            byte[] data = new ObjectMapper().writeValueAsBytes(message);
            for (ClientSession s : clients.values()) {
                try {
                    sendWithLengthPrefix(s.getSocket().getOutputStream(), data);
                } catch (IOException ignored) {}
            }
        } catch (IOException ignored) {}
    }

    private void sendUserListToAll() {
        List<UserInfo> users = clients.values().stream()
                .filter(s -> s.getSessionId() != null)
                .map(s -> new UserInfo(s.getName()))
                .collect(Collectors.toList());
        ListUsersResponse list = new ListUsersResponse(users);
        broadcast(list);
    }

    private void sendHistory(OutputStream out) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            for (String msg : history.stream().skip(Math.max(0, history.size() - 20)).toList()) {
                String[] parts = msg.split(":", 2);
                if (parts.length == 2) {
                    EventMessage historyMsg = new EventMessage(parts[0].trim(), parts[1].trim());
                    sendWithLengthPrefix(out, mapper.writeValueAsBytes(historyMsg));
                }
            }
        } catch (IOException ignored) {}
    }

    private void startTimeoutChecker() {
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            for (ClientSession session : clients.values()) {
                if (session.isTimedOut()) {
                    try {
                        session.getSocket().close();
                        log("No keeponse. Disconnected: " + session.getName());
                        broadcast(new EventUser("sessiontimeout", session.getName()));
                        sendUserListToAll();
                    } catch (IOException ignored) {}
                }
            }
        }, 30, 1, TimeUnit.SECONDS);
    }

    private void log(String message) {
        if (loggingEnabled) {
            logger.info(message);
        } else {
            System.out.println(message);
        }
    }
}
