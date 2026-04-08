package bg.sofia.uni.fmi.mjt.splitwise.response;

public record CreateGroupResponse(ResponseStatus responseStatus, String message) implements ResponseData {
    public static CreateGroupResponse of(String groupName) {
        return new CreateGroupResponse(ResponseStatus.SUCCESSFUL, "You created group called " + groupName);
    }

    @Override
    public String getResponse() {
        return message;
    }
}
