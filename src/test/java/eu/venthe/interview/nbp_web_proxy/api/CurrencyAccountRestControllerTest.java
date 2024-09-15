package eu.venthe.interview.nbp_web_proxy.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.venthe.interview.nbp_web_proxy.application.AccountInformationReadModel;
import eu.venthe.interview.nbp_web_proxy.application.CurrencyAccountCommandService;
import eu.venthe.interview.nbp_web_proxy.application.CurrencyAccountQueryService;
import eu.venthe.interview.nbp_web_proxy.application.CurrencyAccountSpecification;
import eu.venthe.interview.nbp_web_proxy.configuration.JacksonConfiguration;
import eu.venthe.interview.nbp_web_proxy.domain.CurrencyAccountId;
import eu.venthe.interview.nbp_web_proxy.shared_kernel.Money;
import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(controllers = CurrencyAccountRestController.class)
@Import(JacksonConfiguration.class)
class CurrencyAccountRestControllerTest {
    private static final Money EXAMPLE_AMOUNT = Money.of(BigDecimal.ZERO, Money.PLN);

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    @MockBean
    CurrencyAccountCommandService mockCurrencyAccountCommandService;

    @MockBean
    CurrencyAccountQueryService currencyAccountQueryService;

    @Test
    void openCurrencyAccount() throws Exception {
        // given
        var currencyAccountId = CurrencyAccountId.create();
        Mockito.doAnswer(specification(currencyAccountId)).when(mockCurrencyAccountCommandService).openAccount(any());
        var body = body(EXAMPLE_AMOUNT);

        // when
        var result = mockMvc.perform(post("/api/currency-account").content(body).contentType(MediaType.APPLICATION_JSON));

        // then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResult(currencyAccountId), true));
    }

    @SneakyThrows
    String body(Money initialBalance) {
        var dto = new CreateAccountDto("Jane", "Doe", initialBalance, Money.USD);
        return objectMapper.writeValueAsString(dto);
    }

    @SneakyThrows
    String expectedResult(CurrencyAccountId id) {
        var dto = new CurrencyAccountOpenedDto(id);
        return objectMapper.writeValueAsString(dto);
    }

    private static Answer specification(CurrencyAccountId currencyAccountId) {
        return invocation -> {
            CurrencyAccountSpecification argument = invocation.getArgument(0);
            if (argument.initialBalance().compareTo(EXAMPLE_AMOUNT) != 0) {
                Assertions.fail("Incorrectly parsed originalBalance");
            }
            return currencyAccountId;
        };
    }

    @Test
    void getAccountInformation() throws Exception {
        // given
        var currencyAccountId = CurrencyAccountId.create();
        var accountInformation = new AccountInformationReadModel(
                currencyAccountId,
                "John",
                "Doe",
                Money.of(BigDecimal.TEN, Money.PLN),
                Money.of(BigDecimal.ZERO, Money.USD)
        );
        Mockito.when(currencyAccountQueryService.getAccountInformation(currencyAccountId))
                .thenReturn(Optional.of(accountInformation));

        // when
        var result = mockMvc.perform(get("/api/currency-account/{accountId}", currencyAccountId.value().toString()));

        // then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(new AccountInformationDto(accountInformation)), true));
    }

    @Nested
    class Exchange {
        @Test
        void toOriginal() throws Exception {
            var currencyAccountId = CurrencyAccountId.create();
            var exchangeCurrencyDto = new ExchangeCurrencyDto(BigDecimal.ONE, ExchangeCurrencyDto.Direction.TO_ORIGINAL);

            var result = mockMvc.perform(post(
                    "/api/currency-account/{accountId}/exchange",
                    currencyAccountId.value().toString()).content(objectMapper.writeValueAsString(exchangeCurrencyDto)).contentType(MediaType.APPLICATION_JSON)
            );

            result.andDo(print())
                    .andExpect(status().isOk());

            Mockito.verify(mockCurrencyAccountCommandService, Mockito.times(1)).exchangeToOriginalCurrency(currencyAccountId, BigDecimal.ONE);
        }

        @Test
        void toForeign() throws Exception {
            var currencyAccountId = CurrencyAccountId.create();
            var exchangeCurrencyDto = new ExchangeCurrencyDto(BigDecimal.ONE, ExchangeCurrencyDto.Direction.TO_FOREIGN);

            var result = mockMvc.perform(post(
                    "/api/currency-account/{accountId}/exchange",
                    currencyAccountId.value().toString()).content(objectMapper.writeValueAsString(exchangeCurrencyDto)).contentType(MediaType.APPLICATION_JSON)
            );

            result.andDo(print())
                    .andExpect(status().isOk());

            Mockito.verify(mockCurrencyAccountCommandService, Mockito.times(1)).exchangeToForeignCurrency(currencyAccountId, BigDecimal.ONE);
        }
    }
}
