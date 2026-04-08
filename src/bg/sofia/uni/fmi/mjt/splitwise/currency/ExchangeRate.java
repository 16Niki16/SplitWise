package bg.sofia.uni.fmi.mjt.splitwise.currency;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.NotCorrectQueryException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.URIException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public class ExchangeRate {
    private static final int CORRECT_REQUEST = 200;
    private static final int BAD_REQUEST = 300;
    private static final String APIKEY = "&apikey=1db67adc3b08c1ccb5ffe18d8f5356d0";
    private static final String SITE = "api.exchangeratesapi.io";
    private static final String ENDPOINT = "/v1/latest";

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private final HttpClient client;

    public ExchangeRate(HttpClient client) {
        this.client = client;
    }

    public CompletableFuture<ExchangeRateResponse> getAllRates(String baseCurrency) {
        try {
            URI uri = new URI(
                    "https",
                    SITE,
                    ENDPOINT,
                    "?access_key=" + APIKEY + "&base=" + baseCurrency,
                    null
            );

            HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();

            return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(resp -> {
                        validateStatus(resp.statusCode());
                        return resp.body();
                    })
                    .thenApply(this::parseResponse);
        } catch (URISyntaxException e) {
            throw new URIException("URI error", e);
        }
    }

    private ExchangeRateResponse parseResponse(String body) {
        try {
            return MAPPER.readValue(body, ExchangeRateResponse.class);
        } catch (Exception e) {
            throw new RuntimeException("Invalid JSON", e);
        }
    }

    private void validateStatus(int statusCode) {
        if (statusCode < CORRECT_REQUEST || statusCode >= BAD_REQUEST) {
            throw new NotCorrectQueryException("Unsuccessful request! Status: " + statusCode);
        }
    }
}
