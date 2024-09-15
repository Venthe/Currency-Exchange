package eu.venthe.interview.nbp_web_proxy.configuration;

import eu.venthe.interview.nbp_web_proxy.domain.dependencies.CurrencyExchangeService;
import eu.venthe.interview.nbp_web_proxy.infrastructure.impl.NbpCurrencyExchangeServiceImpl;
import eu.venthe.interview.nbp_web_proxy.infrastructure.impl.StubCurrencyExchangeServiceImpl;
import eu.venthe.nbp.api.DefaultApi;
import eu.venthe.nbp.invoker.ApiClient;
import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Data
@Configuration
@ConfigurationProperties(prefix = "currency-exchange")
public class CurrencyExchangeServiceConfiguration {
    private String url;
    private Boolean stub;

    @Bean
    @ConditionalOnProperty(name = "currency-exchange.stub", havingValue = "true")
    CurrencyExchangeService stubCurrencyExchangeService() {
        return new StubCurrencyExchangeServiceImpl();
    }

    @Bean
    @ConditionalOnProperty(name = "currency-exchange.stub", havingValue = "false", matchIfMissing = true)
    public DefaultApi nbpApiClient(CurrencyExchangeServiceConfiguration configuration) {
        return new DefaultApi(new ApiClient(RestClient.create(configuration.url)));
    }

    @Bean
    @ConditionalOnProperty(name = "currency-exchange.stub", havingValue = "false", matchIfMissing = true)
    public CurrencyExchangeService currencyExchangeRealService(DefaultApi defaultApi) {
        return new NbpCurrencyExchangeServiceImpl(defaultApi);
    }
}
