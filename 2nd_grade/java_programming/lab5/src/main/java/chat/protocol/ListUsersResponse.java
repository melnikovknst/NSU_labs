package chat.protocol;

import java.util.List;
import java.util.Map;

public class ListUsersResponse {
    public boolean success = true;
    public Map<String, List<UserInfo>> listusers;

    public ListUsersResponse(List<UserInfo> users) {
        this.listusers = Map.of("user", users);
    }
}