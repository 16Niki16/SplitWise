package bg.sofia.uni.fmi.mjt.splitwise.command.currency.exchange;

import java.util.Map;
import com.google.gson.annotations.SerializedName;

public class ExchangeRateResponse {
    @SerializedName("date")
    private String date;

    @SerializedName("base")
    private String base;

    @SerializedName("rates")
    private Map<String, Double> rates;

    public Map<String, Double> getRates() {
        return rates;
    }
}
