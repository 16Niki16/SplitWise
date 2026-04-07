package bg.sofia.uni.fmi.mjt.splitwise.containers;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@AllArgsConstructor
@Getter
public class Debt {
    private String from;
    private String to;
    private BigDecimal amount;

    public void paid(BigDecimal amountPaid) {
        this.amount = this.amount.subtract(amountPaid);
    }

    public void addAmount(BigDecimal amountToAdd) {
        this.amount = this.amount.add(amountToAdd);
    }
}
