package bg.sofia.uni.fmi.mjt.splitwise.notifications;

import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
public class SplitGroupNotifications implements Notification {
    private String groupName;
    private String receiver;
    private BigDecimal amount;
    private String reason;

    @Override
    public String getNotification() {
        return String.format("*%s - You owe %s %.2f LV[%s]",
            groupName, receiver, amount, reason);
    }
}
