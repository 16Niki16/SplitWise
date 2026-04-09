package bg.sofia.uni.fmi.mjt.splitwise.repository;

import bg.sofia.uni.fmi.mjt.splitwise.containers.User;

import java.util.Map;

public interface UserRepository {
    User getUser(String username);

    void addUser(User user);

    void removeUser(String username);

    Map<String, User> getAllUsers();
}
