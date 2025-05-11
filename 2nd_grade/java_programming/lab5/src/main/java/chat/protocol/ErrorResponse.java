package chat.protocol;

import java.util.Map;
import java.io.Serializable;

public class ErrorResponse implements Serializable {
    public Map<String, String> error;

    public ErrorResponse(String message) {
        this.error = Map.of("message", message);
    }
}