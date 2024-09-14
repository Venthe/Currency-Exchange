package eu.venthe.interview.nbp_web_proxy.api;

import eu.venthe.interview.nbp_web_proxy.shared_kernel.Money;

public record CreateAccountDto(Money initialBalance, String name, String surname) {
}
