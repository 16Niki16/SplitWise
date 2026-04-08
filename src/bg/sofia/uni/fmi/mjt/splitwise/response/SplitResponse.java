package bg.sofia.uni.fmi.mjt.splitwise.response;

public record SplitResponse(ResponseStatus responseStatus, String message) implements ResponseData {
    public static SplitResponse of(String friendName) {
        return new SplitResponse(ResponseStatus.SUCCESSFUL,
            "Successfully split the amount between you and " + friendName);
    }

    @Override
    public String getResponse() {
        return message;
    }
}
