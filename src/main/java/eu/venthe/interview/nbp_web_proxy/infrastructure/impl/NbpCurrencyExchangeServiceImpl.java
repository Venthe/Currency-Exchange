package eu.venthe.interview.nbp_web_proxy.infrastructure.impl;

import com.fasterxml.jackson.databind.JsonNode;
import eu.venthe.interview.nbp_web_proxy.domain.dependencies.CurrencyExchangeFailedException;
import eu.venthe.interview.nbp_web_proxy.domain.dependencies.CurrencyExchangeService;
import eu.venthe.interview.nbp_web_proxy.shared_kernel.CacheManager;
import eu.venthe.interview.nbp_web_proxy.shared_kernel.ClockService;
import eu.venthe.interview.nbp_web_proxy.shared_kernel.Money;
import eu.venthe.nbp.api.DefaultApi;
import eu.venthe.nbp.model.Format;
import eu.venthe.nbp.model.Table;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Currency;

@Slf4j
public class NbpCurrencyExchangeServiceImpl implements CurrencyExchangeService {
    private final CacheManager<NBPExchangeRate> cacheManager;
    private final ClockService clockService;
    private final DefaultApi apiClient;

    public NbpCurrencyExchangeServiceImpl(CacheManager<NBPExchangeRate> cacheManager, ClockService clockService, DefaultApi apiClient) {
        this.clockService = clockService;
        this.apiClient = apiClient;
        this.cacheManager = cacheManager;
    }

    @Override
    public Money exchange(@NonNull Money money, @NonNull Currency targetCurrency) throws CurrencyExchangeFailedException {
        if (!exchangeFromPLN(money) && !exchangeToPLN(targetCurrency)) {
            throw new UnsupportedOperationException(MessageFormat.format("At this point we don''t support exchanging between two non-PLN currencies. Original currency={0}, Target currency={1}", money.getCurrency(), targetCurrency));
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

    private NBPExchangeRate getNewestExchangeRateFor(Currency otherCurrency) throws CurrencyExchangeFailedException {
        var expectedEffectiveDate = getExpectedEffectiveDateAdjustedForWeekends();
        var cacheKey = getCacheKey(otherCurrency, expectedEffectiveDate);
        if (cacheManager.isCached(cacheKey)) {
            var cachedRate = cacheManager.retrieve(cacheKey).orElseThrow();
            log.debug("Retrieved cached rate rate={}", cachedRate);
            return cachedRate;
        }

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

        var rate = new NBPExchangeRate(
                toBigDecimal(rateNode.get("ask")),
                toBigDecimal(rateNode.get("bid")),
                toLocalDate(rateNode.get("effectiveDate"))
        );

        // NBP publishes new rates between 7:45 and 8:15, so in the morning the rates should not be cached
        // This also handles weekend backdate, as the published rates are already dated on friday
        var rateCacheKey = getCacheKey(otherCurrency, rate.effectiveDate());
        if (!cacheManager.isCached(rateCacheKey)) {
            log.debug("Storing rate in cache. Rate={}", rate);
            cacheManager.store(cacheKey, rate);
        }

        return rate;
    }

    private LocalDate toLocalDate(JsonNode node) {
        return LocalDate.parse(node.asText());
    }

    private static BigDecimal toBigDecimal(JsonNode node) {
        return new BigDecimal(node.asText());
    }

    // NBP publishes on work days
    private LocalDate getExpectedEffectiveDateAdjustedForWeekends() {
        var effectiveDate = clockService.getZonedNow().withZoneSameInstant(ZoneOffset.UTC).toLocalDate();
        if (effectiveDate.getDayOfWeek() == DayOfWeek.SATURDAY || effectiveDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
            return effectiveDate.minusDays(effectiveDate.getDayOfWeek().getValue() - 5);
        }
        return effectiveDate;
    }

    // NBP publishes usually between 7:45 and 8:15 CET; but for our purposes we only need to know should we store for "today"
    private String getCacheKey(Currency otherCurrency, LocalDate localDate) {
        return "%s_%s".formatted(otherCurrency.getCurrencyCode(), localDate);
    }
}
