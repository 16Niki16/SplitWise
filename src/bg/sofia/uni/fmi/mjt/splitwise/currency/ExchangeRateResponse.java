package bg.sofia.uni.fmi.mjt.splitwise.currency;

import java.util.Map;

public record ExchangeRateResponse(String base, String date, Map<String, Double> rates) {
}
