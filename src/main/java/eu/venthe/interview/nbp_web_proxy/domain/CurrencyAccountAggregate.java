package eu.venthe.interview.nbp_web_proxy.domain;

import eu.venthe.interview.nbp_web_proxy.shared_kernel.persistence.Aggregate;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class CurrencyAccountAggregate implements Aggregate<CurrencyAccountId> {
    @Getter
    @EqualsAndHashCode.Include
    private final CurrencyAccountId id;

    public static CurrencyAccountAggregate open() {
        return new CurrencyAccountAggregate(CurrencyAccountId.create());
    }
}
