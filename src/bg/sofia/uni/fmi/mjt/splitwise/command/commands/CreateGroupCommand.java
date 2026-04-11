package bg.sofia.uni.fmi.mjt.splitwise.command.commands;

import bg.sofia.uni.fmi.mjt.splitwise.client.request.dto.CreateGroupData;
import bg.sofia.uni.fmi.mjt.splitwise.client.request.dto.Data;
import bg.sofia.uni.fmi.mjt.splitwise.containers.Group;
import bg.sofia.uni.fmi.mjt.splitwise.containers.User;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.DataException;
import bg.sofia.uni.fmi.mjt.splitwise.response.CreateGroupResponse;
import bg.sofia.uni.fmi.mjt.splitwise.response.Response;
import bg.sofia.uni.fmi.mjt.splitwise.response.ResponseData;
import bg.sofia.uni.fmi.mjt.splitwise.server.SessionsManager;
import bg.sofia.uni.fmi.mjt.splitwise.service.ApplicationServices;
import bg.sofia.uni.fmi.mjt.splitwise.service.GroupService;
import bg.sofia.uni.fmi.mjt.splitwise.service.UserService;
import lombok.AllArgsConstructor;

import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
public class CreateGroupCommand implements Command<CreateGroupData> {
    private final ApplicationServices applicationServices;
    private final SessionsManager sessionsManager;

    public ResponseData execute(String token, CreateGroupData createGroupData) {
        UserService userService = applicationServices.getUserService();
        GroupService groupService = applicationServices.getGroupService();
        User creator = sessionsManager.getUserSession(token);

        Set<User> participantsAccounts = createGroupData.participants().stream()
            .map(userService::getUserByUsername)
            .collect(Collectors.toSet());

        participantsAccounts.add(creator);

        Group group = new Group(createGroupData.groupName(), creator.getUsername(), createGroupData.participants());
        groupService.addNewGroup(group);
        participantsAccounts.forEach(user -> user.addGroup(group.getGroupName()));

        return CreateGroupResponse.of(createGroupData.groupName());
    }
}
