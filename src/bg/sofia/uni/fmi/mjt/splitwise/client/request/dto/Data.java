package bg.sofia.uni.fmi.mjt.splitwise.client.request.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = CreateAccountData.class, name = "createAccount"),
        @JsonSubTypes.Type(value = LoginData.class, name = "login"),
        @JsonSubTypes.Type(value = AddFriendData.class, name = "addFriend"),
        @JsonSubTypes.Type(value = CreateGroupData.class, name = "createGroup"),
        @JsonSubTypes.Type(value = PayData.class, name = "pay"),
        @JsonSubTypes.Type(value = SplitGroupData.class, name = "splitGroup"),
        @JsonSubTypes.Type(value = SplitData.class, name = "split"),
        @JsonSubTypes.Type(value = TransformCurrencyData.class, name = "transferCurrency")
})
public interface Data {
}
