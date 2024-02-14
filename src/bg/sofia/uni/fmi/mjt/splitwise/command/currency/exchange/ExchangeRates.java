package bg.sofia.uni.fmi.mjt.splitwise.command.currency.exchange;

public class ExchangeRates {
    private String currencyCode;
    private String rate;

    public ExchangeRates(String currencyCode, String rate) {
        this.currencyCode = currencyCode;
        this.rate = rate;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public String getRate() {
        return rate;
    }

    @Override
    public String toString() {
        return String.format("%s: %s", currencyCode, rate);
    }
}
