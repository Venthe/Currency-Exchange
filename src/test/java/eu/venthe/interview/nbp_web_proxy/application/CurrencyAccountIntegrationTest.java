package eu.venthe.interview.nbp_web_proxy.application;

import eu.venthe.interview.nbp_web_proxy.AbstractBaseIntegrationTest;
import eu.venthe.interview.nbp_web_proxy.domain.dependencies.CurrencyExchangeService;
import eu.venthe.interview.nbp_web_proxy.shared_kernel.Money;
import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;

@ExtendWith(MockitoExtension.class)
class CurrencyAccountIntegrationTest extends AbstractBaseIntegrationTest {
    private static final Money VALID_BALANCE = Money.of(BigDecimal.ZERO, Money.PLN);
    private static final CurrencyAccountSpecification EXAMPLE_ACCOUNT_SPECIFICATION = new CurrencyAccountSpecification("Jane", "Doe", VALID_BALANCE, Money.USD);

    @Autowired
    CurrencyAccountCommandService commandService;

    @Autowired
    CurrencyAccountQueryService queryService;

    @MockBean
    CurrencyExchangeService currencyExchangeService;

    @Test
    void openedAccountShouldExist() {
        var accountId = commandService.openAccount(EXAMPLE_ACCOUNT_SPECIFICATION);

        var result = queryService.doesAccountExist(accountId);

        Assertions.assertThat(result).isTrue();
    }

    @Test
    void canCheckBasicAccountInformation() {
        var accountId = commandService.openAccount(EXAMPLE_ACCOUNT_SPECIFICATION);

        var result = queryService.getAccountInformation(accountId).orElseThrow();

        Assertions.assertThat(result).satisfies(r -> {
            Assertions.assertThat(r.originalBalance()).isEqualByComparingTo(VALID_BALANCE);
            Assertions.assertThat(r.id()).isEqualTo(accountId);
            Assertions.assertThat(r.ownerName()).isEqualTo(EXAMPLE_ACCOUNT_SPECIFICATION.name());
            Assertions.assertThat(r.ownerSurname()).isEqualTo(EXAMPLE_ACCOUNT_SPECIFICATION.surname());
        });
    }

    @SneakyThrows
    @Test
    void canExchangeToForeignCurrency() {
        // given
        var initialBalance = Money.of(BigDecimal.TEN, Money.PLN);
        var moneyAfterConversion = Money.of(BigDecimal.TWO, Money.USD);
        var exchangedValue = BigDecimal.valueOf(5);
        Mockito.when(currencyExchangeService.exchange(Money.of(exchangedValue, Money.PLN), Money.USD)).thenReturn(moneyAfterConversion);

        var accountId = commandService.openAccount(EXAMPLE_ACCOUNT_SPECIFICATION.withInitialBalance(initialBalance));

        // when
        commandService.exchangeToForeignCurrency(accountId, exchangedValue);

        // then
        var result = queryService.getAccountInformation(accountId).orElseThrow();
        Assertions.assertThat(result).satisfies(r -> {
            Assertions.assertThat(r.originalBalance()).isEqualByComparingTo(initialBalance.subtract(exchangedValue));
            Assertions.assertThat(r.exchangedBalance()).isEqualByComparingTo(moneyAfterConversion);
            Assertions.assertThat(r.id()).isEqualTo(accountId);
            Assertions.assertThat(r.ownerName()).isEqualTo(EXAMPLE_ACCOUNT_SPECIFICATION.name());
            Assertions.assertThat(r.ownerSurname()).isEqualTo(EXAMPLE_ACCOUNT_SPECIFICATION.surname());
        });
    }

    @SneakyThrows
    @Test
    void canExchangeToOriginalCurrency() {
        // given
        var exchangedValue = BigDecimal.valueOf(5);
        var initialBalance = Money.of(exchangedValue, Money.PLN);
        var moneyAfterFirstConversion = Money.of(BigDecimal.TWO, Money.USD);
        var moneyAfterSecondConversion = Money.of(BigDecimal.TEN, Money.PLN);
        Mockito.when(currencyExchangeService.exchange(Money.of(exchangedValue, Money.PLN), Money.USD)).thenReturn(moneyAfterFirstConversion);
        Mockito.when(currencyExchangeService.exchange(Money.of(BigDecimal.ONE, Money.USD), Money.PLN)).thenReturn(moneyAfterSecondConversion);

        var accountId = commandService.openAccount(EXAMPLE_ACCOUNT_SPECIFICATION.withInitialBalance(initialBalance));
        commandService.exchangeToForeignCurrency(accountId, exchangedValue);

        // when
        commandService.exchangeToOriginalCurrency(accountId, BigDecimal.ONE);

        // then
        var result = queryService.getAccountInformation(accountId).orElseThrow();
        Assertions.assertThat(result).satisfies(r -> {
            Assertions.assertThat(r.originalBalance()).isEqualByComparingTo(moneyAfterSecondConversion);
            Assertions.assertThat(r.exchangedBalance()).isEqualByComparingTo(Money.of(BigDecimal.ONE, Money.USD));
            Assertions.assertThat(r.id()).isEqualTo(accountId);
            Assertions.assertThat(r.ownerName()).isEqualTo(EXAMPLE_ACCOUNT_SPECIFICATION.name());
            Assertions.assertThat(r.ownerSurname()).isEqualTo(EXAMPLE_ACCOUNT_SPECIFICATION.surname());
        });
    }
}
