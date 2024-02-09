package bg.sofia.uni.fmi.mjt.splitwise.containers;

import bg.sofia.uni.fmi.mjt.splitwise.user.UserAPI;

import java.util.HashSet;
import java.util.Set;

public class ClientContainer {
    Set<UserAPI> users;

    public ClientContainer() {
        users = new HashSet<>();
    }

    public UserAPI getUser(String name) {
        return users.stream()
                .filter(p -> p.getUsername().equals(name.strip()))
                .findAny()
                .orElse(null);
    }

    public void addUser(UserAPI user) {
        users.add(user);
    }
}
