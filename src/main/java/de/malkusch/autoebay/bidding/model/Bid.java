package de.malkusch.autoebay.bidding.model;

import static java.util.Objects.requireNonNull;

import java.time.Instant;

public final class Bid {

    private final ItemNumber itemNumber;
    private final Price price;
    private final Instant bidTime;
    State state;

    public static enum State {
        OPEN, NOTWON, WON
    }

    Bid(ItemNumber itemNumber, Price price, Instant bidTime, State state) {
        this.itemNumber = requireNonNull(itemNumber);
        this.price = requireNonNull(price);
        this.bidTime = requireNonNull(bidTime);
        this.state = requireNonNull(state);
    }

    ItemNumber itemNumber() {
        return itemNumber;
    }

    Instant bidTime() {
        return bidTime;
    }

    void win() {
        state = State.WON;
    }

    void notwon() {
        state = State.NOTWON;
    }

    @Override
    public String toString() {
        return itemNumber.toString();
    }

    @Override
    public int hashCode() {
        return itemNumber.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Bid)) {
            return false;
        }
        var other = (Bid) obj;
        return itemNumber.equals(other.itemNumber);
    }

    public Price price() {
        return price;
    }
}
