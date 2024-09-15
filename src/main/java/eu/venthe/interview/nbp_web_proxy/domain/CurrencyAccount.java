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

/**
 * An account that holds funds in multiple currencies, allowing for exchanges and balances in different currencies.
 */
@Slf4j
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
public class CurrencyAccount implements Aggregate<CurrencyAccountId> {
    @Getter(AccessLevel.NONE)
    private final CurrencyExchangeService currencyExchangeService;

    @EqualsAndHashCode.Include
    private final CurrencyAccountId id;
    private final String name;
    private final String surname;
    /**
     * The primary currency in which an account is denominated. It is the currency in which the account holder typically transacts.
     */
    private Money originalBalance;
    /**
     * A currency other than the original currency of the account.
     */
    private Money foreignBalance;

    private CurrencyAccount(CurrencyExchangeService currencyExchangeService, @NonNull CurrencyAccountId id, CustomerInformation customerInformation, @NonNull Money initialBalance, @NonNull Money initialForeignBalance) {
        this.currencyExchangeService = currencyExchangeService;
        validateCustomerInformation(customerInformation);

        this.id = id;
        name = customerInformation.name();
        surname = customerInformation.surname();
        setOriginalBalance(initialBalance);
        setForeignBalance(initialForeignBalance);
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

    private void setForeignBalance(Money foreignBalance) {
        if (foreignBalance.getCurrency().equals(originalBalance.getCurrency())) {
            throw new IllegalArgumentException("Foreign initialBalance currency cannot be the same as the original initialBalance currency");
        }
        this.foreignBalance = foreignBalance;
    }

    // TODO: Consider moving into a factory
    public static CurrencyAccount open(CurrencyExchangeService currencyExchangeService, CustomerInformation customerInformation, Money initialBalance, Currency foreignCurrency) {
        if (foreignCurrency != USD) {
            throw new UnsupportedOperationException("Opening different accounts than USD is not yet supported");
        }

        return new CurrencyAccount(currencyExchangeService, CurrencyAccountId.create(), customerInformation, initialBalance, Money.of(BigDecimal.ZERO, USD));
    }

    public void exchangeToForeignCurrency(BigDecimal amount) throws CurrencyExchangeFailedException {
        if (originalBalance.getAmount().compareTo(amount) < 0) {
            log.debug("Exchange to foreign currency failed. OriginalBalance={}, ForeignBalance={}", originalBalance, foreignBalance);
            throw new IllegalArgumentException("Exchanged amount cannot exceed the original initialBalance");
        }

        var newOriginalBalance = originalBalance.subtract(amount);
        var moneyToExchange = Money.of(amount, originalBalance.getCurrency());
        var exchangedAmount = currencyExchangeService.exchange(moneyToExchange, foreignBalance.getCurrency());
        var newForeignBalance = foreignBalance.add(exchangedAmount);

        log.trace("Exchange succeeded. Exchanged={} to={}, Old OriginalBalance={}, New OriginalBalance={}, Old ForeignBalance={}, New ForeignBalance={}",
                moneyToExchange, exchangedAmount, originalBalance, newOriginalBalance, foreignBalance, newForeignBalance);

        originalBalance = newOriginalBalance;
        foreignBalance = newForeignBalance;
    }

    public void exchangeToOriginalCurrency(BigDecimal amount) throws CurrencyExchangeFailedException {
        if (foreignBalance.getAmount().compareTo(amount) < 0) {
            log.debug("Exchange to original currency failed. OriginalBalance={}, ForeignBalance={}", originalBalance, foreignBalance);
            throw new IllegalArgumentException("Exchanged amount cannot exceed the exchanged initialBalance");
        }

        var newForeignBalance = foreignBalance.subtract(amount);
        var moneyToExchange = Money.of(amount, foreignBalance.getCurrency());
        var exchangedAmount = currencyExchangeService.exchange(moneyToExchange, originalBalance.getCurrency());
        var newOriginalBalance = originalBalance.add(exchangedAmount);

        log.trace("Exchange succeeded. Exchanged={} to={}, Old OriginalBalance={}, New OriginalBalance={}, Old ForeignBalance={}, New ForeignBalance={}",
                moneyToExchange, exchangedAmount, originalBalance, newOriginalBalance, foreignBalance, newForeignBalance);

        foreignBalance = newForeignBalance;
        originalBalance = newOriginalBalance;
    }
}
