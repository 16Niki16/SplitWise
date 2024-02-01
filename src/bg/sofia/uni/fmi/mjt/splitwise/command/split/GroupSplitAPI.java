package bg.sofia.uni.fmi.mjt.splitwise.command.split;

import bg.sofia.uni.fmi.mjt.splitwise.command.Command;

public interface GroupSplitAPI {

    /**
     * split the money you paid between the group
     */
    public String groupsOwe(Command command);
}
