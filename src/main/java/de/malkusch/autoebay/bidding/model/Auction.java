package de.malkusch.autoebay.bidding.model;

import static java.util.Objects.requireNonNull;

import java.time.Instant;

public final class Auction {

    public Auction(ItemNumber itemNumber, Price price, Instant end) {
        this.itemNumber = requireNonNull(itemNumber);
        this.price = requireNonNull(price);
        this.end = requireNonNull(end);
    }

    private final Instant end;

    public Instant end() {
        return end;
    }

    private final ItemNumber itemNumber;

    public ItemNumber itemNumber() {
        return itemNumber;
    }

    private final Price price;

    public Price price() {
        return price;
    }
}
