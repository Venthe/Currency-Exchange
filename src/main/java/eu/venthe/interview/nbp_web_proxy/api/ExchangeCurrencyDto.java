package eu.venthe.interview.nbp_web_proxy.api;

import java.math.BigDecimal;

public record ExchangeCurrencyDto(BigDecimal amount, Direction direction) {

    public enum Direction {
        TO_BASE,
        TO_TARGET;
    }
}
