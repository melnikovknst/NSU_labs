package chat.server;

import java.net.Socket;
import java.time.Instant;

public class ClientSession {
    private final Socket socket;
    private String name;
    private String sessionId;

    private Instant lastSeen;
    private int missedKeepAliveCount = 0;

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
        this.lastSeen = Instant.now();
    }

    public boolean isInactive() {
        return Instant.now().minusSeconds(30).isAfter(lastSeen);
    }

    public void resetKeepAlive() {
        missedKeepAliveCount = 0;
    }

    public void incrementMissedKeepAlive() {
        missedKeepAliveCount++;
    }

    public boolean isConnectionLost() {
        return missedKeepAliveCount >= 10;
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
