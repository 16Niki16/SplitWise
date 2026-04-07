package bg.sofia.uni.fmi.mjt.splitwise.response;

public record HelpResponse(ResponseStatus responseStatus, String message) implements Response {
    public static HelpResponse of(String commandsList) {
        return new HelpResponse(ResponseStatus.SUCCESSFUL, commandsList);
    }
}
