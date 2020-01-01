package de.malkusch.autoebay.bidding.model;

import static de.malkusch.autoebay.shared.infrastructure.event.EventPublisher.publishEvent;
import static java.util.Objects.requireNonNull;

import java.time.Instant;

import de.malkusch.autoebay.bidding.model.BidService.Api.ApiException;
import de.malkusch.autoebay.shared.infrastructure.event.Event;

public final class BidService {

    private final Api api;
    private final MandateRepository mandates;

    public static interface Api {

        Result bid(Mandate mandate, ItemNumber itemNumber, Price price) throws ApiException;

        public static class Result {
            public Instant auctionEnd;
        }

        public static final class ApiException extends Exception {
            private static final long serialVersionUID = 1081955964012891619L;

        }
    }

    public BidService(Api api, MandateRepository mandates) {
        this.api = api;
        this.mandates = mandates;
    }

    public void placeBid(BidGroup group) throws ApiException {
        requireNonNull(group);
        var mandate = mandates.findAndAssertExisting(group.id().userId());

        var maybeNext = group.nextOpenBid();
        if (maybeNext.isEmpty()) {
            return;
        }
        var nextBid = maybeNext.get();

        var result = api.bid(mandate, nextBid.itemNumber(), nextBid.price());
        publishEvent(new BidPlaced(group, result.auctionEnd));
    }

    public static final class BidPlaced implements Event {
        public final String groupId;
        public Instant auctionEnd;

        private BidPlaced(BidGroup group, Instant auctionEnd) {
            groupId = group.id().toString();
            this.auctionEnd = auctionEnd;
        }
    }
}
