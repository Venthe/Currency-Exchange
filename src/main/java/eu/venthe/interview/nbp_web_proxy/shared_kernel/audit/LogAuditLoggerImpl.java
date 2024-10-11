package eu.venthe.interview.nbp_web_proxy.shared_kernel.audit;

import eu.venthe.interview.nbp_web_proxy.domain.dependencies.AuditLogger;
import eu.venthe.interview.nbp_web_proxy.shared_kernel.ClockService;
import eu.venthe.interview.nbp_web_proxy.shared_kernel.events.DomainEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class LogAuditLoggerImpl implements AuditLogger {
    private final ClockService clockService;

    @Override
    public void log(DomainEvent event) {
        log.info("[AUDIT][{}] {}", clockService.getOffsetNow(), event);
    }
}
