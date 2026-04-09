package bg.sofia.uni.fmi.mjt.splitwise.repository.database;

import bg.sofia.uni.fmi.mjt.splitwise.containers.User;
import bg.sofia.uni.fmi.mjt.splitwise.database.Database;
import bg.sofia.uni.fmi.mjt.splitwise.repository.Repository;
import bg.sofia.uni.fmi.mjt.splitwise.repository.UserRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class UserRepositoryDB implements Repository, UserRepository {
    @Override
    public User getUser(String username) {
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT u.username, u.password, u.currency WHERE u.username = ?");
             PreparedStatement stmt1 = conn.prepareStatement(
                 "SELECT group_name FROM group_participants WHERE username = ?")) {
            stmt.setString(1, username);
            stmt1.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            ResultSet rs1 = stmt1.executeQuery();

            User user = null;
            Set<String> friends = getFriends(username);
            Set<String> groups = new HashSet<>();

            if (rs.next()) {
                user = new User(rs.getString("username"), rs.getString("password"),
                    new HashSet<>(), new HashSet<>(), rs.getString("currency"));
            }

            while (rs1.next()) {
                String group = rs1.getString("group_name");
                if (group != null) {
                    groups.add(group);
                }
            }

            if (user != null) {
                user.setFriends(friends);
                user.setGroups(groups);
            }

            return user;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
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

    @Override
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

    @Override
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

    public boolean areFriends(String user, String friend) {
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT 1 FROM friends WHERE user = ? AND friend = ?")) {

            stmt.setString(1, user);
            stmt.setString(2, friend);

            ResultSet rs = stmt.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}