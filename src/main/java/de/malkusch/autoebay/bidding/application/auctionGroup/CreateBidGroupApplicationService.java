package de.malkusch.autoebay.bidding.application.auctionGroup;

import static de.malkusch.autoebay.bidding.application.ApplicationExceptions.notAuthenticated;
import static de.malkusch.autoebay.shared.infrastructure.persistance.TransactionService.IsolationLevel.SERIALIZABLE;

import de.malkusch.autoebay.bidding.model.BidGroup;
import de.malkusch.autoebay.bidding.model.BidGroupRepository;
import de.malkusch.autoebay.bidding.model.Count;
import de.malkusch.autoebay.bidding.model.GroupName;
import de.malkusch.autoebay.bidding.model.MandateRepository;
import de.malkusch.autoebay.bidding.model.UserId;
import de.malkusch.autoebay.shared.infrastructure.persistance.TransactionService;

public final class CreateBidGroupApplicationService {

    private final MandateRepository mandates;
    private final BidGroupRepository groups;
    private final TransactionService tx;

    CreateBidGroupApplicationService(MandateRepository mandates, BidGroupRepository groups, TransactionService tx) {
        this.mandates = mandates;
        this.groups = groups;
        this.tx = tx;
    }

    public static final class CreateBiddingGroup {
        public String userId;
        public String name;
        public int count;
    }

    public void create(CreateBiddingGroup command) {
        tx.tx(SERIALIZABLE, () -> {
            var userId = new UserId(command.userId);
            var mandate = mandates.find(userId).orElseThrow(notAuthenticated("No mandate for " + userId));
            var name = new GroupName(command.name);
            var count = new Count(command.count);

            var group = new BidGroup(mandate, name, count);
            groups.store(group);
        });
    }
}
