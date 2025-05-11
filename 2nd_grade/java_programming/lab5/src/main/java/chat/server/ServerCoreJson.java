package chat.server;

import chat.protocol.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.*;

public class ServerCoreJson {
    private final int port;
    private final boolean loggingEnabled;
    private final Logger logger = Logger.getLogger("ChatServer");
    private static final String LOG_FOLDER = "logs/";

    private final Map<Socket, ClientSession> clients = new ConcurrentHashMap<>();
    private final List<String> recentMessages = Collections.synchronizedList(new LinkedList<>());
    private final ObjectMapper mapper = new ObjectMapper();

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
        log("Server started on port " + port + " [JSON Mode]");
        startKeepAliveSender();

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
            InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();

            while (!socket.isClosed()) {
                if (in.available() >= 4) {
                    byte[] lenBuf = in.readNBytes(4);
                    int length = ByteBuffer.wrap(lenBuf).getInt();
                    byte[] body = in.readNBytes(length);

                    session.updateLastSeen();

                    String json = new String(body);
                    Map<?, ?> root = mapper.readValue(json, Map.class);
                    String command = (String) root.get("command");

                    if ("login".equals(command)) {
                        LoginCommand login = mapper.readValue(json, LoginCommand.class);
                        String sessionId = UUID.randomUUID().toString();

                        session.setName(login.name);
                        session.setSessionId(sessionId);

                        sendJson(out, new SuccessResponse(sessionId));

                        synchronized (recentMessages) {
                            for (String msg : recentMessages) {
                                sendJson(out, new EventMessage("history", msg));
                            }
                        }

                        broadcastToAll(new EventUser("userlogin", login.name));
                        log("User logged in: " + login.name);

                    } else if ("message".equals(command)) {
                        MessageCommand msgCmd = mapper.readValue(json, MessageCommand.class);
                        ClientSession sender = findBySessionId(msgCmd.session);
                        if (sender == null) {
                            sendJson(out, new ErrorResponse("Invalid session"));
                            continue;
                        }

                        String formatted = sender.getName() + ": " + msgCmd.message;
                        synchronized (recentMessages) {
                            recentMessages.add(formatted);
                            if (recentMessages.size() > 20) recentMessages.remove(0);
                        }

                        broadcastToAll(new EventMessage(sender.getName(), msgCmd.message));
                        log("Message from " + sender.getName() + ": " + msgCmd.message);

                    } else if ("list".equals(command)) {
                        ListCommand listCmd = mapper.readValue(json, ListCommand.class);
                        ClientSession sender = findBySessionId(listCmd.session);
                        if (sender == null) {
                            sendJson(out, new ErrorResponse("Invalid session"));
                            continue;
                        }

                        List<UserInfo> users = clients.values().stream()
                                .map(c -> new UserInfo(c.getName(), "java-server"))
                                .toList();

                        sendJson(out, new ListUsersResponse(users));

                    } else if ("logout".equals(command)) {
                        LogoutCommand logout = mapper.readValue(json, LogoutCommand.class);
                        ClientSession sender = findBySessionId(logout.session);
                        if (sender != null) {
                            clients.remove(sender.getSocket());
                            sender.getSocket().close();
                            broadcastToAll(new EventUser("userlogout", sender.getName()));
                            log("User logged out: " + sender.getName());
                        }

                    } else if ("keeponse".equals(command)) {
                        KeepOnResponse keep = mapper.readValue(json, KeepOnResponse.class);
                        ClientSession sender = findBySessionId(keep.session);
                        if (sender != null) {
                            sender.setWaitingKeepAlive(false);
                        }

                    } else {
                        sendJson(out, new ErrorResponse("Unknown command: " + command));
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

    private void sendJson(OutputStream out, Object obj) throws IOException {
        byte[] data = mapper.writeValueAsBytes(obj);
        ByteBuffer buffer = ByteBuffer.allocate(4 + data.length);
        buffer.putInt(data.length);
        buffer.put(data);
        out.write(buffer.array());
        out.flush();
    }

    private void startKeepAliveSender() {
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            for (ClientSession session : clients.values()) {
                try {
                    if (session.isWaitingKeepAlive()) {
                        clients.remove(session.getSocket());
                        session.getSocket().close();
                        broadcastToAll(new EventUser("userlogout", session.getName()));
                        log("No keeponse. Disconnected: " + session.getName());
                    } else {
                        sendJson(session.getSocket().getOutputStream(), new KeepAliveEvent());
                        session.setWaitingKeepAlive(true);
                    }
                } catch (IOException ignored) {}
            }
        }, 2, 2, TimeUnit.SECONDS);
    }

    private void broadcastToAll(Object obj) {
        for (ClientSession s : clients.values()) {
            try {
                sendJson(s.getSocket().getOutputStream(), obj);
            } catch (IOException ignored) {}
        }
    }

    private ClientSession findBySessionId(String sessionId) {
        for (ClientSession session : clients.values()) {
            if (sessionId != null && sessionId.equals(session.getSessionId())) {
                return session;
            }
        }
        return null;
    }

    public void log(String message) {
        if (loggingEnabled) {
            logger.info(message);
        } else {
            System.out.println(message);
        }
    }
}
