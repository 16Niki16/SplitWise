package bg.sofia.uni.fmi.mjt.splitwise.command.currency.exchange;

import java.util.Map;
import com.google.gson.annotations.SerializedName;

public class ExchangeRateResponse {
    @SerializedName("date")
    private String date;

    @SerializedName("base")
    private String base;

    @SerializedName("rates")
    private Map<String, String> rates;

    public Map<String, String> getRates() {
        return rates;
    }
}
