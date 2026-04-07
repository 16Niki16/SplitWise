package bg.sofia.uni.fmi.mjt.splitwise.notifications;

import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
public class SplitGroupNotification implements Notification {
    private final String groupName;
    private final String receiver;
    private final BigDecimal amount;
    private final String reason;
    private final String currency;

    @Override
    public String getNotification() {
        return String.format("*%s - You owe %s %.2f %s[%s]",
            groupName, receiver, amount, reason, currency);
    }
}
