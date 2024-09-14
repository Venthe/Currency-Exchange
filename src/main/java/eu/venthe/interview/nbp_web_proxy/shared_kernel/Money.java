package eu.venthe.interview.nbp_web_proxy.shared_kernel;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Currency;

@Value
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Money implements Comparable<Money> {
    private static final int SCALE = 7;
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

    public static final DecimalFormat FORMATTER = new DecimalFormat("#.%s".formatted("#".repeat(SCALE))); // Up to 7 decimal places
    public static final Currency PLN = Currency.getInstance("PLN");
    public static final Currency USD = Currency.getInstance("USD");

    BigDecimal amount;
    Currency currency;

    public static Money of(BigDecimal amount, Currency currency) {
        var newAmount = amount.setScale(SCALE, ROUNDING_MODE).stripTrailingZeros();

        return new Money(newAmount, currency);
    }

    public BigDecimal getAmount() {
        return getScaledAmount(amount).stripTrailingZeros();
    }

    public Money multiply(BigDecimal amount) {
        return of(getScaledAmount(getAmount()).multiply(amount), getCurrency());
    }

    public Money divide(BigDecimal amount) {
        return of(getScaledAmount(getAmount()).divide(amount, ROUNDING_MODE), getCurrency());
    }

    public Money subtract(BigDecimal amount) {
        return of(getScaledAmount(getAmount()).subtract(amount), getCurrency());
    }

    public Money add(Money amount) {
        return of(getScaledAmount(getAmount()).add(amount.getAmount()), getCurrency());
    }

    private BigDecimal getScaledAmount(BigDecimal amount) {
        return amount.setScale(SCALE, ROUNDING_MODE);
    }

    @Override
    public int compareTo(Money o) {
        if (!currency.equals(o.getCurrency())) {
            return -1;
        }
        return getAmount().compareTo(o.getAmount());
    }
}
