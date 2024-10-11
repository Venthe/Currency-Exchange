package eu.venthe.interview.nbp_web_proxy.domain.dependencies;

import eu.venthe.interview.nbp_web_proxy.shared_kernel.events.DomainEvent;

public interface AuditLogger {
    void log(DomainEvent event);
}
