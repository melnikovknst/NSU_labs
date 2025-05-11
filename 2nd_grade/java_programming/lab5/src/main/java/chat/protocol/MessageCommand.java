package chat.protocol;

import java.io.Serializable;

public class MessageCommand implements Serializable {
    public String command = "message";
    public String message;
    public String session;

    public MessageCommand() {}

    public MessageCommand(String session, String message) {
        this.command = "message";
        this.session = session;
        this.message = message;
    }
}
