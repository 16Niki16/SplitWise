import bg.sofia.uni.fmi.mjt.splitwise.command.CommandRegistry;
import bg.sofia.uni.fmi.mjt.splitwise.currency.ExchangeRate;
import bg.sofia.uni.fmi.mjt.splitwise.database.Database;
import bg.sofia.uni.fmi.mjt.splitwise.repository.files.DebtsRepositoryFile;
import bg.sofia.uni.fmi.mjt.splitwise.repository.files.GroupRepositoryFile;
import bg.sofia.uni.fmi.mjt.splitwise.repository.files.NotificationsRepositoryFile;
import bg.sofia.uni.fmi.mjt.splitwise.repository.files.UserRepositoryFile;
import bg.sofia.uni.fmi.mjt.splitwise.request.RequestHandler;
import bg.sofia.uni.fmi.mjt.splitwise.server.Server;
import bg.sofia.uni.fmi.mjt.splitwise.server.SessionsManager;
import bg.sofia.uni.fmi.mjt.splitwise.service.ApplicationServices;
import bg.sofia.uni.fmi.mjt.splitwise.service.CurrencyService;
import bg.sofia.uni.fmi.mjt.splitwise.service.DebtsService;
import bg.sofia.uni.fmi.mjt.splitwise.service.ExceptionsService;
import bg.sofia.uni.fmi.mjt.splitwise.service.GroupService;
import bg.sofia.uni.fmi.mjt.splitwise.service.NotificationsService;
import bg.sofia.uni.fmi.mjt.splitwise.service.UserService;

import java.net.http.HttpClient;
import java.nio.file.Path;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        SessionsManager sessionsManager = new SessionsManager();

        UserRepositoryFile userRepository = new UserRepositoryFile(Path.of("DataFiles", "Users"));
        GroupRepositoryFile groupRepository = new GroupRepositoryFile(Path.of("DataFiles", "Groups"));
        DebtsRepositoryFile debtsRepository = new DebtsRepositoryFile(Path.of("DataFiles", "Debts"));
        NotificationsRepositoryFile notificationsRepository =
            new NotificationsRepositoryFile(Path.of("DataFiles", "Notifications"));
        ExchangeRate exchangeRate = new ExchangeRate(HttpClient.newBuilder().build());

        UserService userService = new UserService(userRepository);
        GroupService groupService = new GroupService(groupRepository);
        DebtsService debtsService = new DebtsService(debtsRepository);
        CurrencyService currencyService = new CurrencyService(exchangeRate);
        NotificationsService notificationsService = new NotificationsService(notificationsRepository);
        ExceptionsService exceptionsService = new ExceptionsService();

        ApplicationServices applicationServices = new ApplicationServices(
            userService, groupService, debtsService, currencyService, notificationsService, exceptionsService);
        CommandRegistry commandRegistry = new CommandRegistry(applicationServices, sessionsManager);

        RequestHandler requestHandler = new RequestHandler(commandRegistry, sessionsManager, userService);
        Server server = new Server(requestHandler);
        server.start();

        try {
            Database.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException("gr", e);
        }
    }
}