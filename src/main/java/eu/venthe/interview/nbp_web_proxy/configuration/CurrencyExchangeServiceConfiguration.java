package eu.venthe.interview.nbp_web_proxy.configuration;

import eu.venthe.interview.nbp_web_proxy.domain.dependencies.CurrencyExchangeService;
import eu.venthe.interview.nbp_web_proxy.infrastructure.impl.StubCurrencyExchangeServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CurrencyExchangeServiceConfiguration {
    @Bean
    CurrencyExchangeService currencyExchangeService() {
        return new StubCurrencyExchangeServiceImpl();
    }
}
