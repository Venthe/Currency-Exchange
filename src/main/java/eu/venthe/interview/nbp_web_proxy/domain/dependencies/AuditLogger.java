package eu.venthe.interview.nbp_web_proxy.domain.dependencies;

import eu.venthe.interview.nbp_web_proxy.domain.CurrencyAccountId;
import eu.venthe.interview.nbp_web_proxy.shared_kernel.Money;

public interface AuditLogger {
    void accountOpened(CurrencyAccountId accountId, Money initialOriginalBalance, Money initialForeignBalance);

    void currencyExchanged(CurrencyAccountId accountId, Money initialOriginalBalance, Money updatedOriginalBalance, Money initialForeignBalance, Money updatedForeignBalance);
}
