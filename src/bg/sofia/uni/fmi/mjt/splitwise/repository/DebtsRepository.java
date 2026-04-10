package bg.sofia.uni.fmi.mjt.splitwise.repository;

import bg.sofia.uni.fmi.mjt.splitwise.containers.Debt;

import java.util.List;

public interface DebtsRepository {
    Debt findDebt(String from, String to);

    void removeDebt(Debt debt);

    void addDebt(Debt debt);

    List<Debt> getAllDebts();
}
