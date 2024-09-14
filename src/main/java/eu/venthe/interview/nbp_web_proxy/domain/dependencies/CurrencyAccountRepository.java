package eu.venthe.interview.nbp_web_proxy.domain.dependencies;

import eu.venthe.interview.nbp_web_proxy.domain.CurrencyAccountAggregate;
import eu.venthe.interview.nbp_web_proxy.domain.CurrencyAccountId;
import eu.venthe.interview.nbp_web_proxy.shared_kernel.persistence.DomainRepository;

public interface CurrencyAccountRepository extends DomainRepository<CurrencyAccountId, CurrencyAccountAggregate> {
}
