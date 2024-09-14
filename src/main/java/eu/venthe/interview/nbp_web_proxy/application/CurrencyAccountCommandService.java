package eu.venthe.interview.nbp_web_proxy.application;

import eu.venthe.interview.nbp_web_proxy.domain.CurrencyAccountAggregate;
import eu.venthe.interview.nbp_web_proxy.domain.CurrencyAccountId;
import eu.venthe.interview.nbp_web_proxy.domain.dependencies.CurrencyAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CurrencyAccountCommandService {
    private final CurrencyAccountRepository repository;

    public CurrencyAccountId openAccount() {
        log.trace("Opening a currency account.");

        var account = CurrencyAccountAggregate.create();
        repository.save(account);

        log.debug("Currency account {} opened.", account.getId());
        return account.getId();
    }
}
