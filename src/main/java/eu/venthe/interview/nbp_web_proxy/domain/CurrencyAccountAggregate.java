package eu.venthe.interview.nbp_web_proxy.domain;

import eu.venthe.interview.nbp_web_proxy.shared_kernel.Money;
import eu.venthe.interview.nbp_web_proxy.shared_kernel.persistence.Aggregate;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

import java.math.BigDecimal;
import java.util.Currency;

import static eu.venthe.interview.nbp_web_proxy.shared_kernel.Money.PLN;
import static eu.venthe.interview.nbp_web_proxy.shared_kernel.Money.USD;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
public class CurrencyAccountAggregate implements Aggregate<CurrencyAccountId> {
    @EqualsAndHashCode.Include
    private final CurrencyAccountId id;
    private final String name;
    private final String surname;
    private Money originalBalance;
    private Money exchangedBalance;

    private CurrencyAccountAggregate(@NonNull CurrencyAccountId id, CustomerInformation customerInformation, @NonNull Money initialBalance, @NonNull Money exchangedBalance) {
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

    public static CurrencyAccountAggregate open(CustomerInformation customerInformation, Money initialBalance, Currency currency) {
        if (currency != USD) {
            throw new UnsupportedOperationException("Opening different accounts than USD is not yet supported");
        }

        return new CurrencyAccountAggregate(CurrencyAccountId.create(), customerInformation, initialBalance, Money.of(BigDecimal.ZERO, USD));
    }
}
