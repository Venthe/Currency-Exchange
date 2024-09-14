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
    private Money balance;

    private CurrencyAccountAggregate(@NonNull CurrencyAccountId id, @NonNull Money initialBalance) {
        this.id = id;
        setBalance(initialBalance);
    }

    private void setBalance(Money balance) {
        if (!balance.getCurrency().equals(PLN)) {
            throw new IllegalArgumentException();
        }
        this.balance = balance;
    }

    public static CurrencyAccountAggregate open(Money initialBalance) {
        return new CurrencyAccountAggregate(CurrencyAccountId.create(), initialBalance);
    }
}
