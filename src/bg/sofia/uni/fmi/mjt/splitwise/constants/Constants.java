package bg.sofia.uni.fmi.mjt.splitwise.constants;

public class Constants {
    //server constants
    public static final int SERVER_PORT = 7777;
    public static final String SERVER_HOST = "localhost";
    public static final int BUFFER_SIZE = 1024;

    //commands constants
    public static final int USER = 0;
    public static final int PASSWORD = 1;
    public static final int FRIEND_LIST = 2;
    public static final int COMMAND_NAME = 0;
    public static final int FRIEND_NAME = 1;
    public static final int GROUP_NAME = 1;
    public static final int THREE = 3;
    public static final int FOUR = 4;
    public static final int USERNAME_OWE = 2;
    public static final int AMOUNT = 1;
    public static final int REASON = 3;
    public static final int TWO = 2;

    //type commands constants
    public static final String ADD_FRIEND = "add-friend";
    public static final String CREATE_GROUP = "create-group";
    public static final String SPLIT = "split";
    public static final String SPLIT_GROUP = "split-group";
    public static final String GET_STATUS = "get-status";
    public static final String HELP = "help";
    public static final String PAID = "paid";
    public static final String GROUP_PAID = "group-paid";

}
