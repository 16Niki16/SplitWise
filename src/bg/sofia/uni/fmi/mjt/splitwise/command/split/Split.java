package bg.sofia.uni.fmi.mjt.splitwise.command.split;

import bg.sofia.uni.fmi.mjt.splitwise.command.Command;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.PersonNotFriendException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.AMOUNT;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.FRIEND_LIST;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.MOMENT_MONEY;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.PASSWORD;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.REASON;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.TWO;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.USER;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.USERNAME_OWE;

public class Split implements SplitAPI {
    private String directory;

    public Split(String directory) {
        this.directory = directory;
    }

    @Override
    public String moneyOwe(Command command) {
        try (BufferedReader r = new BufferedReader(new FileReader(directory))) {
            String readline;
            List<String> newLines = new ArrayList<>();
            boolean isFound = false;
            while ((readline = r.readLine()) != null) {
                String[] splited = readline.split("\\|");
                if (command.line().equals(splited[USER].trim())) {
                    String[] splitedFriends = splited[FRIEND_LIST].split(",");
                    for (String fr : splitedFriends) {
                        String[] friend = fr.trim().split(" ");
                        if (friend[USER].equals(command.args()[USERNAME_OWE])) {
                            newLines.add(appendMoney(splited, command, splitedFriends));
                            isFound = true;
                        }
                    }
                } else {
                    newLines.add(readline);
                }
            }
            if (isFound) {
                appendNewInformation(newLines);
                return command.args()[REASON];
            }
            throw new PersonNotFriendException("this person is not part of your friends");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (PersonNotFriendException ee) {
            return ee.getLocalizedMessage();
        }
    }

    private String appendMoney(String[] line, Command command, String[] friends) {
        StringBuilder build = new StringBuilder(line[USER]).append("|").append(line[PASSWORD]);
        boolean isFirst = true;
        for (String fr : friends) {
            String[] friend = fr.trim().split(" ");
            if (friend[USER].equals(command.args()[USERNAME_OWE])) {
                double totalAmount =
                    Double.parseDouble(command.args()[AMOUNT]) / TWO + Double.parseDouble(friend[MOMENT_MONEY]);
                friend[MOMENT_MONEY] = Double.toString(totalAmount);
            }
            if (isFirst) {
                build.append("| ").append(friend[USER]).append(" ").append(friend[MOMENT_MONEY]);
            } else {
                build.append(", ").append(friend[USER]).append(" ").append(friend[MOMENT_MONEY]);
            }
            isFirst = false;
        }
        return build.toString();
    }

    private void appendNewInformation(List<String> lines) {
        try (BufferedWriter wr = new BufferedWriter(new FileWriter(directory, false))) {
            for (String updatedLine : lines) {
                wr.write(updatedLine);
                wr.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
