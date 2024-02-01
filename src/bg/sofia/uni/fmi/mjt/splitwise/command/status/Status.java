package bg.sofia.uni.fmi.mjt.splitwise.command.status;

import bg.sofia.uni.fmi.mjt.splitwise.command.Command;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.AMOUNT;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.FRIEND_LIST;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.FRIEND_NAME;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.USER;

public class Status implements StatusAPI {

    private static final int GROUP = 0;
    private String directory;
    private String groupsDirectory;

    public Status(String directory, String groupsDirectory) {
        this.directory = directory;
        this.groupsDirectory = groupsDirectory;
    }

    public String getStatus(Command command) {
        StringBuilder build = new StringBuilder("Friend list:\n");
        try (BufferedReader r = new BufferedReader(new FileReader(directory))) {
            String line;
            while ((line = r.readLine()) != null) {
                String[] splitedLine = line.split("\\|");
                if (splitedLine[USER].trim().equals(command.line())) {
                    String[] splitfr = splitedLine[FRIEND_LIST].split(",");
                    for (String fr : splitfr) {
                        String[] splitMoney = fr.trim().split(" ");
                        if (Double.parseDouble(splitMoney[AMOUNT]) != 0) {
                            build.append(appendToBuilder(splitMoney, false));
                        }
                    }
                }
            }
            build.append(groupAppend(command));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return build.toString();
    }

    public String groupAppend(Command command) {
        try (BufferedReader r = new BufferedReader(new FileReader(groupsDirectory))) {
            StringBuilder build = new StringBuilder("Groups: \n");
            String line;
            while ((line = r.readLine()) != null) {
                String[] splitLine = line.split("\\|");
                if (checkUserContains(command.line(), splitLine[FRIEND_NAME])) {
                    String[] getPeople = splitLine[FRIEND_NAME].split(",");
                    boolean isFirst = true;
                    for (String people : getPeople) {
                        String[] spl = people.trim().split(" ");
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
            return build.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean checkUserContains(String username, String group) {
        String[] groupMem = group.trim().split(",");
        for (String check : groupMem) {
            String[] spl = check.trim().split(" ");
            if (spl[USER].equals(username)) {
                return true;
            }
        }
        return false;
    }

    private String appendToBuilder(String[] splitMoney, boolean isInGroup) {
        if (!isInGroup) {
            if (Double.parseDouble(splitMoney[AMOUNT]) > 0) {
                return String.format("*%s owes you %s LV.\n", splitMoney[USER], splitMoney[AMOUNT]);
            } else {
                return String.format("*You owe %s to %s LV.\n", splitMoney[AMOUNT], splitMoney[USER]);
            }
        } else {
            if (Double.parseDouble(splitMoney[AMOUNT]) > 0) {
                return String.format("*%s owes to the group %s LV.\n", splitMoney[USER], splitMoney[AMOUNT]);
            } else {
                return String.format("*Group owe %s to %s LV.\n", splitMoney[AMOUNT], splitMoney[USER]);
            }
        }
    }
}
