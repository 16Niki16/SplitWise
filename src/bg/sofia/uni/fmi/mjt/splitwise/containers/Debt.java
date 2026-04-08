package bg.sofia.uni.fmi.mjt.splitwise.containers;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class Debt {
    private final String from;
    private final String to;
    private BigDecimal amount;

    @JsonCreator
    public Debt(
        @JsonProperty("from") String from,
        @JsonProperty("to") String to,
        @JsonProperty("amount") BigDecimal amount
    ) {
        this.from = from;
        this.to = to;
        this.amount = amount != null ? amount : BigDecimal.ZERO;
    }

    public void paid(BigDecimal amountPaid) {
        this.amount = this.amount.subtract(amountPaid);
    }

    public void addAmount(BigDecimal amountToAdd) {
        this.amount = this.amount.add(amountToAdd);
    }

    public String debtMessage() {
        return String.format("%s owes %s %.2f", from, to, amount);
    }
}
