package bg.sofia.uni.fmi.mjt.splitwise.response;

public record AddFriendResponse(ResponseStatus statusResponse, String message) implements ResponseData {
    public static AddFriendResponse of(String username) {
        return new AddFriendResponse(ResponseStatus.SUCCESSFUL, "You successfully added " + username);
    }

    @Override
    public String getResponse() {
        return message;
    }
}
