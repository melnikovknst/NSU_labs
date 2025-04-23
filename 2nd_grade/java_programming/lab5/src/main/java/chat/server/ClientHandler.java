package chat.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ClientHandler extends Thread {
    private final Socket socket;
    private final ServerCore server;

    public ClientHandler(Socket socket, ServerCore server) {
        this.socket = socket;
        this.server = server;
    }

    @Override
    public void run() {
        try (InputStream in = socket.getInputStream(); OutputStream out = socket.getOutputStream()) {
            server.log("Client connected: " + socket.getRemoteSocketAddress());

            // placeholder: keep connection alive
            while (!socket.isClosed()) {
                if (in.available() > 0) {
                    byte[] buffer = new byte[1024];
                    int len = in.read(buffer);
                    String received = new String(buffer, 0, len);
                    server.log("Received: " + received);
                    out.write(("Echo: " + received).getBytes());
                }
                Thread.sleep(50);
            }
        } catch (IOException | InterruptedException e) {
            server.log("Client disconnected: " + e.getMessage());
        } finally {
            server.removeClient(this);
        }
    }

    public void sendMessage(String msg) throws IOException {
        socket.getOutputStream().write(msg.getBytes());
    }

    public Socket getSocket() {
        return socket;
    }
}