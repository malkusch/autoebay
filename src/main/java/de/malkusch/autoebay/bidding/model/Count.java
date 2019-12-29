package de.malkusch.autoebay.bidding.model;

public final class Count {

    final int value;

    public Count(int count) {
        if (count < 0) {
            throw new IllegalArgumentException("Count must not be negative");
        }
        value = count;
    }

    void assertPositive() {
        if (value <= 0) {
            throw new IllegalArgumentException("Count must be positive");
        }
    }

    public Count minus(Count count) {
        return new Count(value - count.value);
    }

    public boolean isZero() {
        return value == 0;
    }

    @Override
    public String toString() {
        return Integer.toString(value);
    }

    @Override
    public int hashCode() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Count)) {
            return false;
        }
        var other = (Count) obj;
        return value == other.value;
    }
}
