package de.malkusch.autoebay.bidding.application.bid;

import static de.malkusch.autoebay.bidding.application.ApplicationExceptions.notFound;
import static de.malkusch.autoebay.shared.infrastructure.persistance.TransactionService.IsolationLevel.SERIALIZABLE;

import de.malkusch.autoebay.bidding.model.BidGroupRepository;
import de.malkusch.autoebay.bidding.model.BidService;
import de.malkusch.autoebay.bidding.model.BidService.Api.ApiException;
import de.malkusch.autoebay.bidding.model.GroupId;
import de.malkusch.autoebay.shared.infrastructure.persistance.TransactionService;

public final class BidApplicationService {

    private final BidGroupRepository groups;
    private final TransactionService tx;
    private final BidService bidder;

    public BidApplicationService(BidGroupRepository groups, TransactionService tx, BidService bidder) {
        this.groups = groups;
        this.tx = tx;
        this.bidder = bidder;
    }

    public static final class PlaceBid {
        public String groupId;
    }

    public void placeBid(PlaceBid command) throws ApiException {
        tx.tx(SERIALIZABLE, () -> {
            var groupId = GroupId.parse(command.groupId);
            var group = groups.find(groupId).orElseThrow(notFound("Group " + groupId + " not found"));

            bidder.placeBid(group);
            groups.store(group);
        });
    }
}
