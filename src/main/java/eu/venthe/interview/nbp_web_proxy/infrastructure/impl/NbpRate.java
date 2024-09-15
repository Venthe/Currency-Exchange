package eu.venthe.interview.nbp_web_proxy.infrastructure.impl;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Represents a person with a name and an age.
 *
 * @param ask           The price a seller is willing to accept for a currency.
 * @param bid           The price a buyer is willing to pay for a currency.
 * @param effectiveDate The date on which the rate is taken into account.
 */
public record NbpRate(BigDecimal ask,
               BigDecimal bid,
               LocalDate effectiveDate) {

}
