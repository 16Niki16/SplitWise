package bg.sofia.uni.fmi.mjt.splitwise.response;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("splitGroup")
public record SplitGroupResponse(ResponseStatus responseStatus, String message) implements ResponseData {
    public static SplitGroupResponse of(String group) {
        return new SplitGroupResponse(ResponseStatus.SUCCESSFUL, "You successfully split the amount in " + group);
    }

    @Override
    public String getResponse() {
        return message;
    }
}
