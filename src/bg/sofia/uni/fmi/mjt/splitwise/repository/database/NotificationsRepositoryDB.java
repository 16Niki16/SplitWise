package bg.sofia.uni.fmi.mjt.splitwise.repository.database;

import bg.sofia.uni.fmi.mjt.splitwise.database.Database;
import bg.sofia.uni.fmi.mjt.splitwise.notifications.Notification;
import bg.sofia.uni.fmi.mjt.splitwise.repository.NotificationsRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NotificationsRepositoryDB implements NotificationsRepository {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public void addNotification(String owner, Notification notification) {
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "INSERT INTO notifications(owner, type, payload) VALUES (?, ?, ?)")) {

            stmt.setString(1, owner);
            stmt.setString(2, notification.getClass().getSimpleName());

            String json = MAPPER.writeValueAsString(notification);
            stmt.setString(3, json);

            stmt.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<Notification> getNotifications(String owner) {
        List<Notification> result = new ArrayList<>();

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT payload FROM notifications WHERE owner = ?")) {

            stmt.setString(1, owner);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String json = rs.getString("payload");

                Notification notification = MAPPER.readValue(json, Notification.class);

                result.add(notification);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        deleteNotifications(owner);

        return result;
    }

    private void deleteNotifications(String owner) {
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "DELETE FROM notifications WHERE owner = ?")) {

            stmt.setString(1, owner);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
