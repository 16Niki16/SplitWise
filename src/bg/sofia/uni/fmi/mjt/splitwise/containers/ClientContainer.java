package bg.sofia.uni.fmi.mjt.splitwise.containers;

import bg.sofia.uni.fmi.mjt.splitwise.streams.ReaderWriterCreator;
import bg.sofia.uni.fmi.mjt.splitwise.user.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class ClientContainer {
    private static final int STARTING_CAPACITY = 0;
    private static final int MAX_CAPACITY = 50;
    private final Set<User> users;
    private final ReaderWriterCreator usersDirectory;

    public ClientContainer(ReaderWriterCreator usersDirectory) {
        this.usersDirectory = usersDirectory;
        users = new HashSet<>();
    }

    public User getUser(String name) {
        return users.stream()
                .filter(p -> p.getUsername().equals(name.strip()))
                .findAny()
                .orElse(null);
    }

    public void addUser(User user) {
        users.add(user);
    }

    public void connectUserAtStart() throws IOException {
        int capacity = STARTING_CAPACITY;
        try (BufferedReader reader = new BufferedReader(usersDirectory.getRead())) {
            String line;
            while ((line = reader.readLine()) != null && capacity != MAX_CAPACITY) {
                users.add(User.of(line));
                ++capacity;
            }
        }
    }
}
