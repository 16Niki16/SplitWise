package bg.sofia.uni.fmi.mjt.splitwise.response;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("help")
public record HelpResponse(ResponseStatus responseStatus, String message) implements ResponseData {
    public static HelpResponse of(String commandsList) {
        return new HelpResponse(ResponseStatus.SUCCESSFUL, commandsList);
    }

    @Override
    public String getResponse() {
        return message;
    }
}
