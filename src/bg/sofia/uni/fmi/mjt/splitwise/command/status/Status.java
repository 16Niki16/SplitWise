package bg.sofia.uni.fmi.mjt.splitwise.command.status;

import bg.sofia.uni.fmi.mjt.splitwise.command.Command;
import bg.sofia.uni.fmi.mjt.splitwise.helpers.ExceptionFormater;
import bg.sofia.uni.fmi.mjt.splitwise.streams.ReaderWriterCreator;
import bg.sofia.uni.fmi.mjt.splitwise.user.User;

import java.io.BufferedReader;
import java.io.IOException;

import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.AMOUNT;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.FRIEND_NAME;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.USER;

public class Status implements StatusAPI {

    private static final int GROUP = 0;
    private ReaderWriterCreator groupsDirectory;
    private ReaderWriterCreator exceptions;
    private User user;

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
                String[] splitLine = line.split("\\|");
                if (checkUserContains(command.line(), splitLine[FRIEND_NAME])) {
                    String[] getPeople = splitLine[FRIEND_NAME].split(",");
                    boolean isFirst = true;
                    for (String people : getPeople) {
                        String[] spl = people.split(" ");
                        if (Double.parseDouble(spl[AMOUNT]) != 0) {
                            if (isFirst) {
                                build.append(splitLine[GROUP]).append("\n").append(appendToBuilder(spl));
                                isFirst = false;
                            } else {
                                build.append(appendToBuilder(spl));
                            }
                        }
                    }
                }
            }
            return (build.toString().equals("Groups:\n")) ? "You do not have debts in the groups!" : build.toString();
        } catch (IOException e) {
            ExceptionFormater.exceptionAdd(command.line(), "Could not extract in status groups", e.getStackTrace(),
                exceptions);
            throw new RuntimeException("Could not extract groups. IO", e);
        }
    }

    private boolean checkUserContains(String username, String group) {
        String[] groupMem = group.split(",");
        for (String check : groupMem) {
            String[] spl = check.split(" ");
            if (spl[USER].equals(username)) {
                return true;
            }
        }
        return false;
    }

    private String appendToBuilder(String[] splitMoney) {
        if (Double.parseDouble(splitMoney[AMOUNT]) > 0) {
            return String.format("*%s owes to the group %.2f LV.\n", splitMoney[USER],
                Double.parseDouble(splitMoney[AMOUNT]));
        } else {
            return String.format("*Group owe %.2f to %s LV.\n", Double.parseDouble(splitMoney[AMOUNT].substring(1)),
                splitMoney[USER]);
        }
    }
}
