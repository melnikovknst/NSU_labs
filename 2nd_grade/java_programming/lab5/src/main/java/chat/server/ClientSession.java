package chat.server;

import java.net.Socket;
import java.time.Instant;

public class ClientSession {
    private final Socket socket;
    private String name;
    private Instant lastSeen;
    private String sessionId;

    public ClientSession(Socket socket, String name) {
        this.socket = socket;
        this.name = name;
        this.lastSeen = Instant.now();
    }

    public Socket getSocket() {
        return socket;
    }

    public String getName() {
        return name;
    }

    public void updateLastSeen() {
        lastSeen = Instant.now();
    }

    public boolean isTimedOut() {
        return Instant.now().minusSeconds(30).isAfter(lastSeen);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionId() {
        return sessionId;
    }
}