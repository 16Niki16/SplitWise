package bg.sofia.uni.fmi.mjt.splitwise.command;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.UnknownCommandException;

public enum CommandType {
    LOGIN("login"),
    CREATE_ACCOUNT("create-account"),

    ADD_FRIEND("add-friend"),

    CREATE_GROUP("create-group"),

    SPLIT("split"),

    SPLIT_GROUP("split-group"),

    GET_STATUS("get-status"),

    HELP("help"),

    PAID("paid"),
    SWITCH_CURRENCY("switch-currency");
    private final String type;

    CommandType(String type) {
        this.type = type;
    }

    public static CommandType of(String type) {

        for (CommandType value : values()) {
            if (value.type.equals(type)) {
                return value;
            }
        }
        throw new UnknownCommandException("Unknown command");
    }
}
