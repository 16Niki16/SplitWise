package bg.sofia.uni.fmi.mjt.splitwise.group;

import bg.sofia.uni.fmi.mjt.splitwise.command.Command;
import bg.sofia.uni.fmi.mjt.splitwise.notifications.GroupNotification;
import bg.sofia.uni.fmi.mjt.splitwise.streams.ReaderWriterCreator;

import java.util.LinkedHashMap;
import java.util.Map;

import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.AMOUNT;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.FRIEND_LIST;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.GROUP_NAME;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.USER;

public class Group implements GroupAPI {
    private static final int AMOUNT_INDEX = 1;
    private static final int PAYER = 2;
    private static final int GROUP_INDEX = 0;
    private static final int PEOPLE_INDEX = 1;
    private static final double STARTER = 0.00;
    private String group;
    private Map<String, Double> members;

    private Group(String group, Map<String, Double> members) {
        this.group = group;
        this.members = members;
    }

    public static Group of(String creator, String... participants) {
        Map<String, Double> participant = new LinkedHashMap<>();
        participant.put(creator, STARTER);

        for (int i = FRIEND_LIST; i < participants.length; i++) {
            participant.put(participants[i], 0.00);
        }

        return new Group(participants[GROUP_NAME].trim(), participant);
    }

    public static Group ofSplit(String line) {
        String[] splitGroup = line.trim().split("\\|");
        Map<String, Double> participant = new LinkedHashMap<>();
        String[] splitPeople = splitGroup[PEOPLE_INDEX].trim().split(",");
        for (String person : splitPeople) {
            String[] getData = person.trim().split(" ");
            participant.put(getData[USER].trim(), Double.parseDouble(getData[AMOUNT]));
        }
        return new Group(splitGroup[GROUP_INDEX].trim(), participant);
    }

    @Override
    public String addInformation(Command command, ReaderWriterCreator notifications) {
        double totalAmount = Double.parseDouble(command.args()[AMOUNT_INDEX]);
        double sumToPay = totalAmount / members.size();

        for (Map.Entry<String, Double> map : this.members.entrySet()) {
            if (map.getKey().equals(command.line())) {
                double balance = map.getValue() - totalAmount + sumToPay;
                this.members.put(map.getKey(), balance);
            } else {
                GroupNotification group = new GroupNotification(notifications);
                group.appendToGroupSplit(command, map.getKey(), Double.toString(sumToPay));
                double balance = map.getValue() + sumToPay;
                this.members.put(map.getKey(), balance);
            }
        }
        return toString();
    }

    public String payInGroup(Command command, ReaderWriterCreator notifications) {
        double totalAmount = Double.parseDouble(command.args()[AMOUNT]);
        double sumToAdd = totalAmount / (members.size() - 1);

        for (Map.Entry<String, Double> map : this.members.entrySet()) {
            if (map.getKey().equals(command.args()[PAYER])) {
                GroupNotification group = new GroupNotification(notifications);
                group.appendToGroupPayment(command, map.getKey());
                double balance = map.getValue() - totalAmount;
                this.members.put(map.getKey(), balance);
            } else {
                double balance = map.getValue() + sumToAdd;
                this.members.put(map.getKey(), balance);
            }
        }
        return toString();
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(String.format("%s | ", group));

        for (Map.Entry<String, Double> entry : members.entrySet()) {
            result.append(String.format("%s %.2f, ", entry.getKey(), entry.getValue()));
        }

        if (!result.isEmpty()) {
            result.setLength(result.length() - 2);
        }

        return result.toString();
    }
}
