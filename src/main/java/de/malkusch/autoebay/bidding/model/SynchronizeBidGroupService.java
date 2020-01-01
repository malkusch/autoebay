package de.malkusch.autoebay.bidding.model;

import static de.malkusch.autoebay.bidding.model.Bid.State.OPEN;
import static java.util.Objects.requireNonNull;

import java.time.Clock;

public final class SynchronizeBidGroupService {

    private final Api api;
    private final MandateRepository mandates;
    private final Clock clock;

    public static interface Api {

        boolean isWon(Mandate mandate, ItemNumber itemNumber);

    }

    public SynchronizeBidGroupService(Api api, Clock clock, MandateRepository mandates) {
        this.api = api;
        this.clock = clock;
        this.mandates = mandates;
    }

    public void synchronize(BidGroup group) {
        requireNonNull(group);
        var mandate = mandates.findAndAssertExisting(group.id().userId());
        group.bids(OPEN).forEach(it -> synchronizeOpenBid(mandate, it));
    }

    private void synchronizeOpenBid(Mandate mandate, Bid bid) {
        if (bid.state != OPEN) {
            return;
        }

        if (api.isWon(mandate, bid.itemNumber())) {
            bid.win();
        } else if (bid.bidTime().isBefore(clock.instant())) {
            bid.notwon();
        }
    }
}
