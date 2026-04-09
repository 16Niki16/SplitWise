package bg.sofia.uni.fmi.mjt.splitwise.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
    private static final String URL = "jdbc:sqlite:splitwise.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    public static void createTables() {
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("""
                    CREATE TABLE IF NOT EXIST users (
                          username TEXT PRIMARY KEY,
                          password TEXT NOT NULL,
                          currency TEXT NOT NULL
                      );
                """);

            stmt.execute("""
                    CREATE TABLE IF NOT EXIST friends (
                          user TEXT NOT NULL,
                          friend TEXT NOT NULL,
                          PRIMARY KEY (user, friend),
                          FOREIGN KEY (user) REFERENCES users(username),
                          FOREIGN KEY (friend) REFERENCES users(username)
                      );
                """);

            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS debts (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        from_user TEXT,
                        to_user TEXT,
                        amount REAL
                    );
                """);

            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS notifications (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        owner TEXT,
                        type TEXT,
                        payload TEXT
                    );
                """);
            stmt.execute("""
                CREATE TABLE IF NOT EXIST groups (
                    group_name TEXT PRIMARY KEY,
                    creator TEXT NOT NULL,
                    FOREIGN KEY (creator) REFERENCES users(username)
                );
                """);

            stmt.execute("""
                CREATE TABLE IF NOT EXIST group_participants (
                    group_name TEXT,
                    username TEXT,
                    PRIMARY KEY (group_name, username),
                    FOREIGN KEY (group_name) REFERENCES groups(group_name),
                    FOREIGN KEY (username) REFERENCES users(username)
                );
                """);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}