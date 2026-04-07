package bg.sofia.uni.fmi.mjt.splitwise.notifications;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class SplitGroupNotification implements Notification {
    private String groupName;
    private String receiver;
    private BigDecimal amount;
    private String reason;
    private String currency;

    @Override
    public String getNotification() {
        return String.format("*%s - You owe %s %.2f %s[%s]",
            groupName, receiver, amount, currency, reason);
    }
}
