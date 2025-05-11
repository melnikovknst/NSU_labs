package chat.protocol;

import java.io.Serializable;

public class SuccessResponse implements Serializable {
    public boolean success = true;
    public String session;

    public SuccessResponse(String session) {
        this.session = session;
    }
}