package bg.sofia.uni.fmi.mjt.splitwise.notifications.user;

import bg.sofia.uni.fmi.mjt.splitwise.command.Command;
import bg.sofia.uni.fmi.mjt.splitwise.helpers.Helpers;
import bg.sofia.uni.fmi.mjt.splitwise.streams.ReaderWriterCreator;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.AMOUNT;

public class PersonPayNotifications implements PersonPayNotificationsAPI {
    private static final int NAME = 0;
    private static final int FRIEND_NAME = 1;
    static final int FRIEND_PAY = 2;
    private final ReaderWriterCreator notificationsDirectory;
    private final ReaderWriterCreator tempNotif;

    public PersonPayNotifications(ReaderWriterCreator notificationsDirectory, ReaderWriterCreator tempNotif) {
        this.notificationsDirectory = notificationsDirectory;
        this.tempNotif = tempNotif;
    }

    @Override
    public void addNotificationFriendPayment(Command command) {
        try {

            appendAtPositionPayment(command, notificationsDirectory);

            appendAtPositionPayment(command, tempNotif);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void appendAtPositionPayment(Command command, ReaderWriterCreator creator) throws IOException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader r = new BufferedReader(creator.getRead())) {
            String line;
            boolean reachedSection = false;
            boolean everReached = false;

            while ((line = r.readLine()) != null) {
                String[] checkName = line.split(":");

                if (checkName[NAME].equals("name") && checkName[FRIEND_NAME].equals(command.args()[FRIEND_PAY])) {
                    reachedSection = true;
                    everReached = true;
                    lines.add(line);
                } else if (reachedSection) {
                    appendInListPaid(lines, line, command);
                    reachedSection = false;
                } else {
                    lines.add(line);
                }
            }
            if (!everReached) {
                appendAtEnd(command, creator);
            }
            Helpers.addInformation(lines, creator);
        }
    }

    private void appendInListPaid(List<String> lines, String line, Command command) {
        if (line.equals("Friends:")) {
            lines.add(line);
            lines.add(String.format("%s approved your payment %.2f LV.", command.line(),
                    Double.parseDouble(command.args()[AMOUNT])));
        } else {
            lines.add("Friends:");
            lines.add(
                    String.format("%s approved your payment %.2f LV.", command.line(),
                            Double.parseDouble(command.args()[AMOUNT])));
            lines.add(line);
        }
    }

    private void appendAtEnd(Command command, ReaderWriterCreator creator)
            throws IOException {
        String build = String.format("name:%s\n", command.args()[FRIEND_PAY]) +
                String.format("Friends:\n%s approved your payment %s LV.",
                        command.line(), Double.parseDouble(command.args()[AMOUNT]));
        Helpers.appendToFile(build, creator);
    }
}
