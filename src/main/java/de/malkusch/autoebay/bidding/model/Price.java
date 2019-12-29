package de.malkusch.autoebay.bidding.model;

import static java.util.Objects.requireNonNull;

import java.math.BigDecimal;
import java.util.Currency;

public final class Price {

    private final BigDecimal amount;
    private final String currency;

    public Price(BigDecimal amount, String currency) {
        this.amount = requireNonNull(amount);
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price must be positive");
        }

        this.currency = requireNonNull(currency);
        assertISO4217Currency(currency);
    }

    private static void assertISO4217Currency(String currency) {
        Currency.getInstance(currency);
    }

    public boolean isMoreThan(Price price) {
        return amount.compareTo(price.amount) > 0;
    }

    @Override
    public String toString() {
        return String.format("%s %s", currency, amount);
    }

    @Override
    public int hashCode() {
        return currency.hashCode() + amount.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Price)) {
            return false;
        }
        var other = (Price) obj;
        return amount.equals(other.amount) && currency.equals(other.currency);
    }
}
