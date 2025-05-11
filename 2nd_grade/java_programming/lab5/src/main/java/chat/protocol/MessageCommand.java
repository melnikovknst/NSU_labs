package chat.protocol;

import java.io.Serializable;

public class MessageCommand implements Serializable {
    public String command;
    public String message;
    public String session;
}