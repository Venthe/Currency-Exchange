package eu.venthe.interview.nbp_web_proxy.api;

import eu.venthe.interview.nbp_web_proxy.application.CurrencyAccountCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/currency-account")
public class CurrencyAccountRestController {
    private final CurrencyAccountCommandService currencyAccountCommandService;

    @PostMapping("")
    private CurrencyAccountOpenedDto openAccount() {
        return new CurrencyAccountOpenedDto(currencyAccountCommandService.openAccount());
    }
}
