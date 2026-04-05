package bg.sofia.uni.fmi.mjt.splitwise.notifications;

import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
public class PayGroupNotifications implements Notification {
    private final String groupName;
    private final String paymentApprover;
    private final BigDecimal amount;

    @Override
    public String getNotification() {
        return String.format("*%s - %s approved your payment %.2f LV.", groupName,
            paymentApprover, amount);
    }
}
