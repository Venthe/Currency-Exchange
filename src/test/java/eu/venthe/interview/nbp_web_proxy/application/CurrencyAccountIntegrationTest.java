package eu.venthe.interview.nbp_web_proxy.application;

import eu.venthe.interview.nbp_web_proxy.AbstractBaseIntegrationTest;
import eu.venthe.interview.nbp_web_proxy.shared_kernel.Money;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

class CurrencyAccountIntegrationTest extends AbstractBaseIntegrationTest {
    private static final Money VALID_BALANCE = Money.of(BigDecimal.ZERO, Money.PLN);
    private static final CurrencyAccountSpecification EXAMPLE_ACCOUNT_SPECIFICATION = new CurrencyAccountSpecification(VALID_BALANCE, "Jane", "Doe");

    @Autowired
    CurrencyAccountCommandService commandService;

    @Autowired
    CurrencyAccountQueryService queryService;

    @Test
    void openedAccountShouldExist() {
        var accountId = commandService.openAccount(EXAMPLE_ACCOUNT_SPECIFICATION);

        var result = queryService.doesAccountExist(accountId);

        Assertions.assertThat(result).isTrue();
    }
}
