package eu.venthe.interview.nbp_web_proxy.api;

import eu.venthe.interview.nbp_web_proxy.application.CurrencyAccountCommandService;
import eu.venthe.interview.nbp_web_proxy.configuration.JacksonConfiguration;
import eu.venthe.interview.nbp_web_proxy.domain.CurrencyAccountId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(controllers = CurrencyAccountRestController.class)
@Import(JacksonConfiguration.class)
class CurrencyAccountRestControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    CurrencyAccountCommandService mockCurrencyAccountCommandService;

    @Test
    void openCurrencyAccount() throws Exception {
        var currencyAccountId = CurrencyAccountId.create();
        Mockito.when(mockCurrencyAccountCommandService.openAccount()).thenReturn(currencyAccountId);

        mockMvc.perform(post("/api/currency-account"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\": \"%s\"}".formatted(currencyAccountId.value().toString()), true));
    }

}
