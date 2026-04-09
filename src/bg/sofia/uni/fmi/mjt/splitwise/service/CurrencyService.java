package bg.sofia.uni.fmi.mjt.splitwise.service;

import bg.sofia.uni.fmi.mjt.splitwise.currency.ExchangeRate;
import bg.sofia.uni.fmi.mjt.splitwise.currency.ExchangeRateResponse;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.CurrencyConversionException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.UnknownCurrencyException;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class CurrencyService implements Service {
    private static final String BASE_CURRENCY = "EUR";
    private final ExchangeRate exchangeRate;
    private volatile ExchangeRateResponse cachedRates;

    public CurrencyService(ExchangeRate exchangeRate) {
        this.exchangeRate = exchangeRate;
        updateCurrencyListAsync().join();
    }

    public CompletableFuture<Void> updateCurrencyListAsync() {
        return exchangeRate.getAllRates()
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

    public BigDecimal transformAmount(BigDecimal originalAmount, String currentCurrency, String wantedCurrency) {
        Double currentCurrencyRate = this.cachedRates.rates().get(currentCurrency);
        Double wantedCurrencyRate = this.cachedRates.rates().get(wantedCurrency);

        if (currentCurrency.equals(wantedCurrency)) {
            return originalAmount;
        } else if (currentCurrencyRate == null) {
            throw new UnknownCurrencyException("The provided current currency is not in the list!");
        } else if (wantedCurrencyRate == null) {
            throw new UnknownCurrencyException("The wanted currency is not part of the list!");
        }

        BigDecimal inBaseCurrency = originalAmount.divide(BigDecimal.valueOf(currentCurrencyRate));
        return inBaseCurrency.multiply(BigDecimal.valueOf(wantedCurrencyRate));
    }

    public BigDecimal transformToBaseCurrency(BigDecimal amount, String currentCurrency) {
        Double currentCurrencyRate = this.cachedRates.rates().get(currentCurrency);

        if (currentCurrency.equals(BASE_CURRENCY)) {
            return amount;
        }
        return amount.divide(BigDecimal.valueOf(currentCurrencyRate));
    }
}