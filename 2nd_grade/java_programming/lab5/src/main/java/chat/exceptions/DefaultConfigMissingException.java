package chat.exceptions;

import java.io.IOException;

public class DefaultConfigMissingException extends IOException {
    @Override
    public String getMessage() {
        return "Default config file is missing. Critical error.";
    }
}