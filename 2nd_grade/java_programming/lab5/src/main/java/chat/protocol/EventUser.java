package chat.protocol;

import java.io.Serializable;

public class EventUser implements Serializable {
    public String name; // "userlogin" or "userlogout"
    public String user;

    public EventUser(String name, String user) {
        this.name = name;
        this.user = user;
    }
}
