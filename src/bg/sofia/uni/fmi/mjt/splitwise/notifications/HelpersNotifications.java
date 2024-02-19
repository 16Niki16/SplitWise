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
                if (checkName[NAME].strip().equals("name") && checkName[FRIEND_NAME].strip().equals(friend)) {
                    return false;
                }
            }
        }
        return true;
    }

    public static String getNotifications(String username, ReaderWriterCreator search, ReaderWriterCreator exception) {
        StringBuilder build = new StringBuilder();
        List<String> updatedList = new ArrayList<>();
        try (BufferedReader r = new BufferedReader(search.getRead())) {
            String line;
            boolean isName = false;
            while ((line = r.readLine()) != null) {
                String[] checkName = line.split(":");
                if (checkName[USER].strip().equals("name") && checkName[FRIEND_NAME].strip().equals(username)) {
                    isName = true;
                    build.append("*** Notifications ***\n");
                } else if (isName) {
                    if (checkName[USER].strip().equals("name")) {
                        updatedList.add(line);
                        isName = false;
                    } else {
                        build.append(line).append('\n');
                    }
                } else {
                    updatedList.add(line);
                }
            }
            Helpers.addInformation(updatedList, search);
        } catch (IOException e) {
            ExceptionFormater.exceptionAdd(username, "Could not get the notification", e.getStackTrace(), exception);
            throw new RuntimeException("Unsuccessfully send notifications");
        }
        return (build.isEmpty()) ? "No notifications!" : build.toString();
    }
}
