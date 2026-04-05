package bg.sofia.uni.fmi.mjt.splitwise.command;

import bg.sofia.uni.fmi.mjt.splitwise.command.commands.Command;

public interface CommandParser {
    Command parse(String[] args);
}
