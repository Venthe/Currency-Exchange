package eu.venthe.interview.nbp_web_proxy.shared_kernel.audit;

import eu.venthe.interview.nbp_web_proxy.domain.CurrencyAccountId;
import eu.venthe.interview.nbp_web_proxy.domain.dependencies.AuditLogger;
import eu.venthe.interview.nbp_web_proxy.shared_kernel.ClockService;
import eu.venthe.interview.nbp_web_proxy.shared_kernel.Money;
import eu.venthe.interview.nbp_web_proxy.shared_kernel.audit.audit_event.AccountOpenedEvent;
import eu.venthe.interview.nbp_web_proxy.shared_kernel.audit.audit_event.CurrencyExchangedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class LogAuditLoggerImpl implements AuditLogger {
    private final ClockService clockService;

    @Override
    public void accountOpened(CurrencyAccountId accountId, Money initialOriginalBalance, Money initialForeignBalance) {
        log.info("[AUDIT] {}", new AccountOpenedEvent(clockService.getOffsetNow(), accountId, initialOriginalBalance, initialForeignBalance));
    }

    @Override
    public void currencyExchanged(CurrencyAccountId accountId, Money initialOriginalBalance, Money updatedOriginalBalance, Money initialForeignBalance, Money updatedForeignBalance) {
        log.info("[AUDIT] {}", new CurrencyExchangedEvent(clockService.getOffsetNow(), accountId, initialOriginalBalance, updatedOriginalBalance, initialForeignBalance, updatedForeignBalance));
    }
}
