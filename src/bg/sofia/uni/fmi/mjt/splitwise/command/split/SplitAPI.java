package bg.sofia.uni.fmi.mjt.splitwise.command.split;

import bg.sofia.uni.fmi.mjt.splitwise.command.Command;

public interface SplitAPI {
    /**
     *add money paid by you for someone else
     * */
    String moneyOwe(Command command);
}
