package bg.sofia.uni.fmi.mjt.splitwise.response;

public record ErrorResponse(ResponseStatus responseStatus, String errorMessage) implements ResponseData {
    public static ErrorResponse of(String errorMessage) {
        return new ErrorResponse(ResponseStatus.ERROR, errorMessage);
    }

    @Override
    public String getResponse() {
        return errorMessage;
    }
}
