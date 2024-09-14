package eu.venthe.interview.nbp_web_proxy.domain;

import eu.venthe.interview.nbp_web_proxy.shared_kernel.Money;
import eu.venthe.interview.nbp_web_proxy.shared_kernel.persistence.Aggregate;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

import static eu.venthe.interview.nbp_web_proxy.shared_kernel.Money.PLN;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
public class CurrencyAccountAggregate implements Aggregate<CurrencyAccountId> {
    @EqualsAndHashCode.Include
    private final CurrencyAccountId id;
    private final String name;
    private final String surname;
    private Money balance;

    private CurrencyAccountAggregate(@NonNull CurrencyAccountId id, @NonNull Money initialBalance, CustomerInformation customerInformation) {
        validateCustomerInformation(customerInformation);

        this.id = id;
        name = customerInformation.name();
        surname = customerInformation.surname();
        setBalance(initialBalance);
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

    private void setBalance(Money balance) {
        if (!balance.getCurrency().equals(PLN)) {
            throw new IllegalArgumentException();
        }
        this.balance = balance;
    }

    public static CurrencyAccountAggregate open(Money initialBalance, CustomerInformation customerInformation) {
        return new CurrencyAccountAggregate(CurrencyAccountId.create(), initialBalance, customerInformation);
    }
}
