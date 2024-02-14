import bg.sofia.uni.fmi.mjt.splitwise.command.currency.client.ClientHttp;

import java.net.URISyntaxException;
import java.net.http.HttpClient;

public class MainHttpTester {
    public static void main(String[] args) {
        HttpClient client = HttpClient.newBuilder().build();
        ClientHttp hhtp = new ClientHttp(client);
        try {
            System.out.println(hhtp.exchange("usd"));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
