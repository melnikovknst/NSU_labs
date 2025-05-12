package chat.protocol;

import java.io.Serializable;

public class UserInfo implements Serializable {
    public String name;

    public UserInfo(String name) {
        this.name = name;
    }
}