package bg.sofia.uni.fmi.mjt.splitwise.command.split;

import bg.sofia.uni.fmi.mjt.splitwise.command.Command;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.GroupDoesNotExistException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.USER;

public class GroupSplitClass implements GroupSplitAPI {
    private static final int GROUP_NAME = 0;
    private static final int GROUP_INDEX = 2;
    private static final int AMOUNT_INDEX = 1;
    private String directory;
    private String groupsDirectory;

    public GroupSplitClass(String directory, String groupsDirectory) {
        this.directory = directory;
        this.groupsDirectory = groupsDirectory;
    }

    @Override
    public String groupsOwe(Command command) {
        try (BufferedReader r = new BufferedReader(new FileReader(groupsDirectory))) {
            checkGroupExist(command.args()[GROUP_INDEX]);
            List<String> info = new ArrayList<>();
            String line;
            while ((line = r.readLine()) != null) {
                String[] searchGr = line.split("\\|");
                if (searchGr[GROUP_NAME].trim().equals(command.args()[GROUP_INDEX])) {
                    info.add(addInformation(searchGr, command));
                } else {
                    info.add(line);
                }
            }
            appendNewInformation(info);
        } catch (GroupDoesNotExistException e) {
            return e.getLocalizedMessage();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return "Information successfully added";
    }

    private void checkGroupExist(String name) throws GroupDoesNotExistException {
        try (BufferedReader r = new BufferedReader(new FileReader(groupsDirectory))) {
            String line;
            while ((line = r.readLine()) != null) {
                String[] searchGr = line.split("\\|");
                if (searchGr[GROUP_NAME].trim().equals(name)) {
                    return;
                }
            }
            throw new GroupDoesNotExistException("Group with that name does not exist");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String addInformation(String[] splitedLine, Command command) {
        StringBuilder build = new StringBuilder(splitedLine[GROUP_NAME]).append("| ");
        String[] splitPeople = splitedLine[1].trim().split(",");
        double totalAmount = Double.parseDouble(command.args()[AMOUNT_INDEX]);
        double sumToPay = totalAmount / splitPeople.length;
        boolean isFirst = true;
        for (String pp : splitPeople) {
            String[] takeMoney = pp.trim().split(" ");
            if (takeMoney[USER].equals(command.line())) {
                double balance = Double.parseDouble(takeMoney[AMOUNT_INDEX]) - totalAmount + sumToPay;
                takeMoney[AMOUNT_INDEX] = Double.toString(balance);
            } else {
                double balance = Double.parseDouble(takeMoney[AMOUNT_INDEX]) + sumToPay;
                takeMoney[AMOUNT_INDEX] = Double.toString(balance);
            }
            if (isFirst) {
                build.append(takeMoney[USER]).append(" ").append(takeMoney[AMOUNT_INDEX]);
                isFirst = false;
            } else {
                build.append(", ").append(takeMoney[USER]).append(" ").append(takeMoney[AMOUNT_INDEX]);
            }
        }
        return build.toString();
    }

    private void appendNewInformation(List<String> lines) {
        try (BufferedWriter wr = new BufferedWriter(new FileWriter(groupsDirectory, false))) {
            for (String updatedLine : lines) {
                wr.write(updatedLine);
                wr.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
