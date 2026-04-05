package bg.sofia.uni.fmi.mjt.splitwise.command.commands;

import bg.sofia.uni.fmi.mjt.splitwise.files.DataStore;
import bg.sofia.uni.fmi.mjt.splitwise.group.Group;
import bg.sofia.uni.fmi.mjt.splitwise.user.User;
import lombok.AllArgsConstructor;

import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
public class CreateGroupCommand implements Command {
    private final String groupName;
    private final Set<String> participants;
    private final DataStore userDataStore;

    public String execute(User creator) {
        Set<User> participantsAccounts = participants.stream()
                .map(userDataStore::getUser)
                .collect(Collectors.toSet());

        participantsAccounts.add(creator);

        Group group = new Group(groupName, creator.getUsername(), participants);
        userDataStore.addGroup(group);
        participantsAccounts.forEach(user -> user.addGroup(group.getGroupId()));

        return "Group is successfully created!";
    }
}
