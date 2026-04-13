package bg.sofia.uni.fmi.mjt.splitwise.command.commands;

import bg.sofia.uni.fmi.mjt.splitwise.client.request.dto.SplitGroupData;
import bg.sofia.uni.fmi.mjt.splitwise.containers.Group;
import bg.sofia.uni.fmi.mjt.splitwise.containers.User;
import bg.sofia.uni.fmi.mjt.splitwise.notifications.Notification;
import bg.sofia.uni.fmi.mjt.splitwise.notifications.SplitGroupNotification;
import bg.sofia.uni.fmi.mjt.splitwise.response.ResponseData;
import bg.sofia.uni.fmi.mjt.splitwise.response.SplitGroupResponse;
import bg.sofia.uni.fmi.mjt.splitwise.server.SessionsManager;
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
public class SplitGroupCommand implements Command<SplitGroupData> {
    private final ApplicationServices applicationServices;
    private final SessionsManager sessionsManager;

    @Override
    public ResponseData execute(String token, SplitGroupData splitGroupData) {
        User user = sessionsManager.getUserSession(token);
        UserService userService = applicationServices.getUserService();
        GroupService groupService = applicationServices.getGroupService();
        DebtsService debtsService = applicationServices.getDebtsService();
        CurrencyService currencyService = applicationServices.getCurrencyService();
        NotificationsService notificationsService = applicationServices.getNotificationsService();

        Group group = groupService.getGroupByName(splitGroupData.groupName());
        Set<String> participants = group.getParticipants();
        Set<User> users = participants.stream()
                .map(userService::getUserByUsername)
                .collect(Collectors.toSet());
        BigDecimal splitAmount = splitGroupData.amount().divide(BigDecimal.valueOf(participants.size()));
        BigDecimal amountInBaseCurrency = currencyService.transformToBaseCurrency(splitAmount, user.getCurrency());

        users.forEach((participant) -> {
            if (!participant.getUsername().equals(user.getUsername())) {
                debtsService.addDebt(participant.getUsername(), user.getUsername(), amountInBaseCurrency);
                BigDecimal amountInPersonCurrency =
                        currencyService.transformAmount(splitAmount, user.getCurrency(), participant.getCurrency());
                Notification groupSplitNotification =
                        new SplitGroupNotification(splitGroupData.groupName(), user.getUsername(),
                                amountInPersonCurrency, splitGroupData.reason(), participant.getCurrency());
                notificationsService.addNotification(participant.getUsername(), groupSplitNotification);
            }
        });

        return SplitGroupResponse.of(splitGroupData.groupName());
    }
}
