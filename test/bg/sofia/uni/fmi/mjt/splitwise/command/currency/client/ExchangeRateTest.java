package bg.sofia.uni.fmi.mjt.splitwise.command.currency.client;

import bg.sofia.uni.fmi.mjt.splitwise.currency.ExchangeRate;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.NotCorrectQueryException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.UnknownCurrencyException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ExchangeRateTest {
    private ExchangeRate rate;
    private HttpResponse<String> response;
    private String wrongData;

    @BeforeEach
    void setUp() {
        String data = """
                {
                    "date": "2023-03-21 13:26:00+00",
                    "base": "USD",
                    "rates": {
                        "EUR": "0.927",
                        "USD": "1.0"
                    }
                }""";

        wrongData = """
                {
                    "date": "2023-03-21 13:26:00+00",
                    "base": "USD",
                    "rates": {
                        "EUR": "0.927"
                    }
                }""";

        HttpClient client = mock();
        response = mock();
        rate = new ExchangeRate(client);
        Map<String, Double> rates = new HashMap<>();
        rates.put("EUR", 0.927);
        rates.put("USD", 1.0);

        when(response.body()).thenReturn(data);
        when(response.statusCode()).thenReturn(200);

        CompletableFuture<HttpResponse<String>> completedFuture = CompletableFuture.completedFuture(response);
        when(client.sendAsync(any(HttpRequest.class),
                any(HttpResponse.BodyHandlers.ofString().getClass()))).thenReturn(completedFuture);
    }

    @Test
    void testExchange() throws NotCorrectQueryException, URISyntaxException, UnknownCurrencyException {
        Map<String, Double> extracted = rate.exchange("random", "random");
        assertTrue(extracted.containsKey("EUR") && extracted.containsKey("USD"),
                "test exchange valid mistake!");
    }

    @Test
    void testExchangeStatusCodeException() {
        when(response.statusCode()).thenReturn(400);
        assertThrows(NotCorrectQueryException.class, () -> rate.exchange("random", "random"),
                "Expected not correct query exception!");
    }

    @Test
    void tesExchangeUnknownCurrencyException(){
        when(response.body()).thenReturn(wrongData);
        assertThrows(UnknownCurrencyException.class, () -> rate.exchange("random", "random"),
                "expected unknown currency exception, but nothing was thrown!");
    }
}
