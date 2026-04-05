package bg.sofia.uni.fmi.mjt.splitwise.command.currency.client;

import bg.sofia.uni.fmi.mjt.splitwise.command.CommandLine;
import bg.sofia.uni.fmi.mjt.splitwise.helpers.Helpers;
import bg.sofia.uni.fmi.mjt.splitwise.streams.ReaderWriterCreator;
import bg.sofia.uni.fmi.mjt.splitwise.user.User;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

public class TransformCurrency {
    private static final int CURRENCY = 1;
    private final ReaderWriterCreator directory;
    private final User user;
    private final ExchangeRate rate;

    public TransformCurrency(ReaderWriterCreator directory, User user, ExchangeRate rate) {
        this.directory = directory;
        this.user = user;
        this.rate = rate;
    }

    public String changeCurrency(CommandLine command) {
        try {
            Map<String, Double> currencies = rate.exchange(command.args()[CURRENCY], user.getCurrency());
            String userAppend = user.changeCurrency(currencies);

            Helpers.addInformation(Helpers.updatedGroup(command.line(), directory, userAppend), directory);
            return "Currency successfully changed!";
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException("IO exception in currency", e);
        }
    }
}
