package bg.sofia.uni.fmi.mjt.splitwise.helpers;

import bg.sofia.uni.fmi.mjt.splitwise.command.CommandType;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.AddYourselfException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.NotEnoughArgumentsException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.NotNumberException;

import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.FOUR;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.THREE;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.TWO;

public class ExceptionHandler {

    private static final int ONE = 1;

    public static void checkAddYourself(String name, String addName) throws AddYourselfException {
        if (name.equals(addName)) {
            throw new AddYourselfException("You can not add yourself as a friend or two times in a group!");
        }
    }

    public static void checkNumber(String possibleNumber) throws NotNumberException {
        if (!possibleNumber.matches("\\d+(\\.\\d+)?")) {
            throw new NotNumberException("The format of the number is not correct(Need positive number).");
        }
    }

    public static CommandType getCommand(CommandType type, String... commandArguments)
            throws NotEnoughArgumentsException {
        return switch (type) {
            case CommandType.ADD_FRIEND -> {
                if (commandArguments.length != TWO) {
                    throw new NotEnoughArgumentsException(
                            "Add-friend does not have enough arguments or have too many arguments!");
                }
                yield type;
            }
            case CommandType.CREATE_GROUP, CommandType.SPLIT, CommandType.SPLIT_GROUP -> {
                if (commandArguments.length < FOUR) {
                    throw new NotEnoughArgumentsException("Command does not have enough arguments!");
                }
                yield type;
            }
            case CommandType.PAID -> {
                if (commandArguments.length != THREE) {
                    throw new NotEnoughArgumentsException("Paid does not have enough arguments!");
                }
                yield type;
            }
            case CommandType.GET_STATUS, CommandType.HELP -> {
                if (commandArguments.length != ONE) {
                    throw new NotEnoughArgumentsException("Get-status or help does not have enough arguments!");
                }
                yield type;
            }
            case CommandType.GROUP_PAID -> {
                if (commandArguments.length != FOUR) {
                    throw new NotEnoughArgumentsException("Group-paid number of arguments is not correct!");
                }
                yield type;
            }
        };
    }
}
