package bg.sofia.uni.fmi.mjt.splitwise.command.create;

public interface CreateGroupAPI {
    /**
     * create group method
     * */
    String createGroup(String username, String... participants);
}
