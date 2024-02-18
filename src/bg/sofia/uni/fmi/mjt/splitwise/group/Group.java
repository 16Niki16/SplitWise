package bg.sofia.uni.fmi.mjt.splitwise.group;

import bg.sofia.uni.fmi.mjt.splitwise.command.Command;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.FriendNotRegisteredException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.NoMembersToPayException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.PersonNotFriendException;
import bg.sofia.uni.fmi.mjt.splitwise.helpers.Helpers;
import bg.sofia.uni.fmi.mjt.splitwise.notifications.group.PayGroupNotifications;
import bg.sofia.uni.fmi.mjt.splitwise.notifications.group.SplitGroupNotificationAPI;
import bg.sofia.uni.fmi.mjt.splitwise.notifications.group.SplitGroupNotifications;
import bg.sofia.uni.fmi.mjt.splitwise.notifications.user.PersonPayNotifications;
import bg.sofia.uni.fmi.mjt.splitwise.notifications.user.PersonPayNotificationsAPI;
import bg.sofia.uni.fmi.mjt.splitwise.streams.ReaderWriterCreator;
import bg.sofia.uni.fmi.mjt.splitwise.user.User;
import bg.sofia.uni.fmi.mjt.splitwise.user.UserAPI;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.AMOUNT;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.FRIEND_LIST;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.GROUP_NAME;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.USER;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.USERNAME_OWE;
import static java.lang.Math.abs;

public class Group implements GroupAPI {
    private static final int ZERO = 0;
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

    public static Group of(Command command) {
        Map<String, Double> participant = new LinkedHashMap<>();
        participant.put(command.line(), STARTER);

        for (int i = FRIEND_LIST; i < command.args().length; i++) {
            participant.put(command.args()[i], STARTER);
        }

        return new Group(command.args()[GROUP_NAME], participant);
    }

    public static Group ofSplit(String line) {
        String[] splitGroup = line.split("\\|");
        Map<String, Double> participant = new LinkedHashMap<>();
        String[] splitPeople = splitGroup[PEOPLE_INDEX].split(",");
        for (String person : splitPeople) {
            String[] getData = person.split(" ");
            participant.put(getData[USER], Double.parseDouble(getData[AMOUNT]));
        }
        return new Group(splitGroup[GROUP_INDEX], participant);
    }

    public boolean checkPersonContains(String user) {
        return this.members.containsKey(user);
    }

    public String addOwes() {
        StringBuilder build = new StringBuilder(this.group + '\n');
        for (Map.Entry<String, Double> map : this.members.entrySet()) {
            if (map.getValue() > 0) {
                build.append(String.format("*%s owes to the group %.2f LV.\n", map.getKey(),
                    map.getValue()));
            } else if (map.getValue() < 0) {
                build.append(String.format("*Group owes %.2f to %s LV.\n", abs(map.getValue()), map.getKey()));
            }
        }
        return (build.toString().equals(this.group + '\n')) ? "" : build.toString();
    }

    @Override
    public String getGroupName() {
        return this.group;
    }

    @Override
    public String addInformation(Command command, ReaderWriterCreator notifications, ReaderWriterCreator tempNotif) {
        double totalAmount = Double.parseDouble(command.args()[AMOUNT_INDEX]);
        double sumToPay = totalAmount / this.members.size();

        for (Map.Entry<String, Double> map : this.members.entrySet()) {
            if (map.getKey().equals(command.line())) {
                double balance = map.getValue() - totalAmount + sumToPay;
                this.members.put(map.getKey(), balance);
            } else {
                SplitGroupNotificationAPI group = new SplitGroupNotifications(notifications, tempNotif);
                group.appendToGroupSplit(command, map.getKey(), Double.toString(sumToPay));
                double balance = map.getValue() + sumToPay;
                this.members.put(map.getKey(), balance);
            }
        }
        return toString();
    }

    @Override
    public String payInGroup(Command command, ReaderWriterCreator notifications, ReaderWriterCreator tempNotif,
                             ReaderWriterCreator friends, User user)
        throws NoMembersToPayException, PersonNotFriendException, IOException, FriendNotRegisteredException {
        double totalAmount = Double.parseDouble(command.args()[AMOUNT]);
        double sumToAdd = getSumToAdd(command, totalAmount, friends, notifications, tempNotif, user);
        totalAmount = getTotalAmount(command, totalAmount);
        for (Map.Entry<String, Double> map : this.members.entrySet()) {

            if (map.getKey().equals(command.args()[PAYER])) {
                PayGroupNotifications group = new PayGroupNotifications(notifications, tempNotif);
                group.appendToGroupPayment(command, map.getKey());
                double balance = map.getValue() - totalAmount;
                this.members.put(map.getKey(), balance);

            } else if (map.getValue() < 0) {
                double balance = map.getValue() + sumToAdd;
                this.members.put(map.getKey(), balance);
            }
        }
        return toString();
    }

    private double getSumToAdd(Command command, double totalAmount, ReaderWriterCreator friends,
                               ReaderWriterCreator notifications, ReaderWriterCreator tempNotif, User user)
        throws NoMembersToPayException, PersonNotFriendException, IOException, FriendNotRegisteredException {
        int membersPay = ZERO;
        for (Map.Entry<String, Double> map : this.members.entrySet()) {

            if (map.getValue() < ZERO && !map.getKey().equals(command.args()[PAYER])) {
                ++membersPay;

            } else if (map.getKey().equals(command.args()[PAYER]) && map.getValue() < totalAmount) {

                double amountPersonalPay = totalAmount - map.getValue();
                totalAmount = map.getValue();

                String userAppend = user.paidMoney(command.args()[USERNAME_OWE], -1 * amountPersonalPay);

                UserAPI friend = User.of(Helpers.findFriendLine(command, USERNAME_OWE, friends));
                String receiverAppend = friend.paidMoney(command.line(), amountPersonalPay);

                Helpers.addInformation(Helpers.updatedInfo(command.line(), command.args()[USERNAME_OWE],
                    userAppend, receiverAppend, friends), friends);

                PersonPayNotificationsAPI notif = new PersonPayNotifications(notifications, tempNotif);
                notif.addNotificationFriendPayment(command);
            }
        }
        if (membersPay == 0) {
            throw new NoMembersToPayException("There is not a member in the group that you can pay to!");
        }
        return totalAmount / membersPay;
    }

    private double getTotalAmount(Command command, double totalAmount) {
        for (Map.Entry<String, Double> map : this.members.entrySet()) {
            if (map.getKey().equals(command.args()[PAYER]) && map.getValue() < totalAmount) {
                return map.getValue();
            } else if (map.getKey().equals(command.args()[PAYER]) && map.getValue() >= totalAmount) {
                return totalAmount;
            }
        }
        return totalAmount;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(String.format("%s|", group));

        for (Map.Entry<String, Double> entry : members.entrySet()) {
            result.append(String.format("%s %.2f,", entry.getKey(), entry.getValue()));
        }

        if (!result.isEmpty()) {
            result.setLength(result.length() - 1);
        }

        return result.toString();
    }
}
