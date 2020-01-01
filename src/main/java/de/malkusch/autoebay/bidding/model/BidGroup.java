package de.malkusch.autoebay.bidding.model;

import static de.malkusch.autoebay.bidding.model.Bid.State.OPEN;
import static de.malkusch.autoebay.bidding.model.Bid.State.OUTBID;
import static de.malkusch.autoebay.bidding.model.Bid.State.WON;
import static de.malkusch.autoebay.shared.infrastructure.event.EventPublisher.publishEvent;
import static java.util.Comparator.comparing;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.empty;
import static java.util.UUID.randomUUID;
import static java.util.stream.Collectors.toSet;

import java.time.Duration;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import de.malkusch.autoebay.bidding.model.Bid.State;
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

    public void registerBid(Auction auction, Price price, Duration biddingWindow) {
        requireNonNull(auction);
        requireNonNull(price);
        requireNonNull(biddingWindow);

        if (biddingWindow.isNegative()) {
            throw new IllegalArgumentException("Bidding window must not be negative");
        }

        var bidTime = auction.end().minus(biddingWindow);
        var state = price.isMoreThan(auction.price()) ? OPEN : OUTBID;

        var bid = new Bid(auction.itemNumber(), price, bidTime, state);
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

    Set<Bid> won() {
        return bids(WON).collect(toSet());
    }

    private Count remaining() {
        var won = new Count(won().size());
        return count.minus(won);
    }

    public boolean isCompleted() {
        return remaining().isZero();
    }

    public Optional<Bid> nextOpenBid() {
        if (isCompleted()) {
            return empty();
        }
        return bids(OPEN).min(comparing(Bid::bidTime));
    }

    private Stream<Bid> bids(State state) {
        return bids.stream().filter(it -> it.state == state);
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
