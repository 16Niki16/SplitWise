package bg.sofia.uni.fmi.mjt.splitwise.command.currency.client;

import bg.sofia.uni.fmi.mjt.splitwise.command.currency.exchange.ExchangeRateResponse;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.NotCorrectQueryException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.UnknownCurrencyException;
import com.google.gson.Gson;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

public class ExchangeRate {
    private static final String APIKEY = "&apikey=fc74e945025942028f624f24b4b5b240";
    private static final String SITE = "api.currencyfreaks.com";
    private static final String ENDPOINT = "/v2.0/rates/latest";

    private static final int TWO = 2;
    private static final int DEFAULT_STATUS = 0;
    private int statusNow = 0;
    private static final int CORRECT = 200;
    private static final int END_CORRECT = 300;
    private final HttpClient client;

    public ExchangeRate(HttpClient client) {
        this.client = client;
    }

    public Map<String, Double> exchange(String wantedCurrency, String momentCurrency)
        throws URISyntaxException, NotCorrectQueryException, UnknownCurrencyException {
        URI uri = new URI("https", SITE, ENDPOINT, APIKEY + "&symbols=" + wantedCurrency + "," + momentCurrency, null);
        String data = takeData(uri);
        checkRequestStatus();
        Gson gson = new Gson();
        ExchangeRateResponse exchangeRateResponse = gson.fromJson(data, ExchangeRateResponse.class);
        Map<String, Double> rates = exchangeRateResponse.getRates();
        if (rates.size() == TWO) {
            return rates;
        } else {
            throw new UnknownCurrencyException("Unknown currency!");
        }
    }

    private String takeData(URI uri) {
        HttpRequest request = HttpRequest.newBuilder().uri(uri).build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
            .thenApply(response -> {
                int statusCode = response.statusCode();
                if (statusCode >= CORRECT && statusCode < END_CORRECT) {
                    statusNow = statusCode;

                }
                return response;
            })
            .thenApply(HttpResponse::body)
            .join();
    }

    private void checkRequestStatus() throws NotCorrectQueryException {
        if (DEFAULT_STATUS == statusNow) {
            throw new NotCorrectQueryException("Unsuccessful request!");
        }
        statusNow = DEFAULT_STATUS;
    }
}
