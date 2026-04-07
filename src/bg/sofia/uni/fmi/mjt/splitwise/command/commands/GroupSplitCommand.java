package bg.sofia.uni.fmi.mjt.splitwise.command.commands;

import bg.sofia.uni.fmi.mjt.splitwise.containers.Group;
import bg.sofia.uni.fmi.mjt.splitwise.containers.User;
import bg.sofia.uni.fmi.mjt.splitwise.service.ApplicationServices;
import bg.sofia.uni.fmi.mjt.splitwise.service.CurrencyService;
import bg.sofia.uni.fmi.mjt.splitwise.service.DebtsService;
import bg.sofia.uni.fmi.mjt.splitwise.service.GroupService;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.util.Set;

@AllArgsConstructor
public class GroupSplitCommand implements Command {
    private String groupName;
    private BigDecimal amountToAdd;
    private String reason;
    private ApplicationServices applicationServices;

    @Override
    public String execute(User user) {
        GroupService groupService = applicationServices.getGroupService();
        DebtsService debtsService = applicationServices.getDebtsService();
        CurrencyService currencyService = applicationServices.getCurrencyService();

        Group group = groupService.getGroupByName(groupName);
        Set<String> participants = group.getParticipants();
        BigDecimal splitAmount = amountToAdd.divide(BigDecimal.valueOf(participants.size()));
        BigDecimal amountInBaseCurrency = currencyService.transformToBaseCurrency(splitAmount, user.getCurrency());

        participants.forEach((participant) -> {
            if (!participant.equals(user.getUsername())) {
                debtsService.addDebt(participant, user.getUsername(), amountInBaseCurrency);
            }
        });
        return "amount split";
    }
}
