package bg.sofia.uni.fmi.mjt.splitwise.response;

public record PayResponse(ResponseStatus responseStatus, String message) implements ResponseData {
    public static PayResponse of(String username) {
        return new PayResponse(ResponseStatus.SUCCESSFUL, "You successfully accepted the payment from " + username);
    }

    @Override
    public String getResponse() {
        return message;
    }
}
