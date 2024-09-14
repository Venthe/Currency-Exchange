package eu.venthe.interview.nbp_web_proxy.api;

import eu.venthe.interview.nbp_web_proxy.shared_kernel.Money;

import java.util.Currency;

public record CreateAccountDto(String name, String surname, Money initialBalance, Currency exchangeCurrency) {
}
