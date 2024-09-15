package eu.venthe.interview.nbp_web_proxy.infrastructure.impl;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.venthe.interview.nbp_web_proxy.domain.dependencies.CurrencyExchangeFailedException;
import eu.venthe.interview.nbp_web_proxy.domain.dependencies.CurrencyExchangeService;
import eu.venthe.interview.nbp_web_proxy.shared_kernel.CacheManager;
import eu.venthe.interview.nbp_web_proxy.shared_kernel.ClockService;
import eu.venthe.interview.nbp_web_proxy.shared_kernel.Money;
import eu.venthe.nbp.api.DefaultApi;
import eu.venthe.nbp.model.Format;
import eu.venthe.nbp.model.Table;
import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Currency;
import java.util.stream.Stream;

import static eu.venthe.interview.nbp_web_proxy.shared_kernel.Money.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

class NbpCurrencyExchangeServiceImplTest {
    static final ZonedDateTime NOW = ZonedDateTime.of(LocalDateTime.of(2023, Month.APRIL, 1, 12, 0), ZoneId.of("UTC"));

    ClockService mockClockService;
    CacheManager<NbpRate> cacheManager;
    DefaultApi mockApi;
    CurrencyExchangeService currencyExchangeService;

    @BeforeEach
    void setup() {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        loggerContext.getLogger("eu.venthe").setLevel(Level.TRACE);

        mockClockService = Mockito.mock(ClockService.class);
        Mockito.when(mockClockService.getZonedNow()).thenReturn(NOW);
        cacheManager = new InMemoryCacheManager<>();
        mockApi = Mockito.mock(DefaultApi.class, Mockito.RETURNS_DEEP_STUBS);
        currencyExchangeService = new NbpCurrencyExchangeServiceImpl(cacheManager, mockClockService, mockApi);
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
        mockExchangeRate(rate, LocalDate.MAX);

        Assertions.assertThat(currencyExchangeService.exchange(money, target))
                .isEqualTo(Money.of(BigDecimal.valueOf(expectedResult), target));
    }

    static Stream<Arguments> exchange() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(pln(1), USD, rate(5), 0.2d))
                .add(Arguments.of(usd(1), PLN, rate(5), 5d))
                .build();
    }

    @Nested
    class Cache {

        @SneakyThrows
        @Test
        void testValueIsRetrievedFromCache() {
            Mockito.when(mockClockService.getZonedNow()).thenReturn(NOW);
            mockExchangeRate(rate(10), NOW.toLocalDate());

            for (int i = 0; i < 100; i++) {
                currencyExchangeService.exchange(pln(10), USD);
            }

            // Fist one is from the stubbing
            Mockito.verify(mockApi, Mockito.times(2))
                    .apiExchangeratesRatesTableCodeGetWithResponseSpec(any(), any(), any());
        }

        @SneakyThrows
        @Test
        void valueIsUpdatedForTheNextDay() {
            // Monday
            var now = ZonedDateTime.of(LocalDateTime.of(2023, Month.APRIL, 3, 12, 0), ZoneId.of("UTC"));

            Mockito.when(mockClockService.getZonedNow()).thenReturn(now);
            mockExchangeRate(rate(10), now.toLocalDate());

            for (int i = 0; i < 100; i++) {
                currencyExchangeService.exchange(pln(10), USD);
            }

            var newDate = now.plusDays(1);
            Mockito.when(mockClockService.getZonedNow()).thenReturn(newDate);
            mockExchangeRate(rate(10), newDate.toLocalDate());

            for (int i = 0; i < 100; i++) {
                currencyExchangeService.exchange(pln(10), USD);
            }

            // 2 are from the stubbing
            Mockito.verify(mockApi, Mockito.times(4))
                    .apiExchangeratesRatesTableCodeGetWithResponseSpec(any(), any(), any());
        }

        @SneakyThrows
        @Test
        void valueIsRetrievedForTheWeekend() {
            // Saturday
            var now = ZonedDateTime.of(LocalDateTime.of(2023, Month.APRIL, 1, 12, 0), ZoneId.of("UTC"));

            Mockito.when(mockClockService.getZonedNow()).thenReturn(now);
            mockExchangeRate(rate(10), now.toLocalDate());

            for (int i = 0; i < 100; i++) {
                currencyExchangeService.exchange(pln(10), USD);
            }

            var newDate = now.plusDays(1);
            Mockito.when(mockClockService.getZonedNow()).thenReturn(newDate);
            mockExchangeRate(rate(10), newDate.toLocalDate());

            for (int i = 0; i < 100; i++) {
                currencyExchangeService.exchange(pln(10), USD);
            }

            // 2 are from the stubbing
            Mockito.verify(mockApi, Mockito.times(3))
                    .apiExchangeratesRatesTableCodeGetWithResponseSpec(any(), any(), any());
        }
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

    private void mockExchangeRate(BigDecimal rate, LocalDate effectiveDate) throws JsonProcessingException {
        var formatted = """
                {
                    "rates": [
                        {
                            "ask": %1$s,
                            "bid": %1$s,
                            "effectiveDate": "%2$s"
                        }
                    ]
                }
                """.formatted(FORMATTER.format(rate), DateTimeFormatter.ofPattern("yyyy-MM-dd").format(effectiveDate));
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
