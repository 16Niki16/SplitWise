package bg.sofia.uni.fmi.mjt.splitwise.repository.database;

import bg.sofia.uni.fmi.mjt.splitwise.database.Database;
import bg.sofia.uni.fmi.mjt.splitwise.repository.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class FriendRepositoryDB implements Repository {
    public Set<String> getFriends(String username) {
        Set<String> friends = new HashSet<>();

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT friend FROM friends WHERE user = ?")) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                friends.add(rs.getString("friend"));
            }
            return friends;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void addFriend(String user, String friend) {
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "INSERT OR IGNORE INTO friends(user, friend) VALUES (?, ?)")) {
            stmt.setString(1, user);
            stmt.setString(2, friend);
            stmt.executeUpdate();

            stmt.setString(1, friend);
            stmt.setString(2, user);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void removeFriend(String user, String friend) {
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "DELETE FROM friends WHERE user = ? AND friend = ?")) {
            stmt.setString(1, user);
            stmt.setString(2, friend);
            stmt.executeUpdate();

            stmt.setString(1, friend);
            stmt.setString(2, user);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
