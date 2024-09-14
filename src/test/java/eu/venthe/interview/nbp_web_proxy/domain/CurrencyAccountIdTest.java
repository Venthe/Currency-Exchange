package eu.venthe.interview.nbp_web_proxy.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;

class CurrencyAccountIdTest {
    @Test
    void newlyGeneratedIdIsValid() {
        // given & when
        var id = CurrencyAccountId.create().value();

        // then
        Assertions.assertThat(id)
                .isNotNull()
                .isInstanceOf(UUID.class);
    }
}
