package de.malkusch.autoebay.bidding.model;

import static de.malkusch.autoebay.shared.infrastructure.event.EventPublisher.publishEvent;
import static java.util.Objects.requireNonNull;
import static java.util.UUID.randomUUID;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

import de.malkusch.autoebay.shared.infrastructure.event.Event;

public final class BidGroup {

    private final GroupId id;
    private final GroupName name;
    private final Count count;
    private final Set<Bid> bids;

    public BidGroup(Mandate mandate, GroupName name, Count count) {
        this(newId(mandate), name, count, new HashSet<>());
    }

    private static GroupId newId(Mandate mandate) {
        return new GroupId(mandate.userId(), randomUUID().toString());
    }

    BidGroup(GroupId id, GroupName name, Count count, Set<Bid> bids) {
        this.id = requireNonNull(id);
        this.name = requireNonNull(name);
        this.bids = requireNonNull(bids);
        this.count = requireNonNull(count);
        count.assertPositive();
    }

    public GroupId id() {
        return id;
    }

    public void registerBid(Auction auction, Price price, Duration biddingWindow) {
        requireNonNull(auction);
        requireNonNull(price);
        requireNonNull(biddingWindow);

        if (biddingWindow.isNegative()) {
            throw new IllegalArgumentException("Bidding window must not be negative");
        }

        var bidTime = auction.end().minus(biddingWindow);

        var bid = new Bid(auction.itemNumber(), price, bidTime);
        if (bids.contains(bid)) {
            throw new IllegalArgumentException(
                    "Bid for item " + auction.itemNumber() + " exists already in group " + id);
        }

        bids.add(bid);
        publishEvent(new BidRegistered(id));
    }

    public static final class BidRegistered implements Event {

        public final String groupId;

        private BidRegistered(GroupId id) {
            this.groupId = id.toString();
        }
    }

    @Override
    public String toString() {
        return name.toString();
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof BidGroup)) {
            return false;
        }
        var other = (BidGroup) obj;
        return id.equals(other.id);
    }
}
