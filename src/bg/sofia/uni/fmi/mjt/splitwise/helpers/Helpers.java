package bg.sofia.uni.fmi.mjt.splitwise.helpers;

import bg.sofia.uni.fmi.mjt.splitwise.command.Command;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.FriendNotRegisteredException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.GroupDoesNotExistException;
import bg.sofia.uni.fmi.mjt.splitwise.streams.ReaderWriterCreator;
import bg.sofia.uni.fmi.mjt.splitwise.user.User;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.REASON;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.USER;

public class Helpers {
    private static final int GROUP_NAME = 0;

    public static void addInformation(List<String> lines, ReaderWriterCreator writer) throws IOException {
        try (BufferedWriter wr = new BufferedWriter(writer.getNotAppend())) {
            for (String line : lines) {
                wr.write(line);
                wr.newLine();
                wr.flush();
            }
        }
    }

    public static void checkInFileGroup(Set<String> users, ReaderWriterCreator creator)
        throws FriendNotRegisteredException, IOException {
        try (BufferedReader r = new BufferedReader(creator.getRead())) {
            String line;
            while ((line = r.readLine()) != null) {
                if (users.isEmpty()) {
                    return;
                }
                String[] user = line.split("\\|");
                users.remove(user[USER]);
            }
            if (users.isEmpty()) {
                return;
            }
            throw new FriendNotRegisteredException(extractNotRegistered(users));
        }
    }

    public static String extractNotRegistered(Set<String> users) {
        StringBuilder build = new StringBuilder("These people are still not registered: ");
        for (String user : users) {
            build.append(user).append(", ");
        }
        return build.substring(0, build.length() - 2);
    }

    public static User checkInFileExtract(String username, ReaderWriterCreator creator)
        throws FriendNotRegisteredException, IOException {
        try (BufferedReader r = new BufferedReader(creator.getRead())) {
            String user;
            while ((user = r.readLine()) != null) {
                String[] splitU = user.split("\\|");
                if (splitU[USER].equals(username)) {
                    return User.of(user);
                }
            }
            throw new FriendNotRegisteredException("This person is not registered yet.");
        }
    }

    public static String getReason(Command command) {
        StringBuilder build = new StringBuilder();
        for (int i = REASON; i < command.args().length; i++) {
            build.append(command.args()[i]).append(" ");
        }
        return build.toString().strip();
    }

    public static boolean checkInFileNoException(String username, ReaderWriterCreator creator) throws IOException {
        try (BufferedReader r = new BufferedReader(creator.getRead())) {
            String user;
            while ((user = r.readLine()) != null) {
                String[] splitU = user.split("\\|");
                if (splitU[USER].equals(username)) {
                    return true;
                }
            }
            return false;
        }
    }

    public static void appendToFile(String information, ReaderWriterCreator directory) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(directory.getAppend())) {
            writer.write(information);
            writer.newLine();
            writer.flush();
        }
    }

    public static List<String> updatedInfo(String user, String receiver, String appendUser, String appendReceiver,
                                           ReaderWriterCreator directory)
        throws IOException {
        try (BufferedReader r = new BufferedReader(directory.getRead())) {
            String readline;
            List<String> newLines = new ArrayList<>();
            while ((readline = r.readLine()) != null) {
                String[] splited = readline.split("\\|");
                if (user.equals(splited[USER])) {
                    newLines.add(appendUser);
                } else if (splited[USER].equals(receiver)) {
                    newLines.add(appendReceiver);
                } else {
                    newLines.add(readline);
                }
            }
            return newLines;
        }
    }

    public static String findFriendLine(Command command, int index, ReaderWriterCreator directory)
        throws IOException, FriendNotRegisteredException {
        try (BufferedReader r = new BufferedReader(directory.getRead())) {
            String line;
            while ((line = r.readLine()) != null) {
                String[] splited = line.split("\\|");
                if (splited[USER].equals(command.args()[index])) {
                    return line;
                }
            }
        }
        throw new FriendNotRegisteredException("This person is still not registered!");
    }

    public static List<String> updatedGroup(Command command, ReaderWriterCreator groupsDirectory, String payment,
                                            int index) throws IOException {
        try (BufferedReader r = new BufferedReader(groupsDirectory.getRead())) {
            String line;
            List<String> lines = new ArrayList<>();
            while ((line = r.readLine()) != null) {
                String[] getData = line.split("\\|");
                if (getData[GROUP_NAME].equals(command.args()[index])) {
                    lines.add(payment);
                } else {
                    lines.add(line);
                }
            }
            return lines;
        }
    }

    public static String findGroupLine(Command command, ReaderWriterCreator groupsDirectory, int index)
        throws IOException, GroupDoesNotExistException {
        try (BufferedReader r = new BufferedReader(groupsDirectory.getRead())) {
            String line;
            while ((line = r.readLine()) != null) {
                String[] getData = line.split("\\|");
                if (getData[GROUP_NAME].equals(command.args()[index])) {
                    return line;
                }
            }
            throw new GroupDoesNotExistException("Group with this name does not exist!");
        }
    }
}
