import bg.sofia.uni.fmi.mjt.splitwise.command.CommandRegistry;
import bg.sofia.uni.fmi.mjt.splitwise.repository.GroupRepository;
import bg.sofia.uni.fmi.mjt.splitwise.repository.NotificationsRepository;
import bg.sofia.uni.fmi.mjt.splitwise.repository.UserRepository;
import bg.sofia.uni.fmi.mjt.splitwise.server.Server;
import bg.sofia.uni.fmi.mjt.splitwise.server.SessionsManager;
import bg.sofia.uni.fmi.mjt.splitwise.service.ApplicationServices;
import bg.sofia.uni.fmi.mjt.splitwise.service.UserService;

import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {
        SessionsManager sessionsManager = new SessionsManager();
        UserRepository userRepository = new UserRepository(Path.of("DataFiles", "Users"));
        GroupRepository groupRepository = new GroupRepository(Path.of("DataFiles", "Groups"));
        NotificationsRepository notificationsRepository = new NotificationsRepository(Path.of())
        UserService userService = new UserService()
        ApplicationServices applicationServices = new ApplicationServices()
        CommandRegistry commandRegistry = new CommandRegistry()
        Server server = new Server()
    }
}