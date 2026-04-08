package bg.sofia.uni.fmi.mjt.splitwise.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("addFriend")
public record AddFriendResponse(ResponseStatus statusResponse, String message) implements ResponseData {
    public static AddFriendResponse of(String username) {
        return new AddFriendResponse(ResponseStatus.SUCCESSFUL, "You successfully added " + username);
    }

    @Override
    public String getResponse() {
        return message;
    }
}
