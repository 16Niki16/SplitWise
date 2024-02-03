package bg.sofia.uni.fmi.mjt.splitwise.notifications;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.List;

public class HelpersNotifications {
    private static final int NAME = 0;
    private static final int FRIEND_NAME = 1;

    public static boolean checkSectionAlreadyExist(String friend, Reader reader) throws IOException {
        try (BufferedReader r = new BufferedReader(reader)) {
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

    public static void addInformation(List<String> lines, Writer writer) throws IOException {
        try (BufferedWriter wr = new BufferedWriter(writer)) {
            for (String line : lines) {
                wr.write(line);
                wr.newLine();
            }
        }
    }
}
