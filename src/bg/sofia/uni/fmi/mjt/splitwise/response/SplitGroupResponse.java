package bg.sofia.uni.fmi.mjt.splitwise.response;

public record SplitGroupResponse(ResponseStatus responseStatus, String message) implements Response {
    public static SplitGroupResponse of(String group) {
        return new SplitGroupResponse(ResponseStatus.SUCCESSFUL, "You successfully split the amount in " + group);
    }

    @Override
    public String getResponse() {
        return message;
    }
}
