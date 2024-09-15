package eu.venthe.interview.nbp_web_proxy.domain.dependencies;

import eu.venthe.interview.nbp_web_proxy.domain.CurrencyAccount;
import eu.venthe.interview.nbp_web_proxy.domain.CurrencyAccountId;
import eu.venthe.interview.nbp_web_proxy.shared_kernel.persistence.DomainRepository;

public interface CurrencyAccountRepository extends DomainRepository<CurrencyAccountId, CurrencyAccount> {
}
