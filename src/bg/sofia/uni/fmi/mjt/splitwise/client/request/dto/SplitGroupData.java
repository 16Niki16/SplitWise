package bg.sofia.uni.fmi.mjt.splitwise.client.request.dto;

import java.math.BigDecimal;

public record SplitGroupData(String groupName, BigDecimal amount, String reason) implements Data {
}
