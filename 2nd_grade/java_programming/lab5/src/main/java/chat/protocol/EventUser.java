package chat.protocol;

import java.io.Serializable;

public class EventUser implements Serializable {
    public String command;
    public String user;

    public EventUser(String name, String user) {
        this.command = name;
        this.user = user;
    }
}
