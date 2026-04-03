package bg.sofia.uni.fmi.mjt.splitwise.notifications;

import bg.sofia.uni.fmi.mjt.splitwise.command.CommandLine;
import bg.sofia.uni.fmi.mjt.splitwise.helpers.ExceptionFormater;
import bg.sofia.uni.fmi.mjt.splitwise.helpers.Helpers;
import bg.sofia.uni.fmi.mjt.splitwise.streams.ReaderWriterCreator;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.USER;

public class HelpersNotifications {
    private static final int FRIEND_NAME = 1;
    private static final int NAME = 0;
    static final int FRIEND_PAY = 2;

    public static void appendNotification(CommandLine command, String message,
                                          ReaderWriterCreator creator) throws IOException {
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
                    HelpersNotifications.appendInList(lines, line, message);
                    reachedSection = false;

                } else {
                    lines.add(line);
                }
            }
            if (!everReached) {
                HelpersNotifications.appendAtEnd(command.args()[FRIEND_PAY], message, creator);
            } else {
                Helpers.addInformation(lines, creator);
            }
        }
    }

    public static void appendInList(List<String> lines, String line, String message) {
        if (!line.equals("Friends:")) {
            lines.add("Friends:");
            lines.add(message);
            lines.add(line);
        } else {
            lines.add(line);
            lines.add(message);
        }
    }

    public static void appendAtEnd(String friendName, String message, ReaderWriterCreator creator) throws IOException {
        Helpers.appendToFile(String.format("name:%s\nFriends:\n%s", friendName, message), creator);
    }

    public static void appendAtPosition(String friend, String message, ReaderWriterCreator creator) throws IOException {
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
                    lines.add(message);

                } else if (reachedSection && checkName[NAME].equals("name")) {
                    reachedSection = false;
                    lines.add("Groups:");
                    lines.add(message);
                    lines.add(line);

                } else {
                    lines.add(line);
                }
            }
            if (!everReached) {
                Helpers.appendToFile(String.format("name:%s\nGroups:\n%s", friend, message), creator);
            } else {
                Helpers.addInformation(lines, creator);
            }
        }
    }

    public static String getNotifications(String username, ReaderWriterCreator search, ReaderWriterCreator exception) {
        StringBuilder build = new StringBuilder("*** Notifications ***\n");
        List<String> updatedList = new ArrayList<>();
        try (BufferedReader r = new BufferedReader(search.getRead())) {
            String line;
            boolean isName = false;
            while ((line = r.readLine()) != null) {
                String[] checkName = line.split(":");
                if (checkName[USER].equals("name") && checkName[FRIEND_NAME].equals(username)) {
                    isName = true;
                } else if (checkName[USER].equals("name")) {
                    updatedList.add(line);
                    isName = false;
                } else if (isName) {
                    build.append(line).append('\n');
                } else {
                    updatedList.add(line);
                }
            }
            Helpers.addInformation(updatedList, search);
        } catch (IOException e) {
            ExceptionFormater.exceptionAdd(username, "Could not get the notification", e.getStackTrace(), exception);
            throw new RuntimeException("Unsuccessfully send notifications");
        }
        return (build.toString().equals("*** Notifications ***\n")) ? "No notifications!" : build.toString();
    }
}
