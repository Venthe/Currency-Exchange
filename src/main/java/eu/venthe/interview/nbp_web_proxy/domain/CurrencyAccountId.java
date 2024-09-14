package eu.venthe.interview.nbp_web_proxy.domain;

import java.util.UUID;

/**
 * Globally unique ID
 *
 * @param value
 */
public record CurrencyAccountId(UUID value) {
    public static CurrencyAccountId create() {
        return new CurrencyAccountId(UUID.randomUUID());
    }
}
