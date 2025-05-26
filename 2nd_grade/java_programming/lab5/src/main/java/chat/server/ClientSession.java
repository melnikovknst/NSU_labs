package chat.server;

import java.net.Socket;
import java.time.Instant;
import java.time.Duration;

public class ClientSession {
    private final Socket socket;
    private String name;
    private String sessionId;

    private Instant lastSeen;
    private Instant lastKeepAliveReceived;

    public ClientSession(Socket socket, String name) {
        this.socket = socket;
        this.name = name;
        this.lastSeen = Instant.now();
        this.lastKeepAliveReceived = Instant.now();
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
        return Instant.now().minusSeconds(60).isAfter(lastSeen);
    }

    public void resetKeepAlive() {
        this.lastKeepAliveReceived = Instant.now();
    }

    public boolean isConnectionLost() {
        Duration duration = Duration.between(lastKeepAliveReceived, Instant.now());
        return duration.getSeconds() >= 10;
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
