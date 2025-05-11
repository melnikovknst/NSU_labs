package chat.protocol;
import java.io.Serializable;

public class ListCommand implements Serializable {
    public String command = "list";
    public String session;

    public ListCommand() {}

    public ListCommand(String session) {
        this.session = session;
    }
}
