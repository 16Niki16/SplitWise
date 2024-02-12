package bg.sofia.uni.fmi.mjt.splitwise.helpers;

import bg.sofia.uni.fmi.mjt.splitwise.command.Command;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.FriendNotRegisteredException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.GroupDoesNotExistException;
import bg.sofia.uni.fmi.mjt.splitwise.streams.ReaderWriterCreator;
import bg.sofia.uni.fmi.mjt.splitwise.user.User;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
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

    public static void checkGroupExist(String name, ReaderWriterCreator groupsDirectory)
        throws GroupDoesNotExistException, IOException {
        try (BufferedReader r = new BufferedReader(groupsDirectory.getRead())) {
            String line;
            while ((line = r.readLine()) != null) {
                String[] searchGr = line.split("\\|");
                if (searchGr[GROUP_NAME].equals(name)) {
                    return;
                }
            }
            throw new GroupDoesNotExistException("Group with that name does not exist");
        }
    }

    public static void checkInFile(String username, ReaderWriterCreator creator)
        throws FriendNotRegisteredException, IOException {
        try (BufferedReader r = new BufferedReader(creator.getRead())) {
            String user;
            while ((user = r.readLine()) != null) {
                String[] splitU = user.split("\\|");
                if (splitU[USER].equals(username)) {
                    return;
                }
            }
            throw new FriendNotRegisteredException("This person is not registered yet.");
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
            throw new FriendNotRegisteredException(extractNotRegistered(users));
        }
    }

    public static String extractNotRegistered(Set<String> users) {
        StringBuilder build = new StringBuilder("These people are still not registered: ");
        for (String user : users) {
            build.append(user).append(", ");
        }
        return build.substring(build.length() - 2);
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
}
