package eu.venthe.interview.nbp_web_proxy.api;

import eu.venthe.interview.nbp_web_proxy.application.AccountInformationReadModel;
import eu.venthe.interview.nbp_web_proxy.domain.CurrencyAccountId;

public record AccountInformationDto(CurrencyAccountId id, Owner owner, PlainTextMoney balance) {
    public AccountInformationDto(AccountInformationReadModel readModel) {
        this(readModel.id(), new Owner(readModel.ownerName(), readModel.ownerSurname()), new PlainTextMoney(readModel.balance()));
    }

    public record Owner(String name, String surname) {
    }
}
