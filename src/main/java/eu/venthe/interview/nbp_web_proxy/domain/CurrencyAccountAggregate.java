package eu.venthe.interview.nbp_web_proxy.domain;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class CurrencyAccountAggregate {
    @Getter
    @EqualsAndHashCode.Include
    private final CurrencyAccountId id;

    public static CurrencyAccountAggregate create() {
        return new CurrencyAccountAggregate(CurrencyAccountId.create());
    }
}
