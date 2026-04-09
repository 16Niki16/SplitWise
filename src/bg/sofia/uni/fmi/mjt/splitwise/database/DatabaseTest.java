package bg.sofia.uni.fmi.mjt.splitwise.database;

import bg.sofia.uni.fmi.mjt.splitwise.containers.Debt;
import bg.sofia.uni.fmi.mjt.splitwise.containers.User;
import bg.sofia.uni.fmi.mjt.splitwise.repository.database.DebtsRepositoryDB;
import bg.sofia.uni.fmi.mjt.splitwise.repository.database.UserRepositoryDB;

public class DatabaseTest {
    public static void main(String[] args) {

        // 1. Създаваме таблиците
        Database.createTables();

        UserRepositoryDB userRepo = new UserRepositoryDB();
        DebtsRepositoryDB debtsRepo = new DebtsRepositoryDB();

        // 2. Добавяме users
        userRepo.addUser(new User("niki", "1234", null, null, "EUR"));
        userRepo.addUser(new User("ivan", "1234", null, null, "USD"));

        // 3. Проверка дали са записани
        System.out.println("All users:");
        userRepo.getAllUsers().values()
            .forEach(u -> System.out.println(u.getUsername() + " " + u.getCurrency()));

        // 4. Добавяме дълг
        Debt debt = new Debt("niki", "ivan", new java.math.BigDecimal("50"));
        debtsRepo.addOrUpdateDebt(debt);

        // 5. Взимаме дълга
        System.out.println("\nDebt:");
        Debt found = debtsRepo.findDebt("niki", "ivan");
        if (found != null) {
            System.out.println(found.debtMessage());
        }

        // 6. Тест за UPDATE (merge)
        debtsRepo.addOrUpdateDebt(new Debt("niki", "ivan", new java.math.BigDecimal("100")));
        System.out.println("\nUpdated Debt:");
        System.out.println(debtsRepo.findDebt("niki", "ivan").debtMessage());

        // 7. Тест за DELETE CASCADE
        userRepo.removeUser("ivan");

        System.out.println("\nAfter deleting ivan:");
        Debt afterDelete = debtsRepo.findDebt("niki", "ivan");
        System.out.println(afterDelete == null ? "Debt deleted (CASCADE works)" : "Still exists ❌");
    }
}
