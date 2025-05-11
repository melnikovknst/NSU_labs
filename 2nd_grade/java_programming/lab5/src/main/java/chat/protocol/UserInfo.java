package chat.protocol;

import java.io.Serializable;

public class UserInfo implements Serializable {
    public String name;
    public String type;

    public UserInfo(String name, String type) {
        this.name = name;
        this.type = type;
    }
}