package bg.sofia.uni.fmi.mjt.splitwise.service;

import bg.sofia.uni.fmi.mjt.splitwise.currency.ExchangeRate;
import bg.sofia.uni.fmi.mjt.splitwise.currency.ExchangeRateResponse;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.CurrencyConversionException;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class CurrencyService {
    private static final String BASE_CURRENCY = "EUR";
    private final ExchangeRate exchangeRate;
    private volatile ExchangeRateResponse cachedRates;

    public CurrencyService(ExchangeRate exchangeRate) {
        this.exchangeRate = exchangeRate;
        updateCurrencyListAsync();
    }

    public CompletableFuture<Void> updateCurrencyListAsync() {
        return exchangeRate.getAllRates(BASE_CURRENCY)
                .thenAccept(rates -> {
                    cachedRates = rates;
                })
                .exceptionally(ex -> {
                    throw new CurrencyConversionException("Failed to load currencies!", ex);
                });
    }

    public CompletableFuture<Map<String, Double>> getRates() {
        if (cachedRates != null) {
            return CompletableFuture.completedFuture(cachedRates.rates());
        }

        return updateCurrencyListAsync()
                .thenApply(v -> cachedRates.rates());
    }
}