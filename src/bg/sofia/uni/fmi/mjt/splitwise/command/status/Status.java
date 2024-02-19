package bg.sofia.uni.fmi.mjt.splitwise.command.status;

import bg.sofia.uni.fmi.mjt.splitwise.command.Command;
import bg.sofia.uni.fmi.mjt.splitwise.group.Group;
import bg.sofia.uni.fmi.mjt.splitwise.helpers.ExceptionFormater;
import bg.sofia.uni.fmi.mjt.splitwise.streams.ReaderWriterCreator;
import bg.sofia.uni.fmi.mjt.splitwise.user.User;

import java.io.BufferedReader;
import java.io.IOException;

public class Status implements StatusAPI {
    private final ReaderWriterCreator groupsDirectory;
    private final ReaderWriterCreator exceptions;
    private final User user;

    public Status(ReaderWriterCreator groupsDirectory,
                  ReaderWriterCreator exceptionsDirectory, User user) {
        this.groupsDirectory = groupsDirectory;
        this.exceptions = exceptionsDirectory;
        this.user = user;
    }

    public String getStatus(Command command) {
        return peopleOwes() +
            groupAppend(command);
    }

    private String peopleOwes() {
        StringBuilder build = new StringBuilder("Friend list:\n");
        String status = user.getStatus();
        if (status.isEmpty()) {
            return "You do not have debts with friends!";
        }
        return build.append(status).toString();
    }

    private String groupAppend(Command command) {
        try (BufferedReader r = new BufferedReader(groupsDirectory.getRead())) {
            StringBuilder build = new StringBuilder("Groups:\n");
            String line;
            while ((line = r.readLine()) != null) {
                Group checkGroup = Group.ofSplit(line);
                if (checkGroup.checkPersonContains(user.getUsername())) {
                    build.append(checkGroup.addOwes());
                }
            }
            return (build.toString().equals("Groups:\n")) ? "You do not have debts in the groups!" : build.toString();
        } catch (IOException e) {
            ExceptionFormater.exceptionAdd(command.line(), "Could not extract in status groups", e.getStackTrace(),
                exceptions);
            throw new RuntimeException("Could not extract groups. IO", e);
        }
    }
}
