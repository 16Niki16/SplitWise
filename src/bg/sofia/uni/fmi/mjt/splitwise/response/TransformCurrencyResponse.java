package bg.sofia.uni.fmi.mjt.splitwise.response;

public record TransformCurrencyResponse(ResponseStatus responseStatus, String message) implements Response {
    public static TransformCurrencyResponse of(String currency) {
        return new TransformCurrencyResponse(ResponseStatus.SUCCESSFUL, "You changed the currency to " + currency);
    }
}
