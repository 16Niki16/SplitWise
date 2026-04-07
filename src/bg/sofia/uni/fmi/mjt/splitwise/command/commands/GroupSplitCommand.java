package bg.sofia.uni.fmi.mjt.splitwise.command.commands;

import bg.sofia.uni.fmi.mjt.splitwise.containers.Group;
import bg.sofia.uni.fmi.mjt.splitwise.containers.User;
import bg.sofia.uni.fmi.mjt.splitwise.notifications.Notification;
import bg.sofia.uni.fmi.mjt.splitwise.notifications.SplitGroupNotification;
import bg.sofia.uni.fmi.mjt.splitwise.response.Response;
import bg.sofia.uni.fmi.mjt.splitwise.service.ApplicationServices;
import bg.sofia.uni.fmi.mjt.splitwise.service.CurrencyService;
import bg.sofia.uni.fmi.mjt.splitwise.service.DebtsService;
import bg.sofia.uni.fmi.mjt.splitwise.service.GroupService;
import bg.sofia.uni.fmi.mjt.splitwise.service.NotificationsService;
import bg.sofia.uni.fmi.mjt.splitwise.service.UserService;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
public class GroupSplitCommand implements Command {
    private String groupName;
    private BigDecimal amountToAdd;
    private String reason;
    private ApplicationServices applicationServices;

    @Override
    public Response execute(User user) {
        UserService userService = applicationServices.getUserService();
        GroupService groupService = applicationServices.getGroupService();
        DebtsService debtsService = applicationServices.getDebtsService();
        CurrencyService currencyService = applicationServices.getCurrencyService();
        NotificationsService notificationsService = applicationServices.getNotificationsService();

        Group group = groupService.getGroupByName(groupName);
        Set<String> participants = group.getParticipants();
        Set<User> users = participants.stream()
            .map(userService::getUserByUsername)
            .collect(Collectors.toSet());
        BigDecimal splitAmount = amountToAdd.divide(BigDecimal.valueOf(participants.size()));
        BigDecimal amountInBaseCurrency = currencyService.transformToBaseCurrency(splitAmount, user.getCurrency());

        users.forEach((participant) -> {
            if (!participant.getUsername().equals(user.getUsername())) {
                debtsService.addDebt(participant.getUsername(), user.getUsername(), amountInBaseCurrency);
                BigDecimal amountInPersonCurrency =
                    currencyService.transformAmount(splitAmount, user.getCurrency(), participant.getCurrency());
                Notification groupSplitNotification =
                    new SplitGroupNotification(groupName, user.getUsername(), amountInPersonCurrency, reason,
                        participant.getCurrency());
                notificationsService.addNotification(participant.getUsername(), groupSplitNotification);
            }
        });
        return "Successful amount split";
    }
}
