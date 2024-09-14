package eu.venthe.interview.nbp_web_proxy.application;

import eu.venthe.interview.nbp_web_proxy.domain.CurrencyAccountId;
import eu.venthe.interview.nbp_web_proxy.domain.dependencies.CurrencyAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CurrencyAccountQueryService {
    public final CurrencyAccountRepository repository;

    public boolean doesAccountExist(CurrencyAccountId accountId) {
        return repository.exists(accountId);
    }

    public Optional<AccountInformationReadModel> getAccountInformation(CurrencyAccountId accountId) {
        // Consider replacing with a visitor
        return repository.find(accountId)
                .map(e -> new AccountInformationReadModel(e.getId(), e.getName(), e.getSurname(), e.getOriginalBalance(), e.getExchangedBalance()));
    }
}
