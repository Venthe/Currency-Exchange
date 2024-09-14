package eu.venthe.interview.nbp_web_proxy.application;

import eu.venthe.interview.nbp_web_proxy.shared_kernel.Money;

import java.util.Currency;

public record CurrencyAccountSpecification(String name, String surname, Money balance, Currency exchangeCurrency) {
}
