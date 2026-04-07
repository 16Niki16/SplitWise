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
        this.amount.subtract(amountPaid);
    }

}
