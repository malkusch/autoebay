package de.malkusch.autoebay.bidding.application.auctionGroup;

import static de.malkusch.autoebay.bidding.application.ApplicationExceptions.notFound;

import java.math.BigDecimal;
import java.time.Duration;

import de.malkusch.autoebay.bidding.model.AuctionRepository;
import de.malkusch.autoebay.bidding.model.BidGroupRepository;
import de.malkusch.autoebay.bidding.model.GroupId;
import de.malkusch.autoebay.bidding.model.ItemNumber;
import de.malkusch.autoebay.bidding.model.Price;
import de.malkusch.autoebay.bidding.model.UserId;

public final class RegisterBidApplicationService {

    private final BidGroupRepository groups;
    private final AuctionRepository auctions;
    private final Duration biddingWindow;

    RegisterBidApplicationService(AuctionRepository auctions, BidGroupRepository groups, Duration biddingWindow) {
        this.groups = groups;
        this.auctions = auctions;
        this.biddingWindow = biddingWindow;
    }

    public static final class RegisterBid {
        public String userId;
        public String groupId;
        public String itemNumber;
        public BigDecimal price;
        public String currency;
    }

    public void register(RegisterBid command) {
        var userId = new UserId(command.userId);
        var groupId = new GroupId(userId, command.groupId);
        var group = groups.find(groupId).orElseThrow(notFound("Group " + groupId + " not found"));
        var itemNumber = new ItemNumber(command.itemNumber);
        var auction = auctions.find(itemNumber).orElseThrow(notFound("Item " + itemNumber + " not found"));
        var price = new Price(command.price, command.currency);

        group.registerBid(auction, price, biddingWindow);
        groups.store(group);
    }
}
