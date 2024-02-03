package bg.sofia.uni.fmi.mjt.splitwise.notifications;

import bg.sofia.uni.fmi.mjt.splitwise.command.Command;
import bg.sofia.uni.fmi.mjt.splitwise.streams.ReaderWriterCreator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.AMOUNT;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.REASON;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.TWO;

public class GroupNotification implements GroupNotificationAPI {
    private static final int NAME = 0;
    private static final int FRIEND_NAME = 1;
    private static final int GROUP_NAME = 2;
    private static final int GROUP_PAYMENT = 3;
    private ReaderWriterCreator notificationsDirectory;

    public GroupNotification(ReaderWriterCreator notificationsDirectory) {
        this.notificationsDirectory = notificationsDirectory;
    }

    public void appendToGroupSplit(Command command, String friend, String amount) {
        try {
            if (!checkSectionAlreadyExist(friend)) {
                appendAtEnd(command.line(), amount, friend, false, command.args()[REASON],
                    command.args()[GROUP_NAME]);
            } else {
                appendAtPositionSplit(command, friend, amount);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void appendToGroupPayment(Command command, String friend) {
        try {
            if (!checkSectionAlreadyExist(friend)) {
                appendAtEnd(command.line(), command.args()[AMOUNT], friend, true, null, command.args()[GROUP_PAYMENT]);
            } else {
                appendAtPositionPayment(command, friend);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void appendAtPositionSplit(Command command, String friend, String amount) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader r = new BufferedReader(notificationsDirectory.getRead())) {
            String line;
            boolean reachedSection = false;
            boolean reachedGroups = false;
            while ((line = r.readLine()) != null) {
                String[] checkName = line.split(":");

                if (checkName[NAME].trim().equals("name") &&
                    checkName[FRIEND_NAME].trim().equals(friend)) {
                    reachedSection = true;
                    lines.add(line);
                } else if (reachedSection && line.trim().equals("Groups:")) {
                    reachedGroups = true;
                    reachedSection = false;
                    lines.add(line);
                } else if (reachedGroups) {
                    appendNewInfoSplit(lines, line, command, amount);
                    reachedGroups = false;
                } else {
                    lines.add(line);
                }
            }
            addInformation(lines);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void appendNewInfoSplit(List<String> lines, String line, Command command, String amount) {
        if (line.trim().equals("No information!")) {
            lines.add(
                String.format("*%s - You owes %s %s LV[%s]", command.args()[TWO], command.line(), amount,
                    command.args()[REASON]));
        } else {
            lines.add(
                String.format("*%s - You owes %s %s LV[%s]", command.args()[TWO], command.line(), amount,
                    command.args()[REASON]));
            lines.add(line);
        }
    }

    private void appendAtPositionPayment(Command command, String friend) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader r = new BufferedReader(notificationsDirectory.getRead())) {
            String line;
            boolean reachedSection = false;
            boolean reachedGroups = false;
            while ((line = r.readLine()) != null) {
                String[] checkName = line.split(":");

                if (checkName[NAME].trim().equals("name") &&
                    checkName[FRIEND_NAME].trim().equals(friend)) {
                    reachedSection = true;
                    lines.add(line);
                } else if (reachedSection && line.trim().equals("Groups:")) {
                    reachedGroups = true;
                    reachedSection = false;
                    lines.add(line);
                } else if (reachedGroups) {
                    appendNewInfoPayment(lines, line, command);
                    reachedGroups = false;
                } else {
                    lines.add(line);
                }
            }
            addInformation(lines);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void appendNewInfoPayment(List<String> lines, String line, Command command) {
        if (line.trim().equals("No information!")) {
            lines.add(String.format("*%s - %s approved your payment %s LV.", command.args()[GROUP_PAYMENT],
                command.line(), command.args()[AMOUNT]));
        } else {
            lines.add(String.format("*%s - %s approved your payment %s LV.", command.args()[GROUP_PAYMENT],
                command.line(), command.args()[AMOUNT]));
            lines.add(line);
        }
    }

    private void appendAtEnd(String name, String amount, String friend, boolean paid, String reason, String groupName)
        throws IOException {
        try (BufferedWriter wr = new BufferedWriter(notificationsDirectory.getAppend())) {
            StringBuilder build = new StringBuilder(String.format("name: %s\n", friend));
            if (paid) {
                build.append(String.format("Groups:\n*%s - %s approved your payment %s LV.", groupName, name, amount));
            } else {
                build.append(String.format("Groups:\n*%s - You owes %s %s LV[%s]", groupName, name, amount, reason));
            }
            wr.write(String.valueOf(build));
            wr.newLine();
        }
    }

    private boolean checkSectionAlreadyExist(String friend) throws IOException {
        try (BufferedReader r = new BufferedReader(notificationsDirectory.getRead())) {
            String line;
            while ((line = r.readLine()) != null) {
                String[] checkName = line.split(":");
                if (checkName[NAME].trim().equals("name") && checkName[FRIEND_NAME].trim().equals(friend.trim())) {
                    return true;
                }
            }
        }
        return false;
    }

    private void addInformation(List<String> lines) throws IOException {
        try (BufferedWriter wr = new BufferedWriter(notificationsDirectory.getNotAppend())) {
            for (String line : lines) {
                wr.write(line);
                wr.newLine();
            }
        }
    }
}
