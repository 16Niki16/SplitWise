package bg.sofia.uni.fmi.mjt.splitwise.command.currency.client;

import bg.sofia.uni.fmi.mjt.splitwise.command.Command;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.NotCorrectQueryException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.UnknownCurrencyException;
import bg.sofia.uni.fmi.mjt.splitwise.helpers.ExceptionFormater;
import bg.sofia.uni.fmi.mjt.splitwise.helpers.Helpers;
import bg.sofia.uni.fmi.mjt.splitwise.streams.ReaderWriterCreator;
import bg.sofia.uni.fmi.mjt.splitwise.user.User;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.util.Map;

public class TransformCurrency {
    private static final int CURRENCY = 1;
    private final ReaderWriterCreator directory;
    private final ReaderWriterCreator exceptions;
    private final User user;
    private final HttpClient client;

    public TransformCurrency(ReaderWriterCreator directory, User user, HttpClient client,
                             ReaderWriterCreator exceptions) {
        this.directory = directory;
        this.user = user;
        this.client = client;
        this.exceptions = exceptions;
    }

    public String changeCurrency(Command command) {
        try {
            GetExchangeRate exchange = new GetExchangeRate(client);
            Map<String, Double> currencies = exchange.exchange(command.args()[CURRENCY], user.getCurrency());
            String userAppend = user.changeCurrency(currencies);
            System.out.println(userAppend);

            Helpers.addInformation(Helpers.updatedGroup(command.line(), directory, userAppend), directory);
            return "Currency successfully changed!";
        } catch (URISyntaxException | IOException e) {
            ExceptionFormater.exceptionAdd(user.getUsername(), "IO exception in currency",
                    e.getStackTrace(), exceptions);
            throw new RuntimeException("IO exception in currency", e);
        } catch (UnknownCurrencyException | NotCorrectQueryException e) {
            ExceptionFormater.exceptionAdd(user.getUsername(), e.getLocalizedMessage(), e.getStackTrace(), exceptions);
            return e.getLocalizedMessage();
        }
    }
}
