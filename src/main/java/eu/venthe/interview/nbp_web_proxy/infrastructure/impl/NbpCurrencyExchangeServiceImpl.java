package eu.venthe.interview.nbp_web_proxy.infrastructure.impl;

import com.fasterxml.jackson.databind.JsonNode;
import eu.venthe.interview.nbp_web_proxy.domain.dependencies.CurrencyExchangeFailedException;
import eu.venthe.interview.nbp_web_proxy.domain.dependencies.CurrencyExchangeService;
import eu.venthe.interview.nbp_web_proxy.shared_kernel.Money;
import eu.venthe.nbp.api.DefaultApi;
import eu.venthe.nbp.model.Format;
import eu.venthe.nbp.model.Table;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.Currency;

@Slf4j
public class NbpCurrencyExchangeServiceImpl implements CurrencyExchangeService {
    private final DefaultApi apiClient;

    public NbpCurrencyExchangeServiceImpl(DefaultApi apiClient) {
        this.apiClient = apiClient;
    }

    @Override
    public Money exchange(@NonNull Money money, @NonNull Currency targetCurrency) throws CurrencyExchangeFailedException {
        if (!exchangeFromPLN(money) && !exchangeToPLN(targetCurrency)) {
            throw new UnsupportedOperationException(MessageFormat.format("At this point we don''t support exchanging between two non-PLN currencies. Base currency={0}, Target currency={1}", money.getCurrency(), targetCurrency));
        }

        if (exchangeBetweenSameCurrencies(money, targetCurrency)) {
            log.warn("Exchange not required as both the base and the target currencies are same. currency={}", targetCurrency);
            return money;
        }

        if (money.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Exchange of negative or zero amounts are disallowed");
        }

        // NBP works with PLN as a base currency
        var nonPLNCurrency = pickNonPLNCurrency(money, targetCurrency);
        var exchangeRate = getNewestExchangeRateFor(nonPLNCurrency);

        if (exchangeFromPLN(money)) {
            return Money.of(money.divide(exchangeRate.ask()).getAmount(), targetCurrency);
        } else if (exchangeToPLN(targetCurrency)) {
            return Money.of(money.multiply(exchangeRate.bid()).getAmount(), targetCurrency);
        }

        // Should not happen, as we either ask or bid
        //  It should be caught earlier as well
        throw new UnsupportedOperationException("This should never happen");
    }

    private static boolean exchangeBetweenSameCurrencies(Money money, Currency targetCurrency) {
        return money.getCurrency() == targetCurrency;
    }

    private static Currency pickNonPLNCurrency(Money money, Currency targetCurrency) {
        if (exchangeFromPLN(money)) {
            return targetCurrency;
        }

        if (exchangeToPLN(targetCurrency)) {
            return money.getCurrency();
        }

        throw new UnsupportedOperationException("This should never happen");
    }

    private static boolean exchangeToPLN(Currency targetCurrency) {
        return targetCurrency.equals(Money.PLN);
    }

    private static boolean exchangeFromPLN(Money money) {
        return money.getCurrency().equals(Money.PLN);
    }

    // TODO: Cache the result
    private Rate getNewestExchangeRateFor(Currency otherCurrency) throws CurrencyExchangeFailedException {
        // FIXME: Use apiExchangeratesRatesTableCodeGet
        //  For some reason, the object is not fully populated with data. To work around the issue, I'm extracting data
        //  from JsonNode manually.
        var responseEntity = apiClient.apiExchangeratesRatesTableCodeGetWithResponseSpec(Table.C, otherCurrency.getCurrencyCode(), Format.JSON).toEntity(JsonNode.class);

        if (!responseEntity.getStatusCode().is2xxSuccessful()) {
            throw new CurrencyExchangeFailedException();
        }

        var root = responseEntity.getBody();

        var ratesNode = root.get("rates");
        if (ratesNode.size() != 1) {
            throw new RuntimeException("Unexpected error. When requesting the newest rate there should be only one result");
        }
        var rateNode = ratesNode.get(0);

        return new Rate(
                toBigDecimal(rateNode.get("ask")),
                toBigDecimal(rateNode.get("bid")),
                toLocalDate(rateNode.get("effectiveDate"))
        );
    }

    private LocalDate toLocalDate(JsonNode node) {
        return LocalDate.parse(node.asText());
    }

    private static BigDecimal toBigDecimal(JsonNode node) {
        return new BigDecimal(node.asText());
    }

    /**
     * Represents a person with a name and an age.
     *
     * @param ask           The price a seller is willing to accept for a currency.
     * @param bid           The price a buyer is willing to pay for a currency.
     * @param effectiveDate The date on which the rate is taken into account.
     */
    private record Rate(BigDecimal ask,
                        BigDecimal bid,
                        LocalDate effectiveDate) {

    }
}
