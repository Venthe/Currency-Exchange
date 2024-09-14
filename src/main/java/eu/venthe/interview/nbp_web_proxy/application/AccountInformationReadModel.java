package eu.venthe.interview.nbp_web_proxy.application;

import eu.venthe.interview.nbp_web_proxy.domain.CurrencyAccountId;
import eu.venthe.interview.nbp_web_proxy.shared_kernel.Money;

public record AccountInformationReadModel(CurrencyAccountId id, String ownerName, String ownerSurname, Money balance) {
}
