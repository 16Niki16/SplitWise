package bg.sofia.uni.fmi.mjt.splitwise.command.currency.client;

import bg.sofia.uni.fmi.mjt.splitwise.command.Command;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.NotCorrectQueryException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.UnknownCurrencyException;
import bg.sofia.uni.fmi.mjt.splitwise.helpers.Helpers;
import bg.sofia.uni.fmi.mjt.splitwise.streams.ReaderWriterCreator;
import bg.sofia.uni.fmi.mjt.splitwise.user.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.USER;

public class TransformCurrency {
    private static final int CURRENCY = 1;
    private ReaderWriterCreator directory;
    private User user;
    private HttpClient client;

    public TransformCurrency(ReaderWriterCreator directory, User user, HttpClient client) {
        this.directory = directory;
        this.user = user;
        this.client = client;
    }

    public String changeCurrency(Command command) throws UnknownCurrencyException, NotCorrectQueryException {
        try {
            Helpers.addInformation(updatedInformation(command), directory);
            return "Currency successfully changed!";
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<String> updatedInformation(Command command)
        throws IOException, NotCorrectQueryException, URISyntaxException,
        UnknownCurrencyException {
        try (BufferedReader r = new BufferedReader(directory.getRead())) {
            String lineRead;
            List<String> lines = new ArrayList<>();
            while ((lineRead = r.readLine()) != null) {
                String[] splitedUser = lineRead.split("\\|");
                if (splitedUser[USER].equals(command.line())) {
                    GetExchangeRate exchange = new GetExchangeRate(client);
                    Map<String, String> currencies = exchange.exchange(command.args()[CURRENCY], user.getCurrency());
                    lines.add(user.changeCurrency(currencies));
                } else {
                    lines.add(lineRead);
                }
            }
            return lines;
        }
    }
}
