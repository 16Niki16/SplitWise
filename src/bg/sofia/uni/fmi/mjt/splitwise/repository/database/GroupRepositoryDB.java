package bg.sofia.uni.fmi.mjt.splitwise.repository.database;

import bg.sofia.uni.fmi.mjt.splitwise.containers.Group;
import bg.sofia.uni.fmi.mjt.splitwise.database.Database;
import bg.sofia.uni.fmi.mjt.splitwise.repository.GroupRepository;
import bg.sofia.uni.fmi.mjt.splitwise.repository.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class GroupRepositoryDB implements Repository, GroupRepository {
    @Override
    public void addGroup(Group group) {
        try (Connection conn = Database.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO groups(group_name, creator) VALUES (?, ?)")) {

                stmt.setString(1, group.getGroupName());
                stmt.setString(2, group.getCreator());
                stmt.executeUpdate();
            }

            for (String participant : group.getParticipants()) {
                addParticipant(group.getGroupName(), participant);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void addParticipant(String groupName, String username) {
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "INSERT OR IGNORE INTO group_participants(group_name, username) VALUES (?, ?)")) {

            stmt.setString(1, groupName);
            stmt.setString(2, username);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void removeGroup(String groupName) {
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "DELETE FROM groups WHERE group_name = ?")) {

            stmt.setString(1, groupName);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Group getGroup(String groupName) {
        try (Connection conn = Database.getConnection()) {

            String creator;

            try (PreparedStatement stmt = conn.prepareStatement(
                "SELECT creator FROM groups WHERE group_name = ?")) {

                stmt.setString(1, groupName);
                ResultSet rs = stmt.executeQuery();

                if (!rs.next()) return null;

                creator = rs.getString("creator");
            }

            Set<String> participants = getParticipants(groupName);

            return new Group(groupName, creator, participants);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Set<String> getParticipants(String groupName) {
        Set<String> participants = new HashSet<>();

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT username FROM group_participants WHERE group_name = ?")) {

            stmt.setString(1, groupName);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                participants.add(rs.getString("username"));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return participants;
    }

    public Set<String> getGroupsByUser(String username) {
        Set<String> groups = new HashSet<>();

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT group_name FROM group_participants WHERE username = ?")) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                groups.add(rs.getString("group_name"));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return groups;
    }
}
