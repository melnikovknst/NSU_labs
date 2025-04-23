package chat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.*;

public class ServerCore {
    private final int port;
    private final boolean loggingEnabled;
    private final List<ClientHandler> clients = new CopyOnWriteArrayList<>();
    private final Logger logger = Logger.getLogger("ChatServer");
    private static final String LOG_FOLDER = "logs/";

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

        while (true) {
            Socket clientSocket = serverSocket.accept();
            ClientHandler handler = new ClientHandler(clientSocket, this);
            clients.add(handler);
            handler.start();
        }
    }

    public void log(String message) {
        if (loggingEnabled) {
            logger.info(message);
        } else {
            System.out.println(message);
        }
    }

    public void removeClient(ClientHandler handler) {
        clients.remove(handler);
        log("Client removed: " + handler.getSocket().getRemoteSocketAddress());
    }
}