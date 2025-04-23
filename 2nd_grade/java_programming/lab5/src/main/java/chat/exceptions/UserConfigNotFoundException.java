package chat.exceptions;

import java.io.IOException;

public class UserConfigNotFoundException extends IOException {
    @Override
    public String getMessage() {
        return "User config not found.";
    }
}