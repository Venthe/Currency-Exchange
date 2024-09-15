package eu.venthe.interview.nbp_web_proxy.domain;

import eu.venthe.interview.nbp_web_proxy.domain.dependencies.CurrencyExchangeFailedException;
import eu.venthe.interview.nbp_web_proxy.domain.dependencies.CurrencyExchangeService;
import eu.venthe.interview.nbp_web_proxy.shared_kernel.Money;
import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import static eu.venthe.interview.nbp_web_proxy.shared_kernel.Money.PLN;
import static eu.venthe.interview.nbp_web_proxy.shared_kernel.Money.USD;
import static org.mockito.ArgumentMatchers.any;

class CurrencyAccountTest {

    private static final CustomerInformation VALID_CUSTOMER_INFORMATION = new CustomerInformation("Jane", "Doe");
    private static final Money VALID_INITIAL_BALANCE = Money.of(BigDecimal.ZERO, PLN);
    private static final Currency EXAMPLE_EXCHANGE_CURRENCY = USD;
    private static final CurrencyExchangeService MOCK_EXCHANGE_SERVICE = Mockito.mock(CurrencyExchangeService.class);

    @Test
    void shouldHaveIdAfterCreation() {
        // given
        var currencyAccount = CurrencyAccount.open(MOCK_EXCHANGE_SERVICE, VALID_CUSTOMER_INFORMATION, VALID_INITIAL_BALANCE, EXAMPLE_EXCHANGE_CURRENCY);

        // when
        Assertions.assertThat(currencyAccount.getId()).isNotNull();
    }

    @Test
    void shouldSetValidOriginalBalanceAfterCreation() {
        // given
        var currencyAccount = CurrencyAccount.open(MOCK_EXCHANGE_SERVICE, VALID_CUSTOMER_INFORMATION, VALID_INITIAL_BALANCE, EXAMPLE_EXCHANGE_CURRENCY);

        // when
        Assertions.assertThat(currencyAccount.getOriginalBalance()).satisfies(balance -> {
            Assertions.assertThat(balance.getAmount()).isEqualByComparingTo(BigDecimal.ZERO);
            Assertions.assertThat(balance.getCurrency()).isEqualTo(PLN);
        });
    }

    @Test
    void shouldSetValidExchangedBalanceAfterCreation() {
        // given
        var currencyAccount = CurrencyAccount.open(MOCK_EXCHANGE_SERVICE, VALID_CUSTOMER_INFORMATION, VALID_INITIAL_BALANCE, EXAMPLE_EXCHANGE_CURRENCY);

        // when
        Assertions.assertThat(currencyAccount.getForeignBalance()).satisfies(balance -> {
            Assertions.assertThat(balance.getAmount()).isEqualByComparingTo(BigDecimal.ZERO);
            Assertions.assertThat(balance.getCurrency()).isEqualTo(USD);
        });
    }

    @Test
    void shouldSetNameAfterCreation() {
        // given
        var currencyAccount = CurrencyAccount.open(MOCK_EXCHANGE_SERVICE, VALID_CUSTOMER_INFORMATION, VALID_INITIAL_BALANCE, EXAMPLE_EXCHANGE_CURRENCY);

        // when
        Assertions.assertThat(currencyAccount.getName()).isEqualTo(VALID_CUSTOMER_INFORMATION.name());
    }

    @Test
    void shouldSetSurnameAfterCreation() {
        // given
        var currencyAccount = CurrencyAccount.open(MOCK_EXCHANGE_SERVICE, VALID_CUSTOMER_INFORMATION, VALID_INITIAL_BALANCE, EXAMPLE_EXCHANGE_CURRENCY);

        // when
        Assertions.assertThat(currencyAccount.getSurname()).isEqualTo(VALID_CUSTOMER_INFORMATION.surname());
    }

    @Test
    void shouldNotCreateAccountWhenInitialBalanceIsEmpty() {
        // given
        ThrowableAssert.ThrowingCallable throwable = () -> CurrencyAccount.open(MOCK_EXCHANGE_SERVICE, VALID_CUSTOMER_INFORMATION, null, EXAMPLE_EXCHANGE_CURRENCY);

        // when
        Assertions.assertThatThrownBy(throwable).isInstanceOf(NullPointerException.class);
    }

    @ParameterizedTest
    @MethodSource
    void shouldNotCreateAccountWhenExchangeCurrencyIsNotUSD(Currency currency) {
        // given
        ThrowableAssert.ThrowingCallable throwable = () -> CurrencyAccount.open(MOCK_EXCHANGE_SERVICE, VALID_CUSTOMER_INFORMATION, VALID_INITIAL_BALANCE, currency);

        // when
        Assertions.assertThatThrownBy(throwable).isInstanceOf(UnsupportedOperationException.class).hasMessage("Opening different accounts than USD is not yet supported");
    }

    static Stream<Arguments> shouldNotCreateAccountWhenExchangeCurrencyIsNotUSD() {
        return Currency.getAvailableCurrencies().stream()
                .filter(e -> !e.equals(USD))
                .map(Arguments::of);
    }

    @ParameterizedTest
    @MethodSource
    void shouldNotCreateAccountWhenCustomerInformationIsEmpty(CustomerInformation customerInformation) {
        // given
        ThrowableAssert.ThrowingCallable throwable = () -> CurrencyAccount.open(MOCK_EXCHANGE_SERVICE, customerInformation, VALID_INITIAL_BALANCE, EXAMPLE_EXCHANGE_CURRENCY);

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
        ThrowableAssert.ThrowingCallable throwable = () -> CurrencyAccount.open(MOCK_EXCHANGE_SERVICE, VALID_CUSTOMER_INFORMATION, Money.of(BigDecimal.ZERO, currency), EXAMPLE_EXCHANGE_CURRENCY);

        // when
        Assertions.assertThatThrownBy(throwable).isInstanceOf(IllegalArgumentException.class);
    }

    static Stream<Arguments> shouldNotCreateAnAccountWhenBalanceIsNotInPLN() {
        return Currency.getAvailableCurrencies().stream()
                .filter(e -> !e.equals(PLN))
                .map(Arguments::of);
    }

    @Nested
    class Exchange {

        @Test
        void shouldThrowErrorWhenTheForeignBalanceIsTooLowToExchangeToOriginalCurrency() {
            // given
            var account = CurrencyAccount.open(MOCK_EXCHANGE_SERVICE, VALID_CUSTOMER_INFORMATION, Money.of(BigDecimal.ZERO, PLN), EXAMPLE_EXCHANGE_CURRENCY);

            // when
            ThrowableAssert.ThrowingCallable throwable = () -> account.exchangeToOriginalCurrency(BigDecimal.ONE);

            // when
            Assertions.assertThatThrownBy(throwable)
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Exchanged amount cannot exceed the exchanged initialBalance");
        }

        @Test
        void shouldThrowErrorWhenTheOriginalBalanceIsTooLowToExchangeToForeignCurrency() {
            // given
            var account = CurrencyAccount.open(MOCK_EXCHANGE_SERVICE, VALID_CUSTOMER_INFORMATION, Money.of(BigDecimal.ZERO, PLN), EXAMPLE_EXCHANGE_CURRENCY);

            // when
            ThrowableAssert.ThrowingCallable throwable = () -> account.exchangeToForeignCurrency(BigDecimal.ONE);

            // when
            Assertions.assertThatThrownBy(throwable)
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Exchanged amount cannot exceed the original initialBalance");
        }

        @SneakyThrows
        @ParameterizedTest
        @MethodSource
        void exchangeScenarios(Money initialBalance,
                               Function<CurrencyAccount, CurrencyAccount> operator,
                               Money expectedOriginalBalance,
                               Money expectedForeignBalance) {
            // given
            var account = CurrencyAccount.open(MOCK_EXCHANGE_SERVICE, VALID_CUSTOMER_INFORMATION, initialBalance, EXAMPLE_EXCHANGE_CURRENCY);
            Mockito.doAnswer(setupExchange()).when(MOCK_EXCHANGE_SERVICE).exchange(any(Money.class), any(Currency.class));

            // when
            operator.apply(account);

            // when
            Assertions.assertThat(account.getOriginalBalance())
                    .isEqualTo(expectedOriginalBalance);
            Assertions.assertThat(account.getForeignBalance())
                    .isEqualTo(expectedForeignBalance);
        }

        static Stream<Arguments> exchangeScenarios() {
            return Stream.<Arguments>builder()
                    .add(Arguments.of(
                            pln(10),
                            exchangeToForeignCurrency(10),
                            pln(0),
                            usd(5)
                    ))
                    .add(Arguments.of(
                            pln(1),
                            exchangeToForeignCurrency(0.5),
                            pln(0.5),
                            usd(0.25)
                    ))
                    .add(Arguments.of(
                            pln(0.000001),
                            exchangeToForeignCurrency(0.0000005),
                            pln(0.0000005),
                            usd(0.0000003)
                    ))
                    .add(Arguments.of(
                            pln(1),
                            exchangeToForeignCurrency(1).andThen(exchangeToOriginalCurrency(0.25)),
                            pln(0.5),
                            usd(0.25)
                    ))
                    .build();
        }

        @SneakyThrows
        private static UnaryOperator<CurrencyAccount> exchangeToForeignCurrency(double amount) {
            return agg -> {
                try {
                    agg.exchangeToForeignCurrency(BigDecimal.valueOf(amount));
                    return agg;
                } catch (CurrencyExchangeFailedException e) {
                    throw new RuntimeException();
                }
            };
        }

        private static UnaryOperator<CurrencyAccount> exchangeToOriginalCurrency(double amount) {
            return agg -> {
                try {
                    agg.exchangeToOriginalCurrency(BigDecimal.valueOf(amount));
                    return agg;
                } catch (CurrencyExchangeFailedException e) {
                    throw new RuntimeException();
                }
            };
        }

        private static Money pln(double amount) {
            return money(amount, PLN);
        }

        private static Money usd(double amount) {
            return money(amount, USD);
        }

        private static Money money(double amount, Currency pln) {
            return Money.of(BigDecimal.valueOf(amount), pln);
        }

        private static Answer setupExchange() {
            return invocationOnMock -> {
                Money money = invocationOnMock.getArgument(0);
                Currency currency = invocationOnMock.getArgument(1);

                if (money.getCurrency() == PLN && currency == USD) {
                    return Money.of(money.divide(BigDecimal.TWO).getAmount(), USD);
                } else if (money.getCurrency() == USD && currency == PLN) {
                    return Money.of(money.multiply(BigDecimal.TWO).getAmount(), PLN);
                }

                throw new RuntimeException();
            };
        }
    }
}
