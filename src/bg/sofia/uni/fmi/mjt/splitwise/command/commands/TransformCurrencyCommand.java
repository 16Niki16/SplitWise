package bg.sofia.uni.fmi.mjt.splitwise.command.commands;

import bg.sofia.uni.fmi.mjt.splitwise.client.request.dto.Data;
import bg.sofia.uni.fmi.mjt.splitwise.client.request.dto.TransformCurrencyData;
import bg.sofia.uni.fmi.mjt.splitwise.containers.User;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.DataException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.UnknownCurrencyException;
import bg.sofia.uni.fmi.mjt.splitwise.response.Response;
import bg.sofia.uni.fmi.mjt.splitwise.response.ResponseData;
import bg.sofia.uni.fmi.mjt.splitwise.response.TransformCurrencyResponse;
import bg.sofia.uni.fmi.mjt.splitwise.server.SessionsManager;
import bg.sofia.uni.fmi.mjt.splitwise.service.ApplicationServices;
import bg.sofia.uni.fmi.mjt.splitwise.service.CurrencyService;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class TransformCurrencyCommand implements Command<TransformCurrencyData> {
    private final ApplicationServices applicationServices;
    private final SessionsManager sessionsManager;

    @Override
    public ResponseData execute(String token, TransformCurrencyData transformCurrencyData) {
        CurrencyService currencyService = applicationServices.getCurrencyService();
        User user = sessionsManager.getUserSession(token);
        String currentCurrency = user.getCurrency();

        currencyService.getRates()
            .thenAccept(rates -> {
                if (!rates.containsKey(transformCurrencyData.newCurrency())) {
                    throw new UnknownCurrencyException("Unknown currency: " + transformCurrencyData.newCurrency());
                }
            });
        user.changeCurrency(transformCurrencyData.newCurrency());

        return TransformCurrencyResponse.of(currentCurrency, transformCurrencyData.newCurrency());
    }
}
