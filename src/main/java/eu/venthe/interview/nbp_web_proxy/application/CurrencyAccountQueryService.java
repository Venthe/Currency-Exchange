package eu.venthe.interview.nbp_web_proxy.application;

import eu.venthe.interview.nbp_web_proxy.domain.CurrencyAccountId;
import eu.venthe.interview.nbp_web_proxy.domain.dependencies.CurrencyAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CurrencyAccountQueryService {
    public final CurrencyAccountRepository repository;

    public boolean doesAccountExist(CurrencyAccountId accountId) {
        return repository.exists(accountId);
    }
}
