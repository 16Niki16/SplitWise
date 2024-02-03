package bg.sofia.uni.fmi.mjt.splitwise.notifications;

import bg.sofia.uni.fmi.mjt.splitwise.command.Command;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.AMOUNT;

public class Notification implements NotificationAPI {
    private static final int NAME = 0;
    private static final int FRIEND_NAME = 1;
    private static final int FRIEND_PAY = 2;
    private static final int REASON = 3;
    private String notificationsDirectory;

    public Notification(String notificationsDirectory) {
        this.notificationsDirectory = notificationsDirectory;
    }

    @Override
    public void addNotificationFriendPayment(Command command) {
        try {
            if (!checkSectionAlreadyExist(command.args()[FRIEND_PAY])) {
                appendAtEnd(command.line(), command.args()[AMOUNT], command.args()[FRIEND_PAY], true, " ");
            } else {
                appendAtPositionPayment(command);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void addNotificationFriendSplit(Command command) {
        try {
            if (!checkSectionAlreadyExist(command.args()[FRIEND_PAY])) {
                appendAtEnd(command.line(), command.args()[AMOUNT], command.args()[FRIEND_PAY], false,
                    command.args()[REASON]);
            } else {
                appendAtPositionSplit(command);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void appendAtPositionPayment(Command command) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader r = new BufferedReader(new FileReader(notificationsDirectory))) {
            String line;
            boolean reachedSection = false;
            while ((line = r.readLine()) != null) {
                String[] checkName = line.split(":");
                if (checkName[NAME].trim().equals("name") &&
                    checkName[FRIEND_NAME].trim().equals(command.args()[FRIEND_PAY])) {
                    reachedSection = true;
                    lines.add(line);
                } else if (reachedSection && line.trim().equals("Friends:")) {
                    reachedSection = false;
                    lines.add(line);
                    lines.add(String.format("%s approved your payment %s LV.", command.line(), command.args()[AMOUNT]));
                } else {
                    lines.add(line);
                }
            }
            addInformation(lines);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void appendAtPositionSplit(Command command) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader r = new BufferedReader(new FileReader(notificationsDirectory))) {
            String line;
            boolean reachedSection = false;
            while ((line = r.readLine()) != null) {
                String[] checkName = line.split(":");
                if (checkName[NAME].trim().equals("name") &&
                    checkName[FRIEND_NAME].trim().equals(command.args()[FRIEND_PAY])) {
                    lines.add(line);
                    reachedSection = true;
                } else if (reachedSection && line.trim().equals("Friends:")) {
                    reachedSection = false;
                    lines.add(line);
                    lines.add(String.format(String.format("You owes %s %s[%s]", command.line(), command.args()[AMOUNT],
                        command.args()[REASON])));
                } else {
                    lines.add(line);
                }
            }
            addInformation(lines);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void appendAtEnd(String name, String amount, String friend, boolean paid, String reason)
        throws IOException {
        try (BufferedWriter wr = new BufferedWriter(new FileWriter(notificationsDirectory, true))) {
            StringBuilder build = new StringBuilder(String.format("name: %s\n", friend));
            if (paid) {
                build.append(String.format("Friends:\n%s approved your payment %s LV.", name, amount));
            } else {
                build.append(String.format("Friends:\nYou owes %s %s[%s]", name, amount, reason));
            }
            wr.write(String.valueOf(build));
            wr.newLine();
        }
    }

    private boolean checkSectionAlreadyExist(String friend) throws IOException {
        try (BufferedReader r = new BufferedReader(new FileReader(notificationsDirectory))) {
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
        try (BufferedWriter wr = new BufferedWriter(new FileWriter(notificationsDirectory, false))) {
            for (String line : lines) {
                wr.write(line);
                wr.newLine();
            }
        }
    }
}
