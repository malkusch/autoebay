package de.malkusch.autoebay.bidding.model;

import static java.util.Objects.requireNonNull;

import de.malkusch.autoebay.bidding.model.BidService.Api.ApiException;

public final class BidService {

    private final Api api;

    public static interface Api {

        BidResult bid(Mandate mandate, ItemNumber itemNumber, Price price) throws ApiException;

        public static enum BidResult {
            WON, NOTWON
        }

        public static final class ApiException extends Exception {
            private static final long serialVersionUID = 1081955964012891619L;

        }
    }

    BidService(Api api) {
        this.api = api;
    }

    public void bid(Mandate mandate, BidGroup group) throws ApiException {
        requireNonNull(group);
        group.id().assertValidMandate(mandate);

        var maybeNext = group.nextOpenBid();
        if (maybeNext.isEmpty()) {
            return;
        }
        var nextBid = maybeNext.get();

        var result = api.bid(mandate, nextBid.itemNumber(), nextBid.price());
        switch (result) {
        case WON:
            nextBid.win();
            break;

        case NOTWON:
            nextBid.notwon();
            break;

        default:
            throw new IllegalStateException("Unexpected result " + result);
        }
    }
}
