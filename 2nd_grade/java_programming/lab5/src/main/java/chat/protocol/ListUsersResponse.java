package chat.protocol;

import java.util.List;
import java.util.Map;
import java.io.Serializable;

public class ListUsersResponse  implements Serializable {
    public String command = "listusers";
    public Map<String, List<UserInfo>> listusers;

    public ListUsersResponse(List<UserInfo> users) {
        this.listusers = Map.of("user", users);
    }
}