package bg.sofia.uni.fmi.mjt.splitwise.notifications;

import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
public class SplitPersonNotifications implements Notification {
    private String receiver;
    private BigDecimal amount;
    private String reason;

    @Override
    public String getNotification() {
        return String.format("You owe %s %.2f LV[%s]", receiver, amount, reason);
    }
}
