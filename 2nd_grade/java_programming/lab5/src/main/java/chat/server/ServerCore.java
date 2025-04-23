package chat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.*;
import java.util.logging.*;

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
            byte[] buffer = new byte[1024];

            while (!socket.isClosed()) {
                if (in.available() > 0) {
                    int len = in.read(buffer);
                    session.updateLastSeen();
                    String msg = new String(buffer, 0, len);
                    log("Received: " + msg.trim());
                }
                Thread.sleep(50);
            }
        } catch (IOException | InterruptedException e) {
            log("Client error: " + e.getMessage());
        } finally {
            clients.remove(session.getSocket());
            log("Client disconnected: " + session.getSocket().getRemoteSocketAddress());
        }
    }

    private void startTimeoutChecker() {
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            for (ClientSession session : clients.values()) {
                if (session.isTimedOut()) {
                    try {
                        session.getSocket().close();
                        log("Session timeout: " + session.getSocket().getRemoteSocketAddress());
                    } catch (IOException ignored) {}
                }
            }
        }, 30, 1, TimeUnit.SECONDS);
    }

    public void log(String message) {
        if (loggingEnabled) {
            logger.info(message);
        } else {
            System.out.println(message);
        }
    }
}