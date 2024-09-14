package eu.venthe.interview.nbp_web_proxy.application;

import eu.venthe.interview.nbp_web_proxy.AbstractBaseIntegrationTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class CurrencyAccountIntegrationTest extends AbstractBaseIntegrationTest {
    @Autowired
    CurrencyAccountCommandService commandService;

    @Autowired
    CurrencyAccountQueryService queryService;

    @Test
    void openedAccountShouldExist() {
        var accountId = commandService.openAccount();

        var result = queryService.doesAccountExist(accountId);

        Assertions.assertThat(result).isTrue();
    }
}
