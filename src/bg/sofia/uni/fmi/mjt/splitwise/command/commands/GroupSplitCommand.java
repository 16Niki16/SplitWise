package bg.sofia.uni.fmi.mjt.splitwise.command.commands;

import bg.sofia.uni.fmi.mjt.splitwise.group.Group;
import bg.sofia.uni.fmi.mjt.splitwise.service.DebtsService;
import bg.sofia.uni.fmi.mjt.splitwise.service.GroupService;
import bg.sofia.uni.fmi.mjt.splitwise.service.UserService;
import bg.sofia.uni.fmi.mjt.splitwise.user.User;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.util.Set;

@AllArgsConstructor
public class GroupSplitCommand implements Command {
    private String groupName;
    private BigDecimal amountToAdd;
    private String reason;
    private GroupService groupService;
    private UserService userService;
    private DebtsService debtsService;

    @Override
    public String execute(User user) {
        Group group = groupService.getGroupByName(groupName);
        Set<String> participants = group.getParticipants();
        BigDecimal splitAmount = amountToAdd.divide(BigDecimal.valueOf(participants.size()));

        participants.forEach((participant) -> {
            if (!participant.equals(user.getUsername())) {
                debtsService.addDebt(participant, user.getUsername(), splitAmount);
            }
        });
        return "amount split";
    }
}
