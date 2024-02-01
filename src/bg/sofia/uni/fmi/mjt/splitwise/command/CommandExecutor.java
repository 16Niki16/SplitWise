package bg.sofia.uni.fmi.mjt.splitwise.command;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.FriendNotRegistered;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.NotEnoughParticipantsForGroup;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.PersonNotFriend;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.ADD_FRIEND;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.AMOUNT;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.COMMAND_NAME;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.CREATE_GROUP;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.FOUR;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.FRIEND_LIST;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.FRIEND_NAME;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.GET_STATUS;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.GROUP_NAME;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.HELP;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.MOMENT_MONEY;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.PASSWORD;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.REASON;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.SPLIT;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.SPLIT_GROUP;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.THREE;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.TWO;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.USER;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.USERNAME_OWE;

public class CommandExecutor {
    private static final String GROUPS_DIRECTORY = "DataFiles\\GroupsFile.txt";
    private String directory;

    public CommandExecutor(String directory) {
        this.directory = directory;
    }

    public String execute(Command command) {
        return switch (command.args()[COMMAND_NAME]) {
            case ADD_FRIEND -> addingFriend(command.line(), command.args());
            case CREATE_GROUP -> createGroup(command.line(), command.args());
            case SPLIT -> moneyOwe(command);
            //case SPLIT_GROUP -> splitToGroup(command);
            //case GET_STATUS -> complete(cmd.arguments());
            case HELP -> helpCommand(command);
            default -> "Unknown command";
        };
    }

    private String helpCommand(Command command) {
        return "Login: Username and password \n Commands: \n" +
            "add-friend <username> \n" +
            "create-group <group_name> <username> <username> ... <username>\n" +
            "split <amount> <username> <reason_for_payment>\n" +
            "split-group <amount> <group_name> <reason_for_payment>\n" +
            "get-status\n" +
            "payed <amount> <username>";
    }

    //split-group <amount> <group_name> <reason_for_payment>
    private String splitToGroup(Command command) {
        try (BufferedReader r = new BufferedReader(new FileReader(GROUPS_DIRECTORY))) {

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean checkGroupExist(Command command) {
        try (BufferedReader r = new BufferedReader(new FileReader(GROUPS_DIRECTORY))) {
            String line;
            while((line = r.readLine()) != null){
                String[] splited =
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String moneyOwe(Command command) {
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
            throw new PersonNotFriend("this person is not part of your friends");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (PersonNotFriend ee) {
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

    private String createGroup(String username, String... participants) {
        try {
            if (participants.length < FOUR) {
                throw new NotEnoughParticipantsForGroup("groups participants are not enough");
            }
            StringBuilder buildingGroup = new StringBuilder(participants[GROUP_NAME]).append(" | ")
                .append(username).append(" 0");
            for (int i = FRIEND_LIST; i < participants.length; i++) {
                if (!checkInFile(participants[i])) {
                    throw new FriendNotRegistered("THis person is not registered yet and cannot be added");
                }
                buildingGroup.append(", ").append(participants[i]).append(" 0");
            }
            return appendToFile(buildingGroup);
        } catch (NotEnoughParticipantsForGroup | FriendNotRegistered e) {
            return e.getLocalizedMessage();
        }
    }

    private String appendToFile(StringBuilder group) {
        try (BufferedWriter wr = new BufferedWriter(new FileWriter(GROUPS_DIRECTORY, true))) {
            wr.write(String.valueOf(group));
            wr.newLine();
            return group.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String addingFriend(String username, String... friend) {
        try (BufferedReader r = new BufferedReader(new FileReader(directory))) {
            if (!checkInFile(friend[FRIEND_NAME])) {
                throw new FriendNotRegistered("This person is not registered yet");
            }
            String lineRead;
            List<String> lines = new ArrayList<>();
            while ((lineRead = r.readLine()) != null) {
                String[] splitedUser = lineRead.split("\\|");
                if (splitedUser[USER].trim().equals(username)) {
                    StringBuilder addFriend = new StringBuilder(lineRead);
                    addFriends(addFriend, splitedUser.length != THREE, friend[1]);
                    lines.add(addFriend.toString());
                } else {
                    lines.add(lineRead);
                }
            }
            try (BufferedWriter wr = new BufferedWriter(new FileWriter(directory, false))) {
                for (String updatedLine : lines) {
                    wr.write(updatedLine);
                    wr.newLine();
                }
            }
        } catch (FriendNotRegistered ee) {
            return ee.getLocalizedMessage();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return String.format("Friend %s is added.", friend[1]);
    }

    private void addFriends(StringBuilder addFriend, boolean isFirst, String... friend) {
        if ((isFirst)) {
            addFriend.append(" | ").append(friend[0]).append(" 0");
        } else {
            addFriend.append(", ").append(friend[0]).append(" 0");
        }
    }

    private boolean checkInFile(String username) {
        try (BufferedReader r = new BufferedReader(new FileReader(directory))) {
            String user;
            while ((user = r.readLine()) != null) {
                String[] splitU = user.split("\\|");
                if (splitU[USER].trim().equals(username)) {
                    return true;
                }
            }
            return false;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
