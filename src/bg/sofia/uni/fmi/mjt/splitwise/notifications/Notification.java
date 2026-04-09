package bg.sofia.uni.fmi.mjt.splitwise.notifications;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = SplitGroupNotification.class, name = "group"),
    @JsonSubTypes.Type(value = SplitPersonNotification.class, name = "person"),
    @JsonSubTypes.Type(value = PersonPayNotification.class, name = "pay")
})
public interface Notification {
    @JsonIgnore
    String getNotification();
}
