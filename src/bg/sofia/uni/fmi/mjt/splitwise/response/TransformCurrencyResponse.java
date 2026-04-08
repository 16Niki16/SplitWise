package bg.sofia.uni.fmi.mjt.splitwise.response;

public record TransformCurrencyResponse(ResponseStatus responseStatus, String message) implements ResponseData {
    public static TransformCurrencyResponse of(String currentCurrency, String newCurrency) {
        return new TransformCurrencyResponse(ResponseStatus.SUCCESSFUL,
                "You changed the currency from" + currentCurrency + "to " + newCurrency);
    }

    @Override
    public String getResponse() {
        return message;
    }
}
