package bg.sofia.uni.fmi.mjt.splitwise.command.commands;

import bg.sofia.uni.fmi.mjt.splitwise.containers.User;
import bg.sofia.uni.fmi.mjt.splitwise.response.ResponseData;

public interface Command {
    ResponseData execute(User user);
}
