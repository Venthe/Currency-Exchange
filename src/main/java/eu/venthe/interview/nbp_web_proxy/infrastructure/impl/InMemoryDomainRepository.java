package eu.venthe.interview.nbp_web_proxy.infrastructure.impl;

import eu.venthe.interview.nbp_web_proxy.shared_kernel.persistence.Aggregate;
import eu.venthe.interview.nbp_web_proxy.shared_kernel.persistence.DomainRepository;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
public class InMemoryDomainRepository<ID, AGGREGATE extends Aggregate<ID>> implements DomainRepository<ID, AGGREGATE> {
    private final Map<ID, AGGREGATE> repository = new HashMap<>();

    @Override
    public ID save(AGGREGATE aggregate) {
        repository.put(aggregate.getId(), aggregate);
        return aggregate.getId();
    }

    @Override
    public boolean exists(ID accountId) {
        return repository.get(accountId) != null;
    }

    @Override
    public Optional<AGGREGATE> find(ID accountId) {
        return Optional.ofNullable(repository.get(accountId));
    }
}
