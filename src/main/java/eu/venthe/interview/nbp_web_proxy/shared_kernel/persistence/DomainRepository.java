package eu.venthe.interview.nbp_web_proxy.shared_kernel.persistence;

import java.util.Optional;

public interface DomainRepository<ID, AGGREGATE extends Aggregate<ID>> {
    ID save(AGGREGATE aggregate);

    boolean exists(ID accountId);

    Optional<AGGREGATE> find(ID accountId);
}
