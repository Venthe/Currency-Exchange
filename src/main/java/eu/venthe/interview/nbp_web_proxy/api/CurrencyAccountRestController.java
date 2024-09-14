package eu.venthe.interview.nbp_web_proxy.api;

import eu.venthe.interview.nbp_web_proxy.application.CurrencyAccountCommandService;
import eu.venthe.interview.nbp_web_proxy.application.CurrencyAccountSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/currency-account")
public class CurrencyAccountRestController {
    private final CurrencyAccountCommandService currencyAccountCommandService;

    @PostMapping("")
    private CurrencyAccountOpenedDto openAccount(@RequestBody CreateAccountDto accountDto) {
        var specification = new CurrencyAccountSpecification(accountDto.initialBalance(), accountDto.name(), accountDto.surname());
        var openedAccountId = currencyAccountCommandService.openAccount(specification);
        return new CurrencyAccountOpenedDto(openedAccountId);
    }
}
