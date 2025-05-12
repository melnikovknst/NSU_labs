package chat.protocol;

import java.io.Serializable;

public class LogoutCommand implements Serializable {
    public String command = "logout";
    public String session;

    public LogoutCommand(String session) {
        this.session = session;
    }
}
