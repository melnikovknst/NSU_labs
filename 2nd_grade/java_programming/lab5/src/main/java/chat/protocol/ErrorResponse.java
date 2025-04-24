package chat.protocol;

import java.util.Map;

public class ErrorResponse {
    public Map<String, String> error;

    public ErrorResponse(String message) {
        this.error = Map.of("message", message);
    }
}