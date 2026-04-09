package bg.sofia.uni.fmi.mjt.splitwise.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
    private static final String URL = "jdbc:sqlite:splitwise.db";

    public static Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(URL);
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON;");
        }
        return conn;
    }

    public static void createTables() {
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS users (
                        username TEXT PRIMARY KEY,
                        password TEXT NOT NULL,
                        currency TEXT NOT NULL
                    );
                """);

            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS friends (
                        user TEXT NOT NULL,
                        friend TEXT NOT NULL,
                        PRIMARY KEY (user, friend),
                        FOREIGN KEY (user) REFERENCES users(username) ON DELETE CASCADE,
                        FOREIGN KEY (friend) REFERENCES users(username) ON DELETE CASCADE
                    );
                """);

            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS debts (
                        from_user TEXT NOT NULL,
                        to_user TEXT NOT NULL,
                        amount REAL NOT NULL,
                        PRIMARY KEY (from_user, to_user),
                        FOREIGN KEY (from_user) REFERENCES users(username) ON DELETE CASCADE,
                        FOREIGN KEY (to_user) REFERENCES users(username) ON DELETE CASCADE
                    );
                """);

            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS groups (
                        group_name TEXT PRIMARY KEY,
                        creator TEXT NOT NULL,
                        FOREIGN KEY (creator) REFERENCES users(username) ON DELETE CASCADE
                    );
                """);

            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS group_participants (
                        group_name TEXT NOT NULL,
                        username TEXT NOT NULL,
                        PRIMARY KEY (group_name, username),
                        FOREIGN KEY (group_name) REFERENCES groups(group_name) ON DELETE CASCADE,
                        FOREIGN KEY (username) REFERENCES users(username) ON DELETE CASCADE
                    );
                """);

            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS notifications (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        owner TEXT NOT NULL,
                        type TEXT NOT NULL,
                        payload TEXT NOT NULL,
                        FOREIGN KEY (owner) REFERENCES users(username) ON DELETE CASCADE
                    );
                """);

        } catch (SQLException e) {
            throw new RuntimeException("Error creating database tables", e);
        }
    }
}