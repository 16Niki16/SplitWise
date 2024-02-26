package bg.sofia.uni.fmi.mjt.splitwise.notifications.group;

import bg.sofia.uni.fmi.mjt.splitwise.command.Command;
import bg.sofia.uni.fmi.mjt.splitwise.helpers.Helpers;
import bg.sofia.uni.fmi.mjt.splitwise.streams.ReaderWriterCreator;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.AMOUNT;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.GROUP_NAME;

public class PayGroupNotifications implements PayGroupNotificationsAPI {
    private static final int NAME = 0;
    private static final int FRIEND_NAME = 1;
    private static final int GROUP_PAYMENT = 3;
    private final ReaderWriterCreator notificationsDirectory;
    private final ReaderWriterCreator tempNotif;

    public PayGroupNotifications(ReaderWriterCreator notificationsDirectory, ReaderWriterCreator tempNotif) {
        this.notificationsDirectory = notificationsDirectory;
        this.tempNotif = tempNotif;
    }

    public void appendToGroupPayment(Command command, String friend) {
        try {

            appendAtPositionPayment(command, friend, notificationsDirectory);
            appendAtPositionPayment(command, friend, tempNotif);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void appendAtPositionPayment(Command command, String friend, ReaderWriterCreator creator)
            throws IOException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader r = new BufferedReader(creator.getRead())) {
            String line;
            boolean reachedSection = false;
            boolean everReached = false;
            while ((line = r.readLine()) != null) {
                String[] checkName = line.split(":");
                if (checkName[NAME].equals("name") && checkName[FRIEND_NAME].equals(friend)) {
                    reachedSection = true;
                    everReached = true;
                    lines.add(line);

                } else if (reachedSection && line.equals("Groups:")) {
                    reachedSection = false;
                    lines.add(line);
                    lines.add(appendNewInfoPayment(command));

                } else if (reachedSection && checkName[NAME].equals("name")) {
                    lines.add("Groups:");
                    lines.add(appendNewInfoPayment(command));
                    lines.add(line);
                    reachedSection = false;

                } else {
                    lines.add(line);
                }
            }
            if (!everReached) {
                appendAtEnd(command, friend, creator);
            }
            Helpers.addInformation(lines, creator);
        }
    }

    private String appendNewInfoPayment(Command command) {
        return String.format("*%s - %s approved your payment %.2f LV.", command.args()[GROUP_PAYMENT],
                command.line(), Double.parseDouble(command.args()[AMOUNT]));
    }

    private void appendAtEnd(Command command, String friend, ReaderWriterCreator creator)
            throws IOException {
        String build = String.format("name:%s\n", friend) +
                String.format("Groups:\n*%s - %s approved your payment %.2f LV.", command.args()[GROUP_NAME],
                        command.line(), Double.parseDouble(command.args()[AMOUNT]));
        Helpers.appendToFile(build, creator);
    }
}
