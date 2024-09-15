package eu.venthe.interview.nbp_web_proxy.shared_kernel.audit.audit_event;

import eu.venthe.interview.nbp_web_proxy.domain.CurrencyAccountId;
import eu.venthe.interview.nbp_web_proxy.shared_kernel.Money;

import java.time.OffsetDateTime;

public record CurrencyExchangedEvent(OffsetDateTime timestamp, CurrencyAccountId accountId, Money initialOriginalBalance, Money updatedOriginalBalance, Money initialForeignBalance, Money updatedForeignBalance) {
}
