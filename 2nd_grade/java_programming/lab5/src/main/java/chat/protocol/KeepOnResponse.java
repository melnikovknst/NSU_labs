package chat.protocol;

import java.io.Serializable;

public class KeepOnResponse implements Serializable {
    public String command = "keeponse";
    public String session;

    public KeepOnResponse(String session) {
        this.session = session;
    }
}
