package eu.venthe.interview.nbp_web_proxy.shared_kernel;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Currency;

@Value
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Money implements Comparable<Money> {
    private static final int SCALE = 7;

    public static final DecimalFormat FORMATTER = new DecimalFormat("#.%s".formatted("#".repeat(SCALE))); // Up to 7 decimal places
    public static final Currency PLN = Currency.getInstance("PLN");
    public static final Currency USD = Currency.getInstance("USD");

    BigDecimal amount;
    Currency currency;

    public static Money of(BigDecimal amount, Currency currency) {
        var newAmount = amount.setScale(SCALE, BigDecimal.ROUND_HALF_UP);

        return new Money(newAmount, currency);
    }

    @Override
    public int compareTo(Money o) {
        if (!currency.equals(o.getCurrency())) {
            return -1;
        }
        return getAmount().compareTo(o.getAmount());
    }
}
