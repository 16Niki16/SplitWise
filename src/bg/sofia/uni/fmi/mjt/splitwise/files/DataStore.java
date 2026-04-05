package bg.sofia.uni.fmi.mjt.splitwise.files;

import bg.sofia.uni.fmi.mjt.splitwise.group.Group;
import bg.sofia.uni.fmi.mjt.splitwise.user.User;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class DataStore {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Path path;
    private Map<String, User> users = new HashMap<>();
    private Map<String, Group> groups = new HashMap<>();

    public DataStore(Path path) {
        this.path = path;
    }

    private void load() {
        if (!path.toFile().exists()) {
            save();
            return;
        }
        try {
            DataWrapper wrapper = objectMapper.readValue(path.toFile(), DataWrapper.class);
            this.users = wrapper.users();
            this.groups = wrapper.groups();

        } catch (IOException e) {
            users = new HashMap<>();
        }
    }

    public void save() {
        try {
            objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValue(Files.newBufferedWriter(path), new DataWrapper(users, groups));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public User getUser(String username) {
        return users.get(username);
    }

    public void addUser(User user) {
        users.put(user.getUsername(), user);
        save();
    }

    public void removeUser(String username) {
        users.remove(username);
        save();
    }

    public Map<String, User> getAllUsers() {
        return users;
    }
}
