package eu.venthe.interview.nbp_web_proxy.domain.dependencies;

import eu.venthe.interview.nbp_web_proxy.shared_kernel.Money;

import java.util.Currency;

public interface CurrencyExchangeService {
    /**
     * The process of exchanging one currency for another based on the exchange rate.
     */
    Money exchange(Money money, Currency targetCurrency) throws CurrencyExchangeFailedException;
}
