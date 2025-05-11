package chat.protocol;

import java.io.Serializable;

public class EventMessage implements Serializable {
    public String name = "message";
    public String from;
    public String message;

    public EventMessage(String from, String message) {
        this.from = from;
        this.message = message;
    }
}
