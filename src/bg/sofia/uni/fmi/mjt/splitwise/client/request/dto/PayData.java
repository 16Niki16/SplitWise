package bg.sofia.uni.fmi.mjt.splitwise.client.request.dto;

import java.math.BigDecimal;

public record PayData(String payer, BigDecimal amount) implements Data{
}
