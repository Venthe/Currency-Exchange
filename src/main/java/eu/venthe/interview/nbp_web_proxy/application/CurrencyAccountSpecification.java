package eu.venthe.interview.nbp_web_proxy.application;

import eu.venthe.interview.nbp_web_proxy.shared_kernel.Money;
import lombok.With;

import java.util.Currency;

@With
public record CurrencyAccountSpecification(String name, String surname, Money initialBalance, Currency foreignCurrency) {
}
