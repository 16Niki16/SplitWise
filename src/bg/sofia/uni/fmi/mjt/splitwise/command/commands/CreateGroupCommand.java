package bg.sofia.uni.fmi.mjt.splitwise.command.commands;

import bg.sofia.uni.fmi.mjt.splitwise.command.data.CreateGroupData;
import bg.sofia.uni.fmi.mjt.splitwise.containers.Group;
import bg.sofia.uni.fmi.mjt.splitwise.containers.User;
import bg.sofia.uni.fmi.mjt.splitwise.response.CreateGroupResponse;
import bg.sofia.uni.fmi.mjt.splitwise.response.Response;
import bg.sofia.uni.fmi.mjt.splitwise.service.ApplicationServices;
import bg.sofia.uni.fmi.mjt.splitwise.service.GroupService;
import bg.sofia.uni.fmi.mjt.splitwise.service.UserService;
import lombok.AllArgsConstructor;

import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
public class CreateGroupCommand implements Command {
    private final CreateGroupData createGroupData;
    private final ApplicationServices applicationServices;

    public Response execute(User creator) {
        UserService userService = applicationServices.getUserService();
        GroupService groupService = applicationServices.getGroupService();

        Set<User> participantsAccounts = createGroupData.participants().stream()
                .map(userService::getUserByUsername)
                .collect(Collectors.toSet());

        participantsAccounts.add(creator);

        Group group = new Group(createGroupData.groupName(), creator.getUsername(), createGroupData.participants());
        groupService.addNewGroup(group);
        participantsAccounts.forEach(user -> user.addGroup(group.getGroupName()));
        userService.updateFile();
        groupService.updateFile();

        return CreateGroupResponse.of(createGroupData.groupName());
    }
}
