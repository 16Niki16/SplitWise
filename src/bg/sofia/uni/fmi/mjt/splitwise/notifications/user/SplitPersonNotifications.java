package bg.sofia.uni.fmi.mjt.splitwise.notifications.user;

import bg.sofia.uni.fmi.mjt.splitwise.command.Command;
import bg.sofia.uni.fmi.mjt.splitwise.helpers.Helpers;
import bg.sofia.uni.fmi.mjt.splitwise.streams.ReaderWriterCreator;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.AMOUNT;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.TWO;

public class SplitPersonNotifications implements SplitPersonNotificationsAPI {
    private static final int NAME = 0;
    private static final int FRIEND_NAME = 1;
    static final int FRIEND_PAY = 2;
    private final ReaderWriterCreator notificationsDirectory;
    private final ReaderWriterCreator tempNotif;

    public SplitPersonNotifications(ReaderWriterCreator notificationsDirectory, ReaderWriterCreator tempNotif) {
        this.notificationsDirectory = notificationsDirectory;
        this.tempNotif = tempNotif;
    }

    @Override
    public void addNotificationFriendSplit(Command command) {
        try {

            appendNotification(command, notificationsDirectory);

            appendNotification(command, tempNotif);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void appendNotification(Command command, ReaderWriterCreator creator) throws IOException {
        List<String> lines = new ArrayList<>();
        boolean everReached = false;

        try (BufferedReader r = new BufferedReader(creator.getRead())) {

            String line;
            boolean reachedSection = false;
            while ((line = r.readLine()) != null) {
                String[] checkName = line.split(":");
                if (checkName[NAME].equals("name") && checkName[FRIEND_NAME].equals(command.args()[FRIEND_PAY])) {
                    lines.add(line);
                    reachedSection = true;
                    everReached = true;
                } else if (reachedSection) {
                    appendInList(lines, line, command);
                    reachedSection = false;
                } else {
                    lines.add(line);
                }
            }
            if (!everReached) {
                appendAtEnd(command, creator);
            } else {
                Helpers.addInformation(lines, creator);
            }
        }
    }

    private void appendInList(List<String> lines, String line, Command command) {
        if (!line.equals("Friends:")) {
            lines.add("Friends:");
            lines.add(String.format(String.format("You owe %s %.2f LV[%s]",
                    command.line(), Double.parseDouble(command.args()[AMOUNT]) / TWO, Helpers.getReason(command))));
            lines.add(line);
        } else {
            lines.add(line);
            lines.add(String.format(String.format("You owe %s %.2f LV[%s]",
                    command.line(), Double.parseDouble(command.args()[AMOUNT]) / TWO, Helpers.getReason(command))));
        }
    }

    private void appendAtEnd(Command command, ReaderWriterCreator creator) throws IOException {
        String build = String.format("name:%s\n", command.args()[FRIEND_PAY]) +
                String.format("Friends:\nYou owe %s %.2f[%s].",
                        command.line(), Double.parseDouble(command.args()[AMOUNT]) / TWO, Helpers.getReason(command));
        Helpers.appendToFile(build, creator);
    }
}
