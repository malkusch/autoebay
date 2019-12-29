package de.malkusch.autoebay.bidding.model;

import static java.util.Objects.requireNonNull;

public final class ItemNumber {

    private final String number;

    public ItemNumber(String number) {
        this.number = requireNonNull(number);
        if (number.isBlank()) {
            throw new IllegalArgumentException("Item number must not be empty");
        }
    }

    @Override
    public String toString() {
        return number;
    }

    @Override
    public int hashCode() {
        return number.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ItemNumber)) {
            return false;
        }
        var other = (ItemNumber) obj;
        return number.equals(other.number);
    }
}
