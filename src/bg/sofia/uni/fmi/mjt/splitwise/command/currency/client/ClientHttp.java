package bg.sofia.uni.fmi.mjt.splitwise.command.currency.client;

import bg.sofia.uni.fmi.mjt.splitwise.command.currency.exchange.ExchangeRateResponse;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.NotCorrectQueryException;
import com.google.gson.Gson;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

public class ClientHttp {
    private static final String APIKEY = "&apikey=fc74e945025942028f624f24b4b5b240";
    private static final String SITE = "api.currencyfreaks.com";
    private static final String ENDPOINT = "/v2.0/rates/latest";

    private static final int DEFAULT_STATUS = 0;
    private int statusNow = 0;
    private static final int CORRECT = 200;
    private static final int END_CORRECT = 300;
    private HttpClient client;

    public ClientHttp(HttpClient client) {
        this.client = client;
    }

    public String exchange(String wantedCurrency) throws URISyntaxException {
        URI uri = new URI("https", SITE, ENDPOINT, APIKEY, null);
        String data = takeData(uri);
        //checkRequestStatus();
        //System.out.println(data);
        Gson gson = new Gson();
        ExchangeRateResponse exchangeRateResponse = gson.fromJson(data, ExchangeRateResponse.class);
        Map<String, String> rates = exchangeRateResponse.getRates();
        System.out.println(rates.get("BGN"));
        return "Successfully retrieved the information!";
        /*catch (NotCorrectQueryException e) {
            return "Not correct query code!";
        }*/
    }

    private String takeData(URI uri) {
        HttpRequest request = HttpRequest.newBuilder().uri(uri).build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
            /*.thenApply(response -> {
                int statusCode = response.statusCode();
                if (statusCode >= CORRECT && statusCode < END_CORRECT) {
                    statusNow = statusCode;

                }
                return response;
            })*/
            .thenApply(HttpResponse::body)
            .join();
    }

    private void checkRequestStatus() throws NotCorrectQueryException {
        if (DEFAULT_STATUS == statusNow) {
            throw new NotCorrectQueryException("unsuccessful request");
        }
        statusNow = DEFAULT_STATUS;
    }
}
