package eu.venthe.interview.nbp_web_proxy.infrastructure.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.venthe.interview.nbp_web_proxy.domain.dependencies.CurrencyExchangeFailedException;
import eu.venthe.interview.nbp_web_proxy.domain.dependencies.CurrencyExchangeService;
import eu.venthe.interview.nbp_web_proxy.shared_kernel.Money;
import eu.venthe.nbp.api.DefaultApi;
import eu.venthe.nbp.model.Format;
import eu.venthe.nbp.model.Table;
import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.stream.Stream;

import static eu.venthe.interview.nbp_web_proxy.shared_kernel.Money.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

class NbpCurrencyExchangeServiceImplTest {
    DefaultApi mockApi;
    CurrencyExchangeService currencyExchangeService;

    @BeforeEach
    void setup() {
        mockApi = Mockito.mock(DefaultApi.class, Mockito.RETURNS_DEEP_STUBS);
        currencyExchangeService = new NbpCurrencyExchangeServiceImpl(mockApi);
    }

    @ParameterizedTest
    @MethodSource
    void onlyPositiveAmountsAreExchanged(BigDecimal value) {
        Assertions.assertThatThrownBy(() ->
                        currencyExchangeService.exchange(of(value, USD), PLN))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Exchange of negative or zero amounts are disallowed");
    }

    static Stream<Arguments> onlyPositiveAmountsAreExchanged() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(BigDecimal.ZERO))
                .add(Arguments.of(BigDecimal.valueOf(-1)))
                .build();
    }

    @Test
    void exchangedAmountMustNotBeNull() {
        Assertions.assertThatThrownBy(() ->
                        currencyExchangeService.exchange(null, PLN))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void targetCurrencyMustNotBeNull() {
        Assertions.assertThatThrownBy(() ->
                        currencyExchangeService.exchange(of(BigDecimal.ONE, USD), null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void nonPlnExchangeNotSupported() {
        Assertions.assertThatThrownBy(() ->
                        currencyExchangeService.exchange(of(BigDecimal.ONE, USD), Currency.getInstance("CHF")))
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessage("At this point we don't support exchanging between two non-PLN currencies. Base currency=USD, Target currency=CHF");
    }

    @Test
    void exchangeServiceFailed() {
        var response = Mockito.mock(ResponseEntity.class);
        Mockito.when(response.getStatusCode())
                .thenReturn(HttpStatusCode.valueOf(400));
        Mockito.when(mockApi.apiExchangeratesRatesTableCodeGetWithResponseSpec(eq(Table.C), any(String.class), eq(Format.JSON)).toEntity(JsonNode.class))
                .thenReturn(response);

        Assertions.assertThatThrownBy(() ->
                        currencyExchangeService.exchange(of(BigDecimal.ONE, USD), PLN))
                .isInstanceOf(CurrencyExchangeFailedException.class);
    }

    @SneakyThrows
    @Test
    void sameCurrencyExchangeNotSupported() {
        Assertions.assertThat(currencyExchangeService.exchange(of(BigDecimal.ONE, PLN), PLN))
                .isEqualTo(of(BigDecimal.ONE, PLN));
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource
    void exchange(Money money, Currency target, BigDecimal rate, Double expectedResult) {
        mockExchangeRate(rate);

        Assertions.assertThat(currencyExchangeService.exchange(money, target))
                .isEqualTo(Money.of(BigDecimal.valueOf(expectedResult), target));
    }

    static Stream<Arguments> exchange() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(pln(1), USD, rate(5), 0.2d))
                .add(Arguments.of(usd(1), PLN, rate(5), 5d))
                .build();
    }

    private static BigDecimal rate(double val) {
        return BigDecimal.valueOf(val);
    }

    private static Money pln(double value) {
        return of(BigDecimal.valueOf(value), PLN);
    }

    private static Money usd(double value) {
        return of(BigDecimal.valueOf(value), USD);
    }

    private void mockExchangeRate(BigDecimal rate) throws JsonProcessingException {
        var formatted = """
                {
                    "rates": [
                        {
                            "ask": %1$s,
                            "bid": %1$s,
                            "effectiveDate": "2024-01-01"
                        }
                    ]
                }
                """.formatted(FORMATTER.format(rate));
        var root = new ObjectMapper().readTree(formatted);

        var response = Mockito.mock(ResponseEntity.class);
        Mockito.when(response.getStatusCode())
                .thenReturn(HttpStatusCode.valueOf(200));
        Mockito.when(response.getBody())
                .thenReturn(root);

        Mockito.when(mockApi.apiExchangeratesRatesTableCodeGetWithResponseSpec(eq(Table.C), any(String.class), eq(Format.JSON)).toEntity(JsonNode.class))
                .thenReturn(response);
    }
}
