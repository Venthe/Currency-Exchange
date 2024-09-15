package eu.venthe.interview.nbp_web_proxy.configuration;

import eu.venthe.interview.nbp_web_proxy.domain.CurrencyAccount;
import eu.venthe.interview.nbp_web_proxy.domain.CurrencyAccountId;
import eu.venthe.interview.nbp_web_proxy.domain.dependencies.CurrencyAccountRepository;
import eu.venthe.interview.nbp_web_proxy.infrastructure.impl.InMemoryDomainRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

@Configuration
public class CurrencyAccountRepositoryConfiguration {
    @Bean
    CurrencyAccountRepository currencyAccountRepository() {
        return new CurrencyAccountRepository() {
            private final InMemoryDomainRepository<CurrencyAccountId, CurrencyAccount> repository = new InMemoryDomainRepository<>();

            @Override
            public CurrencyAccountId save(CurrencyAccount aggregate) {
                return repository.save(aggregate);
            }

            @Override
            public boolean exists(CurrencyAccountId accountId) {
                return repository.exists(accountId);
            }

            @Override
            public Optional<CurrencyAccount> find(CurrencyAccountId accountId) {
                return repository.find(accountId);
            }
        };
    }
}
