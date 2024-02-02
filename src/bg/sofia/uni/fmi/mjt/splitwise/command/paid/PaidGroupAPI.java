package bg.sofia.uni.fmi.mjt.splitwise.command.paid;

import bg.sofia.uni.fmi.mjt.splitwise.command.Command;

public interface PaidGroupAPI {
    /**
     * making payment to a group
     * */
    String personPaidToGroup(Command command);
}
