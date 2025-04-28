package chat.client;

import chat.protocol.LoginCommand;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Map;

public class NetworkManager {
    private final Socket socket;
    private final ObjectMapper mapper = new ObjectMapper();
    private final OutputStream out;
    private final InputStream in;

    public NetworkManager(String host, int port) throws IOException {
        socket = new Socket(host, port);
        out = socket.getOutputStream();
        in = socket.getInputStream();
    }

    public String login(String name) throws IOException {
        LoginCommand cmd = new LoginCommand();
        cmd.command = "login";
        cmd.name = name;
        cmd.type = "java-swing";

        byte[] data = mapper.writeValueAsBytes(cmd);
        sendWithLengthPrefix(data);

        Map<String, Object> response = receiveJson();
        if (response.containsKey("success")) {
            return (String) response.get("session");
        } else {
            throw new IOException("Login failed: " + response);
        }
    }

    public void sendWithLengthPrefix(byte[] data) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(4 + data.length);
        buffer.putInt(data.length);
        buffer.put(data);
        out.write(buffer.array());
        out.flush();
    }

    public Map<String, Object> receiveJson() throws IOException {
        while (in.available() < 4) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ignored) {}
        }
        byte[] lenBuf = in.readNBytes(4);
        int length = ByteBuffer.wrap(lenBuf).getInt();

        while (in.available() < length) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ignored) {}
        }
        byte[] body = in.readNBytes(length);
        return mapper.readValue(body, Map.class);
    }


    public Socket getSocket() {
        return socket;
    }

    public ObjectMapper getMapper() {
        return mapper;
    }

    public OutputStream getOut() {
        return out;
    }

    public InputStream getIn() {
        return in;
    }
}
