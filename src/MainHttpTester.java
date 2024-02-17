import bg.sofia.uni.fmi.mjt.splitwise.command.currency.client.GetExchangeRate;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.NotCorrectQueryException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.UnknownCurrencyException;

import java.net.URISyntaxException;
import java.net.http.HttpClient;

public class MainHttpTester {
    public static void main(String[] args) {
        HttpClient client = HttpClient.newBuilder().build();
        GetExchangeRate hhtp = new GetExchangeRate(client);
        try {
            System.out.println(hhtp.exchange("eur", "bgn"));
        } catch (URISyntaxException | NotCorrectQueryException | UnknownCurrencyException e) {
            throw new RuntimeException(e);
        }
    }
}