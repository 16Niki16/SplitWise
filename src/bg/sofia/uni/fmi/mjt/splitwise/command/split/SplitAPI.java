package bg.sofia.uni.fmi.mjt.splitwise.command.split;

import bg.sofia.uni.fmi.mjt.splitwise.command.CommandLine;

public interface SplitAPI {
    /**
     *add money paid by you for someone else
     * */
    String moneyOwe(CommandLine command);
}
