package bg.sofia.uni.fmi.mjt.splitwise.response;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("pay")
public record PayResponse(ResponseStatus responseStatus, String message) implements ResponseData {
    public static PayResponse of(String username) {
        return new PayResponse(ResponseStatus.SUCCESSFUL, "You successfully accepted the payment from " + username);
    }

    @Override
    public String getResponse() {
        return message;
    }
}
