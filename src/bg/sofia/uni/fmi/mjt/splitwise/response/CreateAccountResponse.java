package bg.sofia.uni.fmi.mjt.splitwise.response;

public record CreateAccountResponse(ResponseStatus responseStatus, String message) implements ResponseData {
    public static CreateAccountResponse of(String username) {
        return new CreateAccountResponse(ResponseStatus.SUCCESSFUL,
                "You successfully created account with username: " + username);
    }

    @Override
    public String getResponse() {
        return message;
    }
}
