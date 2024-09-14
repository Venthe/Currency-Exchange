package eu.venthe.interview.nbp_web_proxy.configuration;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import eu.venthe.interview.nbp_web_proxy.api.converter.CurrencyAccountIdConverter;
import eu.venthe.interview.nbp_web_proxy.api.converter.MoneyConverter;
import eu.venthe.interview.nbp_web_proxy.api.converter.PlainTextMoneyConverter;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfiguration {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jacksonCustomizer() {
        return builder -> {
            builder.propertyNamingStrategy(PropertyNamingStrategies.KEBAB_CASE);
            builder.modulesToInstall(CurrencyAccountIdConverter.MODULE, MoneyConverter.MODULE, PlainTextMoneyConverter.MODULE);
        };
    }
}
