package de.malkusch.autoebay.bidding.application.auctionGroup;

import static de.malkusch.autoebay.bidding.application.ApplicationExceptions.notAuthenticated;

import de.malkusch.autoebay.bidding.model.BidGroup;
import de.malkusch.autoebay.bidding.model.BidGroupRepository;
import de.malkusch.autoebay.bidding.model.Count;
import de.malkusch.autoebay.bidding.model.GroupName;
import de.malkusch.autoebay.bidding.model.MandateRepository;
import de.malkusch.autoebay.bidding.model.UserId;

public final class CreateBidGroupApplicationService {

    private final MandateRepository mandates;
    private final BidGroupRepository groups;

    CreateBidGroupApplicationService(MandateRepository mandates, BidGroupRepository groups) {
        this.mandates = mandates;
        this.groups = groups;
    }

    public static final class CreateBiddingGroup {
        public String userId;
        public String name;
        public int count;
    }

    public void create(CreateBiddingGroup command) {
        var userId = new UserId(command.userId);
        var mandate = mandates.find(userId).orElseThrow(notAuthenticated("No mandate for " + userId));
        var name = new GroupName(command.name);
        var count = new Count(command.count);

        var group = new BidGroup(mandate, name, count);
        groups.store(group);
    }
}
