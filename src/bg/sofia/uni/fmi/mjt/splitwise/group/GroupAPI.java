package bg.sofia.uni.fmi.mjt.splitwise.group;

import bg.sofia.uni.fmi.mjt.splitwise.command.Command;
import bg.sofia.uni.fmi.mjt.splitwise.streams.ReaderWriterCreator;

public interface GroupAPI {
    /**
     * add new balance to group people
     */
    String addInformation(Command command, ReaderWriterCreator notifications, ReaderWriterCreator tempNotif);
}
