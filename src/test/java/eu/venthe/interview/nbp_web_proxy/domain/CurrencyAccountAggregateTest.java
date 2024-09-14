package eu.venthe.interview.nbp_web_proxy.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class CurrencyAccountAggregateTest {
    @Test
    void shouldHaveIdAfterCreation() {
        // given
        var currencyAccount = CurrencyAccountAggregate.create();

        // when
        Assertions.assertThat(currencyAccount.getId()).isNotNull();
    }
}
