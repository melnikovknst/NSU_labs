package chat.server;

import chat.protocol.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.*;

public class ServerCoreObject {
    private final int port;
    private final boolean loggingEnabled;
    private final Logger logger = Logger.getLogger("ChatServer");
    private static final String LOG_FOLDER = "logs/";

    private final Map<Socket, ClientSession> clients = new ConcurrentHashMap<>();
    private final List<String> recentMessages = Collections.synchronizedList(new LinkedList<>());

    public ServerCoreObject(int port, boolean loggingEnabled) {
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
        log("Server started on port " + port + " [Object Mode]");
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
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            while (!socket.isClosed()) {
                Object obj = in.readObject();
                session.updateLastSeen();

                if (obj instanceof LoginCommand login) {
                    String sessionId = UUID.randomUUID().toString();
                    session.setName(login.name);
                    session.setSessionId(sessionId);

                    out.writeObject(new SuccessResponse(sessionId));
                    out.flush();

                    synchronized (recentMessages) {
                        for (String msg : recentMessages) {
                            out.writeObject(new EventMessage("history", msg));
                            out.flush();
                        }
                    }

                    broadcastToAll(new EventUser("userlogin", login.name));
                    log("User logged in: " + login.name);

                } else if (obj instanceof MessageCommand msgCmd) {
                    ClientSession sender = findBySessionId(msgCmd.session);
                    if (sender == null) {
                        out.writeObject(new ErrorResponse("Invalid session"));
                        out.flush();
                        continue;
                    }

                    String text = sender.getName() + ": " + msgCmd.message;
                    synchronized (recentMessages) {
                        recentMessages.add(text);
                        if (recentMessages.size() > 20) recentMessages.remove(0);
                    }

                    broadcastToAll(new EventMessage(sender.getName(), msgCmd.message));
                    log("Message from " + sender.getName() + ": " + msgCmd.message);

                } else if (obj instanceof ListCommand listCmd) {
                    ClientSession sender = findBySessionId(listCmd.session);
                    if (sender == null) {
                        out.writeObject(new ErrorResponse("Invalid session"));
                        out.flush();
                        continue;
                    }

                    List<UserInfo> users = clients.values().stream()
                            .map(c -> new UserInfo(c.getName(), "java-server"))
                            .toList();

                    out.writeObject(new ListUsersResponse(users));
                    out.flush();

                } else if (obj instanceof LogoutCommand logout) {
                    ClientSession sender = findBySessionId(logout.session);
                    if (sender != null) {
                        clients.remove(sender.getSocket());
                        sender.getSocket().close();
                        broadcastToAll(new EventUser("userlogout", sender.getName()));
                        log("User logged out: " + sender.getName());
                    }

                } else if (obj instanceof KeepOnResponse keep) {
                    ClientSession sender = findBySessionId(keep.session);
                    if (sender != null) {
                        sender.setWaitingKeepAlive(false);
                    }

                } else {
                    out.writeObject(new ErrorResponse("Unsupported command: " + obj));
                    out.flush();
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            log("Client error: " + e.getMessage());
        } finally {
            clients.remove(session.getSocket());
            log("Client removed: " + session.getSocket().getRemoteSocketAddress());
        }
    }

    private void startKeepAliveSender() {
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            for (ClientSession session : clients.values()) {
                try {
                    ObjectOutputStream out = new ObjectOutputStream(session.getSocket().getOutputStream());
                    if (session.isWaitingKeepAlive()) {
                        clients.remove(session.getSocket());
                        session.getSocket().close();
                        broadcastToAll(new EventUser("userlogout", session.getName()));
                        log("No keeponse. Disconnected: " + session.getName());
                    } else {
                        out.writeObject(new KeepAliveEvent());
                        out.flush();
                        session.setWaitingKeepAlive(true);
                    }
                } catch (IOException ignored) {}
            }
        }, 2, 2, TimeUnit.SECONDS);
    }

    private void broadcastToAll(Object event) {
        for (ClientSession s : clients.values()) {
            try {
                ObjectOutputStream out = new ObjectOutputStream(s.getSocket().getOutputStream());
                out.writeObject(event);
                out.flush();
            } catch (IOException ignored) {}
        }
    }

    private ClientSession findBySessionId(String sessionId) {
        for (ClientSession s : clients.values()) {
            if (sessionId != null && sessionId.equals(s.getSessionId())) {
                return s;
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
