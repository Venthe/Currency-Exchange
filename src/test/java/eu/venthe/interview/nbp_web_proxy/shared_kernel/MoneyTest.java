package eu.venthe.interview.nbp_web_proxy.shared_kernel;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.stream.Stream;

import static java.math.BigDecimal.*;
import static org.junit.jupiter.params.provider.Arguments.of;

class MoneyTest {

    @ParameterizedTest
    @MethodSource({"equalValues", "equalCurrencies"})
    void testIsEqual(Money actual, Money expected) {
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource({"notEqualValues", "notEqualCurrencies"})
    void testIsNotEqual(Money actual, Money expected) {
        Assertions.assertThat(actual).isNotEqualTo(expected);
    }

    static Stream<Arguments> equalValues() {
        return Stream.of(
                of(createMoneyWithValue(ONE), createMoneyWithValue(ONE)),
                of(createMoneyWithValue(ONE), createMoneyWithValue(valueOf(1.0))),
                of(createMoneyWithValue(new BigDecimal("1E+1")), createMoneyWithValue(TEN)),
                of(createMoneyWithValue(TEN), createMoneyWithValue(TEN)),
                of(createMoneyWithValue(TEN), createMoneyWithValue(valueOf(10.0))),
                of(createMoneyWithValue(TEN), createMoneyWithValue(valueOf(10.00000)))
        );
    }

    static Stream<Arguments> notEqualValues() {
        return Stream.of(
                of(createMoneyWithValue(ONE), createMoneyWithValue(ZERO)),
                of(createMoneyWithValue(ONE), createMoneyWithValue(valueOf(1.1))),
                of(createMoneyWithValue(ONE), createMoneyWithValue(valueOf(1.00001))),
                of(createMoneyWithValue(TEN), createMoneyWithValue(ZERO)),
                of(createMoneyWithValue(TEN), createMoneyWithValue(valueOf(10.1))),
                of(createMoneyWithValue(TEN), createMoneyWithValue(valueOf(10.000001)))
        );
    }

    static Stream<Arguments> equalCurrencies() {
        return Stream.of(
                of(createMoneyWithCurrency(Currency.getInstance("CZK")), createMoneyWithCurrency(Currency.getInstance("CZK"))),
                of(createMoneyWithCurrency(Currency.getInstance("PLN")), createMoneyWithCurrency(Currency.getInstance("PLN")))
        );
    }

    static Stream<Arguments> notEqualCurrencies() {
        return Stream.of(
                of(createMoneyWithCurrency(Currency.getInstance("EUR")), createMoneyWithCurrency(Currency.getInstance("CZK"))),
                of(createMoneyWithCurrency(Currency.getInstance("PLN")), createMoneyWithCurrency(Currency.getInstance("CZK")))
        );
    }

    private static Money createMoneyWithValue(BigDecimal value) {
        return Money.of(value, Currency.getInstance("CHF"));
    }

    private static Money createMoneyWithCurrency(Currency currency) {
        return Money.of(TEN, currency);
    }
}
