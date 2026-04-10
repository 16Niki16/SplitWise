package bg.sofia.uni.fmi.mjt.splitwise.repository.database;

import bg.sofia.uni.fmi.mjt.splitwise.containers.Debt;
import bg.sofia.uni.fmi.mjt.splitwise.database.Database;
import bg.sofia.uni.fmi.mjt.splitwise.repository.DebtsRepository;
import bg.sofia.uni.fmi.mjt.splitwise.repository.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DebtsRepositoryDB implements Repository, DebtsRepository {
    @Override
    public Debt findDebt(String from, String to) {
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT from_user, to_user, amount FROM debts WHERE from_user = ? AND to_user = ?")) {
            stmt.setString(1, from);
            stmt.setString(2, to);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Debt(rs.getString("from_user"), rs.getString("to_user"), rs.getBigDecimal("amount"));
            }

            return null;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addDebt(Debt debt) {
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "INSERT INTO debts(from_user, to_user, amount) VALUES (?, ?, ?) " +
                     "ON CONFLICT(from_user, to_user) DO UPDATE SET amount = excluded.amount")) {
            stmt.setString(1, debt.getFrom());
            stmt.setString(2, debt.getTo());
            stmt.setBigDecimal(3, debt.getAmount());
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void removeDebt(Debt debt) {
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "DELETE FROM debts WHERE from_user = ? AND to_user = ?")) {
            stmt.setString(1, debt.getFrom());
            stmt.setString(2, debt.getTo());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Debt> getAllDebts() {
        List<Debt> debts = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT from_user, to_user, amount FROM debts");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                debts.add(new Debt(
                    rs.getString("from_user"),
                    rs.getString("to_user"),
                    rs.getBigDecimal("amount")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return debts;
    }
}
