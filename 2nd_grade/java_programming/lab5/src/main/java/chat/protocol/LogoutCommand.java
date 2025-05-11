package chat.protocol;

import java.io.Serializable;

public class LogoutCommand implements Serializable {
    public String session;

    public LogoutCommand(String session) {
        this.session = session;
    }
}
