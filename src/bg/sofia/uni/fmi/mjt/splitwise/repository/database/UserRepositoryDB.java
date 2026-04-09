package bg.sofia.uni.fmi.mjt.splitwise.repository.database;

import bg.sofia.uni.fmi.mjt.splitwise.containers.User;
import bg.sofia.uni.fmi.mjt.splitwise.database.Database;
import bg.sofia.uni.fmi.mjt.splitwise.repository.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class UserRepositoryDB implements Repository {
    public User getUser(String username) {
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT username, password, currency FROM users WHERE username = ?")) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new User(
                    rs.getString("username"),
                    rs.getString("password"),
                    null, // friends ще се зареждат от FriendRepository
                    null, // groups ще се зареждат от GroupParticipantsRepository
                    rs.getString("currency")
                );
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void addUser(User user) {
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "INSERT INTO users(username, password, currency) VALUES (?, ?, ?)")) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getCurrency());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void removeUser(String username) {
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "DELETE FROM users WHERE username = ?")) {
            stmt.setString(1, username);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String, User> getAllUsers() {
        Map<String, User> users = new HashMap<>();
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT username, password, currency FROM users");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                users.put(rs.getString("username"), new User(
                    rs.getString("username"),
                    rs.getString("password"),
                    null,
                    null,
                    rs.getString("currency")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return users;
    }
}