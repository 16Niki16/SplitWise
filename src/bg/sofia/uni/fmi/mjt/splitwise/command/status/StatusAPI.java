package bg.sofia.uni.fmi.mjt.splitwise.command.status;

import bg.sofia.uni.fmi.mjt.splitwise.command.Command;

public interface StatusAPI {
    /**
     * retrieves all you owe and what they owe you
     * */
    String getStatus(Command command);
}
