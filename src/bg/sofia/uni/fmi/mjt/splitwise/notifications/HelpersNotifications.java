package bg.sofia.uni.fmi.mjt.splitwise.notifications;

import bg.sofia.uni.fmi.mjt.splitwise.helpers.ExceptionFormater;
import bg.sofia.uni.fmi.mjt.splitwise.helpers.Helpers;
import bg.sofia.uni.fmi.mjt.splitwise.streams.ReaderWriterCreator;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.USER;

public class HelpersNotifications {
    private static final int NAME = 0;
    private static final int FRIEND_NAME = 1;

    public static boolean isSectionExistNotification(String friend, ReaderWriterCreator reader) throws IOException {
        try (BufferedReader r = new BufferedReader(reader.getRead())) {
            String line;
            while ((line = r.readLine()) != null) {
                String[] checkName = line.split(":");
                if (checkName[NAME].equals("name") && checkName[FRIEND_NAME].equals(friend)) {
                    return false;
                }
            }
        }
        return true;
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
