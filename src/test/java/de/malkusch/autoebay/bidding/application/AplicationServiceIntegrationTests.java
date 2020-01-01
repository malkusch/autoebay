package de.malkusch.autoebay.bidding.application;

import static java.math.BigDecimal.ONE;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import de.malkusch.autoebay.bidding.application.auctionGroup.CreateBidGroupApplicationService;
import de.malkusch.autoebay.bidding.application.auctionGroup.RegisterBidApplicationService;
import de.malkusch.autoebay.bidding.model.Auction;
import de.malkusch.autoebay.bidding.model.AuctionRepository;
import de.malkusch.autoebay.bidding.model.GroupId;
import de.malkusch.autoebay.bidding.model.ItemNumber;
import de.malkusch.autoebay.bidding.model.Price;
import de.malkusch.autoebay.test.TestScheduler;
import de.malkusch.autoebay.test.TestSynchronizeBidGroupService;

public class AplicationServiceIntegrationTests {

    private final RegisterBidApplicationService registerBidService;

    AplicationServiceIntegrationTests(RegisterBidApplicationService registerBidService) {
        this.registerBidService = registerBidService;
    }

    private static final BigDecimal ANY_PRICE = new BigDecimal("123.45");

    public RegisterBidApplicationService.RegisterBid registerBid(GroupId groupId, String auctionEnd) {
        return registerBid(groupId, Instant.parse(auctionEnd));
    }

    private int counter = 0;

    public RegisterBidApplicationService.RegisterBid registerBid(GroupId groupId, Instant auctionEnd) {
        var itemNumber = "item" + ++counter;
        var command = new RegisterBidApplicationService.RegisterBid();
        command.currency = "EUR";
        command.groupId = groupId.toString();
        command.itemNumber = itemNumber;
        command.price = ANY_PRICE;
        setAuction(itemNumber, auctionEnd);
        registerBidService.register(command);
        return command;
    }

    private final AuctionRepository auctions = mock(AuctionRepository.class);
    private final static Price ONE_EURO = new Price(ONE, "EUR");

    private void setAuction(String itemNumber, Instant date) {
        var itemNumberVO = new ItemNumber(itemNumber);
        var auction = new Auction(itemNumberVO, ONE_EURO, date);
        when(auctions.find(itemNumberVO)).thenReturn(Optional.of(auction));
    }

    TestScheduler scheduler;

    public void setTime(Instant time) {
        scheduler.setTime(time);
    }

    public void setTime(String time) {
        setTime(Instant.parse(time));
    }

    Duration auctionEndWindow;
    TestSynchronizeBidGroupService syncApi;

    public void winAuction(String itemNumber) {
        var itemNumberVO = new ItemNumber(itemNumber);
        var auction = auctions.find(itemNumberVO).get();

        setTime(auction.end());
        syncApi.setWon(itemNumberVO);
        setTime(auction.end().plus(auctionEndWindow));
    }

    public void loseAuction(String itemNumber) {
        var itemNumberVO = new ItemNumber(itemNumber);
        var auction = auctions.find(itemNumberVO).get();

        setTime(auction.end().plus(auctionEndWindow));
    }

    CreateBidGroupApplicationService createGroupService;

    public GroupId createGroup(int count) {
        var command = new CreateBidGroupApplicationService.CreateBiddingGroup();
        command.count = count;
        command.name = "test";
        command.userId = "userId";
        return GroupId.parse(createGroupService.create(command).groupId);
    }
}
