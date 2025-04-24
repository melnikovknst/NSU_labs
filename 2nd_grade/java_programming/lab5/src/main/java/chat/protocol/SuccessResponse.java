package chat.protocol;

public class SuccessResponse {
    public boolean success = true;
    public String session;

    public SuccessResponse(String session) {
        this.session = session;
    }
}