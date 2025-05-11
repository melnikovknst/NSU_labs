package chat.protocol;
import java.io.Serializable;

public class LoginCommand implements Serializable {
    public String command;
    public String name;
    public String type;
}
