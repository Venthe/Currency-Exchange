package eu.venthe.interview.nbp_web_proxy.application;

import eu.venthe.interview.nbp_web_proxy.domain.CurrencyAccount;
import eu.venthe.interview.nbp_web_proxy.domain.CurrencyAccountId;
import eu.venthe.interview.nbp_web_proxy.domain.CustomerInformation;
import eu.venthe.interview.nbp_web_proxy.domain.dependencies.CurrencyAccountRepository;
import eu.venthe.interview.nbp_web_proxy.domain.dependencies.CurrencyExchangeFailedException;
import eu.venthe.interview.nbp_web_proxy.domain.dependencies.CurrencyExchangeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class CurrencyAccountCommandService {
    private final CurrencyAccountRepository repository;
    private final CurrencyExchangeService currencyExchangeService;

    public CurrencyAccountId openAccount(CurrencyAccountSpecification specification) {
        log.trace("Opening a currency account. Specification={}", specification);

        var account = CurrencyAccount.open(currencyExchangeService, new CustomerInformation(specification.name(), specification.surname()), specification.initialBalance(), specification.foreignCurrency());
        repository.save(account);

        log.debug("Currency account {} opened.", account.getId());
        return account.getId();
    }

    public void exchangeToForeignCurrency(CurrencyAccountId accountId, BigDecimal amount) {
        log.trace("Requesting money exchange to foreign currency for AccountId={}. Amount={}", accountId, amount);

        var currencyAccount = repository.find(accountId).orElseThrow();

        try {
            currencyAccount.exchangeToForeignCurrency(amount);
            repository.save(currencyAccount);

            log.debug("Money exchange succeeded, AccountId={}", accountId);
        } catch (CurrencyExchangeFailedException e) {
            throw new RuntimeException(e);
        }
    }

    public void exchangeToOriginalCurrency(CurrencyAccountId accountId, BigDecimal amount) {
        log.trace("Requesting money exchange to original currency for AccountId={}. Amount={}", accountId, amount);

        var currencyAccount = repository.find(accountId).orElseThrow();

        try {
            currencyAccount.exchangeToOriginalCurrency(amount);
            repository.save(currencyAccount);

            log.debug("Money exchange succeeded, AccountId={}", accountId);
        } catch (CurrencyExchangeFailedException e) {
            throw new RuntimeException(e);
        }
    }
}
