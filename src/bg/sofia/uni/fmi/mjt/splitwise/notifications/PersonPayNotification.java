package bg.sofia.uni.fmi.mjt.splitwise.notifications;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PersonPayNotification implements Notification {
    private String paymentApprover;
    private BigDecimal amount;
    private String currency;

    @Override
    public String getNotification() {
        return String.format("%s approved your payment %.2f %s.", paymentApprover, amount, currency);
    }
}
