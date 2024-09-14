package eu.venthe.interview.nbp_web_proxy.domain;

import eu.venthe.interview.nbp_web_proxy.shared_kernel.Money;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.stream.Stream;

import static eu.venthe.interview.nbp_web_proxy.shared_kernel.Money.PLN;

class CurrencyAccountAggregateTest {

    private static final CustomerInformation VALID_CUSTOMER_INFORMATION = new CustomerInformation("Jane", "Doe");
    private static final Money VALID_INITIAL_BALANCE = Money.of(BigDecimal.ZERO, PLN);

    @Test
    void shouldHaveIdAfterCreation() {
        // given
        var currencyAccount = CurrencyAccountAggregate.open(VALID_CUSTOMER_INFORMATION, VALID_INITIAL_BALANCE);

        // when
        Assertions.assertThat(currencyAccount.getId()).isNotNull();
    }

    @Test
    void shouldSetValidBalanceAfterCreation() {
        // given
        var currencyAccount = CurrencyAccountAggregate.open(VALID_CUSTOMER_INFORMATION, VALID_INITIAL_BALANCE);

        // when
        Assertions.assertThat(currencyAccount.getBalance()).satisfies(balance -> {
            Assertions.assertThat(balance.getAmount()).isEqualByComparingTo(BigDecimal.ZERO);
            Assertions.assertThat(balance.getCurrency()).isEqualTo(PLN);
        });
    }

    @Test
    void shouldSetNameAfterCreation() {
        // given
        var currencyAccount = CurrencyAccountAggregate.open(VALID_CUSTOMER_INFORMATION, VALID_INITIAL_BALANCE);

        // when
        Assertions.assertThat(currencyAccount.getName()).isEqualTo(VALID_CUSTOMER_INFORMATION.name());
    }

    @Test
    void shouldSetSurnameAfterCreation() {
        // given
        var currencyAccount = CurrencyAccountAggregate.open(VALID_CUSTOMER_INFORMATION, VALID_INITIAL_BALANCE);

        // when
        Assertions.assertThat(currencyAccount.getSurname()).isEqualTo(VALID_CUSTOMER_INFORMATION.surname());
    }

    @Test
    void shouldNotCreateAccountWhenInitialBalanceIsEmpty() {
        // given
        ThrowableAssert.ThrowingCallable throwable = () -> CurrencyAccountAggregate.open(VALID_CUSTOMER_INFORMATION, null);

        // when
        Assertions.assertThatThrownBy(throwable).isInstanceOf(NullPointerException.class);
    }

    @ParameterizedTest
    @MethodSource
    void shouldNotCreateAccountWhenCustomerInformationIsEmpty(CustomerInformation customerInformation) {
        // given
        ThrowableAssert.ThrowingCallable throwable = () -> CurrencyAccountAggregate.open(customerInformation, VALID_INITIAL_BALANCE);

        // when
        Assertions.assertThatThrownBy(throwable).isInstanceOf(IllegalArgumentException.class);
    }

    static Stream<Arguments> shouldNotCreateAccountWhenCustomerInformationIsEmpty() {
        return Stream.<Arguments>builder()
                .add(Arguments.of((CustomerInformation) null))
                .add(Arguments.of(new CustomerInformation(null, null)))
                .add(Arguments.of(new CustomerInformation("", null)))
                .add(Arguments.of(new CustomerInformation(null, "")))
                .add(Arguments.of(new CustomerInformation(" ", null)))
                .add(Arguments.of(new CustomerInformation(null, " ")))
                .build();
    }

    @ParameterizedTest
    @MethodSource
    void shouldNotCreateAnAccountWhenBalanceIsNotInPLN(Currency currency) {
        // given
        ThrowableAssert.ThrowingCallable throwable = () -> CurrencyAccountAggregate.open(VALID_CUSTOMER_INFORMATION, Money.of(BigDecimal.ZERO, currency));

        // when
        Assertions.assertThatThrownBy(throwable).isInstanceOf(IllegalArgumentException.class);
    }

    static Stream<Arguments> shouldNotCreateAnAccountWhenBalanceIsNotInPLN() {
        return Currency.getAvailableCurrencies().stream()
                .filter(e -> !e.equals(PLN))
                .map(Arguments::of);
    }
}
