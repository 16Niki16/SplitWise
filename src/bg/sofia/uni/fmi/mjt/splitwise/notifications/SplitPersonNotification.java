package bg.sofia.uni.fmi.mjt.splitwise.notifications;

import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
public class SplitPersonNotification implements Notification {
    private final String receiver;
    private final BigDecimal amount;
    private final String reason;
    private final String currency;

    @Override
    public String getNotification() {
        return String.format("You owe %s %.2f %s[%s]", receiver, amount, reason, currency);
    }
}
