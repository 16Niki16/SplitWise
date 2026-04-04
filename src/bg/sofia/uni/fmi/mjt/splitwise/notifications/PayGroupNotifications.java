package bg.sofia.uni.fmi.mjt.splitwise.notifications;

import bg.sofia.uni.fmi.mjt.splitwise.command.CommandLine;

import java.io.IOException;

import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.AMOUNT;

public class PayGroupNotifications implements Notification {
    private static final int GROUP_PAYMENT = 3;

    public PayGroupNotifications() {
    }

    public void appendToGroupPayment(CommandLine command, String friend) {
        try {
            String message = String.format("*%s - %s approved your payment %.2f LV.", command.args()[GROUP_PAYMENT],
                    command.line(), Double.parseDouble(command.args()[AMOUNT]));

            HelpersNotifications.appendAtPosition(friend, message, notificationsDirectory);
            HelpersNotifications.appendAtPosition(friend, message, tempNotif);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
