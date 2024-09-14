package eu.venthe.interview.nbp_web_proxy.shared_kernel.persistence;

public interface DomainRepository<ID, AGGREGATE extends Aggregate<ID>> {
    ID save(AGGREGATE aggregate);

    boolean exists(ID accountId);
}
