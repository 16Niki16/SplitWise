package bg.sofia.uni.fmi.mjt.splitwise.command.commands;

import bg.sofia.uni.fmi.mjt.splitwise.group.Group;
import bg.sofia.uni.fmi.mjt.splitwise.service.GroupService;
import bg.sofia.uni.fmi.mjt.splitwise.service.UserService;
import bg.sofia.uni.fmi.mjt.splitwise.user.User;
import lombok.AllArgsConstructor;

import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
public class CreateGroupCommand implements Command {
    private final String groupName;
    private final Set<String> participants;
    private final GroupService groupService;
    private final UserService userService;

    public String execute(User creator) {
        Set<User> participantsAccounts = participants.stream()
                .map(userService::getUserByUsername)
                .collect(Collectors.toSet());

        participantsAccounts.add(creator);

        Group group = new Group(groupName, creator.getUsername(), participants);
        groupService.addNewGroup(group);
        participantsAccounts.forEach(user -> user.addGroup(group.getGroupId()));
        userService.updateFile();
        groupService.updateFile();

        return "Group is successfully created!";
    }
}
