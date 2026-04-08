package bg.sofia.uni.fmi.mjt.splitwise.response;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = CreateAccountResponse.class, name = "createAccount"),
        @JsonSubTypes.Type(value = LoginResponse.class, name = "login"),
        @JsonSubTypes.Type(value = AddFriendResponse.class, name = "addFriend"),
        @JsonSubTypes.Type(value = CreateGroupResponse.class, name = "createGroup"),
        @JsonSubTypes.Type(value = PayResponse.class, name = "pay"),
        @JsonSubTypes.Type(value = SplitGroupResponse.class, name = "splitGroup"),
        @JsonSubTypes.Type(value = SplitResponse.class, name = "split"),
        @JsonSubTypes.Type(value = TransformCurrencyResponse.class, name = "transferCurrency"),
        @JsonSubTypes.Type(value = HelpResponse.class, name = "help"),
        @JsonSubTypes.Type(value = StatusResponse.class, name = "status")
})
public interface Response {
    String getResponse();
}
