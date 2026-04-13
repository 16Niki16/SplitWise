package bg.sofia.uni.fmi.mjt.splitwise.repository.files;

import bg.sofia.uni.fmi.mjt.splitwise.notifications.Notification;
import bg.sofia.uni.fmi.mjt.splitwise.repository.NotificationsRepository;
import bg.sofia.uni.fmi.mjt.splitwise.repository.wrappers.NotificationsWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationsRepositoryFile implements NotificationsRepository {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Path path;
    private Map<String, List<Notification>> notifications = new HashMap<>();

    public NotificationsRepositoryFile(Path path) {
        this.path = path;
        load();
    }

    private void load() {
        if (!path.toFile().exists()) {
            save();
            return;
        }
        try {
            NotificationsWrapper wrapper = objectMapper.readValue(path.toFile(), NotificationsWrapper.class);
            this.notifications = wrapper.notifications();

        } catch (IOException e) {
            this.notifications = new HashMap<>();
        }
    }

    private void save() {
        try {
            objectMapper.writerWithDefaultPrettyPrinter()
                .writeValue(Files.newBufferedWriter(path), new NotificationsWrapper(this.notifications));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Notification> getNotifications(String notificationsByUser) {
        List<Notification> userNotifications = this.notifications.get(notificationsByUser);
        if (userNotifications == null) {
            return Collections.emptyList();
        }
        this.notifications.remove(notificationsByUser);
        save();
        return userNotifications;
    }

    public void addNotification(String username, Notification notification) {
        notifications
            .computeIfAbsent(username, k -> new ArrayList<>())
            .add(notification);
        save();
    }
}
