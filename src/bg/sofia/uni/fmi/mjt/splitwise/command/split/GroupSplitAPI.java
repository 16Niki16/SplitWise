package bg.sofia.uni.fmi.mjt.splitwise.command.split;

import bg.sofia.uni.fmi.mjt.splitwise.command.CommandLine;

public interface GroupSplitAPI {

    /**
     * split the money you paid between the group
     */
    public String groupsOwe(CommandLine command);
}
