package eu.venthe.interview.nbp_web_proxy.domain;

import eu.venthe.interview.nbp_web_proxy.shared_kernel.Money;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static eu.venthe.interview.nbp_web_proxy.shared_kernel.Money.PLN;

class CurrencyAccountAggregateTest {

    private static final Money VALID_INITIAL_BALANCE = Money.of(BigDecimal.ZERO, PLN);

    @Test
    void shouldHaveIdAfterCreation() {
        // given
        var currencyAccount = CurrencyAccountAggregate.open(VALID_INITIAL_BALANCE);

        // when
        Assertions.assertThat(currencyAccount.getId()).isNotNull();
    }

    @Test
    void shouldSetValidBalanceAfterCreation() {
        // given
        var currencyAccount = CurrencyAccountAggregate.open(VALID_INITIAL_BALANCE);

        // when
        Assertions.assertThat(currencyAccount.getBalance()).satisfies(balance -> {
            Assertions.assertThat(balance.getAmount()).isEqualByComparingTo(BigDecimal.ZERO);
            Assertions.assertThat(balance.getCurrency()).isEqualTo(PLN);
        });
    }
}
