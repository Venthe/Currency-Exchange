package eu.venthe.interview.nbp_web_proxy.domain.event;

import eu.venthe.interview.nbp_web_proxy.domain.CurrencyAccountId;
import eu.venthe.interview.nbp_web_proxy.shared_kernel.Money;
import eu.venthe.interview.nbp_web_proxy.shared_kernel.events.DomainEvent;

public record CurrencyExchangedEvent(
        CurrencyAccountId accountId,
        Money initialOriginalBalance,
        Money updatedOriginalBalance,
        Money initialForeignBalance,
        Money updatedForeignBalance
) implements DomainEvent {
}
