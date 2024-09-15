package eu.venthe.interview.nbp_web_proxy.api;

import eu.venthe.interview.nbp_web_proxy.application.CurrencyAccountCommandService;
import eu.venthe.interview.nbp_web_proxy.application.CurrencyAccountQueryService;
import eu.venthe.interview.nbp_web_proxy.application.CurrencyAccountSpecification;
import eu.venthe.interview.nbp_web_proxy.domain.CurrencyAccountId;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/currency-account")
public class CurrencyAccountRestController {
    private final CurrencyAccountCommandService currencyAccountCommandService;
    private final CurrencyAccountQueryService currencyAccountQueryService;

    @PostMapping("")
    private CurrencyAccountOpenedDto openAccount(@RequestBody CreateAccountDto accountDto) {
        var specification = new CurrencyAccountSpecification(accountDto.name(), accountDto.surname(), accountDto.initialBalance(), accountDto.foreignCurrency());
        var openedAccountId = currencyAccountCommandService.openAccount(specification);
        return new CurrencyAccountOpenedDto(openedAccountId);
    }

    @PostMapping("/{accountId}/exchange")
    private void exchangeCurrency(@PathVariable CurrencyAccountId accountId, @RequestBody ExchangeCurrencyDto exchangeCurrencyDto) {
        switch (exchangeCurrencyDto.direction()) {
            case TO_ORIGINAL ->
                    currencyAccountCommandService.exchangeToOriginalCurrency(accountId, exchangeCurrencyDto.amount());
            case TO_FOREIGN ->
                    currencyAccountCommandService.exchangeToForeignCurrency(accountId, exchangeCurrencyDto.amount());
            default -> throw new UnsupportedOperationException();
        }
    }

    @GetMapping("/{accountId}")
    private Optional<AccountInformationDto> getAccountInformation(@PathVariable CurrencyAccountId accountId) {
        var accountInformation = currencyAccountQueryService.getAccountInformation(accountId);
        return accountInformation.map(AccountInformationDto::new);
    }
}
