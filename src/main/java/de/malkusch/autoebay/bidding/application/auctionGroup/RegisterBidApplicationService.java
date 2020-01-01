package de.malkusch.autoebay.bidding.application.auctionGroup;

import static de.malkusch.autoebay.bidding.application.ApplicationExceptions.notFound;
import static de.malkusch.autoebay.shared.infrastructure.persistance.TransactionService.IsolationLevel.SERIALIZABLE;

import java.math.BigDecimal;
import java.time.Duration;

import de.malkusch.autoebay.bidding.model.AuctionRepository;
import de.malkusch.autoebay.bidding.model.BidGroupRepository;
import de.malkusch.autoebay.bidding.model.GroupId;
import de.malkusch.autoebay.bidding.model.ItemNumber;
import de.malkusch.autoebay.bidding.model.Price;
import de.malkusch.autoebay.shared.infrastructure.persistance.TransactionService;

public final class RegisterBidApplicationService {

    private final BidGroupRepository groups;
    private final AuctionRepository auctions;
    private final Duration biddingWindow;
    private final TransactionService tx;

    public RegisterBidApplicationService(AuctionRepository auctions, BidGroupRepository groups, Duration biddingWindow,
            TransactionService tx) {

        this.groups = groups;
        this.auctions = auctions;
        this.biddingWindow = biddingWindow;
        this.tx = tx;
    }

    public static final class RegisterBid {
        public String groupId;
        public String itemNumber;
        public BigDecimal price;
        public String currency;
    }

    public void register(RegisterBid command) {
        tx.tx(SERIALIZABLE, () -> {
            var groupId = GroupId.parse(command.groupId);
            var group = groups.find(groupId).orElseThrow(notFound("Group " + groupId + " not found"));
            var itemNumber = new ItemNumber(command.itemNumber);
            var auction = auctions.find(itemNumber).orElseThrow(notFound("Item " + itemNumber + " not found"));
            var price = new Price(command.price, command.currency);

            group.registerBid(auction, price, biddingWindow);
            groups.store(group);
        });
    }
}
