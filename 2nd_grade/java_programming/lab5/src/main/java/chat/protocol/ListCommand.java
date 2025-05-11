package chat.protocol;
import java.io.Serializable;

public class ListCommand implements Serializable {
    public String command;
    public String session;
}