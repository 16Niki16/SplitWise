package bg.sofia.uni.fmi.mjt.splitwise.command.status;

import bg.sofia.uni.fmi.mjt.splitwise.command.Command;
import bg.sofia.uni.fmi.mjt.splitwise.helpers.ExceptionFormater;
import bg.sofia.uni.fmi.mjt.splitwise.streams.ReaderWriterCreator;

import java.io.BufferedReader;
import java.io.IOException;

import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.AMOUNT;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.FRIEND_LIST;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.FRIEND_NAME;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.TWO;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.USER;

public class Status implements StatusAPI {

    private static final int GROUP = 0;
    private ReaderWriterCreator directory;
    private ReaderWriterCreator groupsDirectory;
    private ReaderWriterCreator exceptions;

    public Status(ReaderWriterCreator directory, ReaderWriterCreator groupsDirectory,
                  ReaderWriterCreator exceptionsDirectory) {
        this.directory = directory;
        this.groupsDirectory = groupsDirectory;
        this.exceptions = exceptionsDirectory;
    }

    public String getStatus(Command command) {
        return peopleOwes(command) +
            groupAppend(command);
    }

    private String peopleOwes(Command command) {
        StringBuilder build = new StringBuilder("Friend list:\n");
        try (BufferedReader r = new BufferedReader(directory.getRead())) {
            String line;
            while ((line = r.readLine()) != null) {
                String[] splitedLine = line.split("\\|");
                if (splitedLine[USER].equals(command.line())) {
                    if (splitedLine.length == TWO) {
                        return "You do not have debts with friends\n";
                    }
                    String[] splitfr = splitedLine[FRIEND_LIST].split(",");
                    for (String fr : splitfr) {
                        String[] splitMoney = fr.split(" ");
                        if (Double.parseDouble(splitMoney[AMOUNT]) != 0) {
                            build.append(appendToBuilder(splitMoney, false));
                        }
                    }
                }
            }
            return build.toString();
        } catch (IOException e) {
            ExceptionFormater.exceptionAdd(command.line(), "Could not extract in status friends", e.getStackTrace(),
                exceptions);
            throw new RuntimeException("Could not extract friends. IO", e);
        }
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
                                build.append(splitLine[GROUP]).append("\n").append(appendToBuilder(spl, true));
                                isFirst = false;
                            } else {
                                build.append(appendToBuilder(spl, true));
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

    private String appendToBuilder(String[] splitMoney, boolean isInGroup) {
        if (!isInGroup) {
            if (Double.parseDouble(splitMoney[AMOUNT]) > 0) {
                return String.format("*You owe %.2f to %s LV.\n", Double.parseDouble(splitMoney[AMOUNT]),
                    splitMoney[USER]);
            } else {
                return String.format("*%s owes you %.2f LV.\n", splitMoney[USER],
                    Double.parseDouble(splitMoney[AMOUNT].substring(1)));
            }
        } else {
            if (Double.parseDouble(splitMoney[AMOUNT]) > 0) {
                return String.format("*%s owes to the group %.2f LV.\n", splitMoney[USER],
                    Double.parseDouble(splitMoney[AMOUNT]));
            } else {
                return String.format("*Group owe %.2f to %s LV.\n", Double.parseDouble(splitMoney[AMOUNT].substring(1)),
                    splitMoney[USER]);
            }
        }
    }
}
