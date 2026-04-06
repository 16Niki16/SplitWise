package bg.sofia.uni.fmi.mjt.splitwise.command.commands;

import bg.sofia.uni.fmi.mjt.splitwise.containers.User;

public interface Command {
    String execute(User user);
}
