package eu.venthe.interview.nbp_web_proxy.domain.event;

import eu.venthe.interview.nbp_web_proxy.domain.CurrencyAccountId;
import eu.venthe.interview.nbp_web_proxy.shared_kernel.Money;
import eu.venthe.interview.nbp_web_proxy.shared_kernel.events.DomainEvent;

public record AccountOpenedEvent(CurrencyAccountId accountId,
                                 Money initialOriginalBalance,
                                 Money initialForeignBalance) implements DomainEvent {
}
