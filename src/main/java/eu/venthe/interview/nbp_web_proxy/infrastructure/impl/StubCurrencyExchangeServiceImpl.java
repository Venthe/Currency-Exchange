package eu.venthe.interview.nbp_web_proxy.infrastructure.impl;

import eu.venthe.interview.nbp_web_proxy.domain.dependencies.CurrencyExchangeFailedException;
import eu.venthe.interview.nbp_web_proxy.domain.dependencies.CurrencyExchangeService;
import eu.venthe.interview.nbp_web_proxy.shared_kernel.Money;

import java.util.Currency;

public class StubCurrencyExchangeServiceImpl implements CurrencyExchangeService {
    @Override
    public Money exchange(Money money, Currency targetCurrency) throws CurrencyExchangeFailedException {
        throw new UnsupportedOperationException();
    }
}
