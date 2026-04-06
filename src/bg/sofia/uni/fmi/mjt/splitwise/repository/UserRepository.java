package bg.sofia.uni.fmi.mjt.splitwise.repository;

import bg.sofia.uni.fmi.mjt.splitwise.repository.wrappers.UserWrapper;
import bg.sofia.uni.fmi.mjt.splitwise.containers.User;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class UserRepository {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Path path;
    private Map<String, User> users = new HashMap<>();

    public UserRepository(Path path) {
        this.path = path;
        load();
    }

    private void load() {
        if (!path.toFile().exists()) {
            save();
            return;
        }
        try {
            UserWrapper wrapper = objectMapper.readValue(path.toFile(), UserWrapper.class);
            this.users = wrapper.users();

        } catch (IOException e) {
            users = new HashMap<>();
        }
    }

    public void save() {
        try {
            objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValue(Files.newBufferedWriter(path), new UserWrapper(users));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public User getUser(String username) {
        return users.get(username);
    }

    public void addUser(User user) {
        users.put(user.getUsername(), user);
    }

    public void removeUser(String username) {
        users.remove(username);
    }

    public Map<String, User> getAllUsers() {
        return users;
    }

}
