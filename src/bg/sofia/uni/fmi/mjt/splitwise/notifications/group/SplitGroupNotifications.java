package bg.sofia.uni.fmi.mjt.splitwise.notifications.group;

import bg.sofia.uni.fmi.mjt.splitwise.command.Command;
import bg.sofia.uni.fmi.mjt.splitwise.helpers.Helpers;
import bg.sofia.uni.fmi.mjt.splitwise.streams.ReaderWriterCreator;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.AMOUNT;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.TWO;

public class SplitGroupNotifications implements SplitGroupNotificationAPI {

    private static final int NAME = 0;
    private static final int FRIEND_NAME = 1;
    private static final int GROUP_NAME = 2;
    private final ReaderWriterCreator notificationsDirectory;
    private final ReaderWriterCreator tempNotif;

    public SplitGroupNotifications(ReaderWriterCreator notificationsDirectory, ReaderWriterCreator tempNotif) {
        this.notificationsDirectory = notificationsDirectory;
        this.tempNotif = tempNotif;
    }

    @Override
    public void appendToGroupSplit(Command command, String friend) {
        try {
            double amount = Double.parseDouble(command.args()[AMOUNT]);

            appendAtPositionSplit(command, amount, friend, notificationsDirectory);

            appendAtPositionSplit(command, amount, friend, tempNotif);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void appendAtPositionSplit(Command command, double amount, String friend, ReaderWriterCreator creator)
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
                    lines.add(appendNewInfoSplit(command, amount));

                } else if (reachedSection && checkName[NAME].equals("name")) {
                    reachedSection = false;
                    lines.add("Groups:");
                    lines.add(appendNewInfoSplit(command, amount));
                    lines.add(line);

                } else {
                    lines.add(line);
                }
            }
            if (!everReached) {
                appendAtEnd(command, amount, friend, creator);
            } else {
                Helpers.addInformation(lines, creator);
            }
        }
    }

    private String appendNewInfoSplit(Command command,  double amount) {
        return String.format("*%s - You owe %s %2f LV[%s]",
                command.args()[TWO], command.line(), amount, Helpers.getReason(command));
    }

    private void appendAtEnd(Command command, double amount, String friend, ReaderWriterCreator creator)
            throws IOException {
        String build = String.format("name:%s\n", friend) +
                String.format("Groups:\n*%s - You owe %s %.2f LV[%s]", command.args()[GROUP_NAME],
                        command.line(), amount, Helpers.getReason(command));
        Helpers.appendToFile(build, creator);
    }
}
