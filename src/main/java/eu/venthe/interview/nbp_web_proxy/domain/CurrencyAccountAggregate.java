package eu.venthe.interview.nbp_web_proxy.domain;

import eu.venthe.interview.nbp_web_proxy.domain.dependencies.CurrencyExchangeFailedException;
import eu.venthe.interview.nbp_web_proxy.domain.dependencies.CurrencyExchangeService;
import eu.venthe.interview.nbp_web_proxy.shared_kernel.Money;
import eu.venthe.interview.nbp_web_proxy.shared_kernel.persistence.Aggregate;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.Currency;

import static eu.venthe.interview.nbp_web_proxy.shared_kernel.Money.PLN;
import static eu.venthe.interview.nbp_web_proxy.shared_kernel.Money.USD;

@Slf4j
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
public class CurrencyAccountAggregate implements Aggregate<CurrencyAccountId> {
    @Getter(AccessLevel.NONE)
    private final CurrencyExchangeService currencyExchangeService;

    @EqualsAndHashCode.Include
    private final CurrencyAccountId id;
    private final String name;
    private final String surname;
    private Money originalBalance;
    private Money exchangedBalance;

    private CurrencyAccountAggregate(CurrencyExchangeService currencyExchangeService, @NonNull CurrencyAccountId id, CustomerInformation customerInformation, @NonNull Money initialBalance, @NonNull Money exchangedBalance) {
        this.currencyExchangeService = currencyExchangeService;
        validateCustomerInformation(customerInformation);

        this.id = id;
        name = customerInformation.name();
        surname = customerInformation.surname();
        setOriginalBalance(initialBalance);
        setExchangedBalance(exchangedBalance);
    }

    private static void validateCustomerInformation(CustomerInformation customerInformation) {
        if (customerInformation == null) {
            throw new IllegalArgumentException("Customer information cannot be null");
        }
        validateCustomerName(customerInformation.name());
        validateCustomerSurname(customerInformation.surname());
    }

    private static void validateCustomerName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name should not be blank");
        }
    }

    private static void validateCustomerSurname(String surname) {
        if (surname == null || surname.isBlank()) {
            throw new IllegalArgumentException("Surname should not be blank");
        }
    }

    private void setOriginalBalance(Money originalBalance) {
        if (!originalBalance.getCurrency().equals(PLN)) {
            throw new IllegalArgumentException();
        }
        this.originalBalance = originalBalance;
    }

    private void setExchangedBalance(Money exchangedBalance) {
        if (exchangedBalance.getCurrency().equals(originalBalance.getCurrency())) {
            throw new IllegalArgumentException("Exchange originalBalance cannot be the same as the original originalBalance");
        }
        this.exchangedBalance = exchangedBalance;
    }

    // TODO: Consider moving into a factory
    public static CurrencyAccountAggregate open(CurrencyExchangeService currencyExchangeService, CustomerInformation customerInformation, Money initialBalance, Currency currency) {
        if (currency != USD) {
            throw new UnsupportedOperationException("Opening different accounts than USD is not yet supported");
        }

        return new CurrencyAccountAggregate(currencyExchangeService, CurrencyAccountId.create(), customerInformation, initialBalance, Money.of(BigDecimal.ZERO, USD));
    }

    public void exchangeToTargetCurrency(BigDecimal amount) throws CurrencyExchangeFailedException {
        if (originalBalance.getAmount().compareTo(amount) < 0) {
            log.debug("Exchange to Target Currency failed. OriginalBalance={}, ExchangedBalance={}", originalBalance, exchangedBalance);
            throw new IllegalArgumentException("Exchanged amount cannot exceed the original balance");
        }

        var newOriginalBalance = originalBalance.subtract(amount);
        var moneyToExchange = Money.of(amount, originalBalance.getCurrency());
        var exchangedAmount = currencyExchangeService.exchange(moneyToExchange, exchangedBalance.getCurrency());
        var newExchangedBalance = exchangedBalance.add(exchangedAmount);

        log.trace("Exchange succeeded. Exchanged={} to={}, Old OriginalBalance={}, New OriginalBalance={}, Old ExchangedBalance={}, New ExchangedBalance={}",
                moneyToExchange, exchangedAmount, originalBalance, newOriginalBalance, exchangedBalance, newExchangedBalance);

        originalBalance = newOriginalBalance;
        exchangedBalance = newExchangedBalance;
    }

    public void exchangeToBaseCurrency(BigDecimal amount) throws CurrencyExchangeFailedException {
        if (exchangedBalance.getAmount().compareTo(amount) < 0) {
            log.debug("Exchange to Base Currency failed. OriginalBalance={}, ExchangeBalance={}", originalBalance, exchangedBalance);
            throw new IllegalArgumentException("Exchanged amount cannot exceed the exchanged balance");
        }

        var newExchangedBalance = exchangedBalance.subtract(amount);
        var moneyToExchange = Money.of(amount, exchangedBalance.getCurrency());
        var exchangedAmount = currencyExchangeService.exchange(moneyToExchange, originalBalance.getCurrency());
        var newOriginalBalance = originalBalance.add(exchangedAmount);

        log.trace("Exchange succeeded. Exchanged={} to={}, Old OriginalBalance={}, New OriginalBalance={}, Old ExchangedBalance={}, New ExchangedBalance={}",
                moneyToExchange, exchangedAmount, originalBalance, newOriginalBalance, exchangedBalance, newExchangedBalance);

        exchangedBalance = newExchangedBalance;
        originalBalance = newOriginalBalance;
    }
}
