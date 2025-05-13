package chat.protocol;

import java.io.Serializable;

public class KeepAliveEvent implements Serializable {
    public String command = "keepalive";
}
