package de.malkusch.autoebay.bidding.application.auctionGroup;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;

import org.junit.jupiter.api.Test;

import de.malkusch.autoebay.bidding.model.GroupId;
import de.malkusch.autoebay.bidding.model.SchedulesBidRepository;

public class RegisterBidApplicationServiceIntegrationTest {

    RegisterBidApplicationService registerBidService;
    SchedulesBidRepository scheduledBids;
    Duration biddingWindow;

    @Test
    public void shouldScheduleFirstRegisteredBid() {
        var groupId = createGroup(1);
        var auctionEnd = Instant.parse("2012-01-01 12:00:00.00Z");

        registerBid(groupId, auctionEnd);

        var scheduled = scheduledBids.find(groupId).get();
        assertTrue(scheduled.scheduledAt().isPresent());
        assertTrue(scheduled.time().isBefore(auctionEnd));
        assertEquals(auctionEnd.minus(biddingWindow), scheduled.time());
    }

    @Test
    public void shouldNotScheduleNewBidWhenAfterNextBid() {
        var groupId = createGroup(1);
        var firstTime = Instant.parse("2012-01-01 12:00:00.00Z");
        var secondTime = Instant.parse("2013-01-01 12:00:00.00Z");

        registerBid(groupId, firstTime);
        registerBid(groupId, secondTime);

        var scheduled = scheduledBids.find(groupId).get();
        assertTrue(scheduled.time().isBefore(firstTime));
    }

    @Test
    public void shouldScheduleNewBidWhenBeforeNextBid() {
        var groupId = createGroup(1);
        var firstTime = Instant.parse("2013-01-01 12:00:00.00Z");
        var secondTime = Instant.parse("2012-01-01 12:00:00.00Z");

        registerBid(groupId, firstTime);
        registerBid(groupId, secondTime);

        var scheduled = scheduledBids.find(groupId).get();
        assertTrue(scheduled.time().isBefore(secondTime));
    }

    @Test
    public void shouldNotScheduleNewBidWhenGroupIsComplete() {
        var groupId = createGroup(1);
        var auctionEnd = Instant.parse("2013-01-01 12:00:00.00Z");
        var command = registerBid(groupId, auctionEnd);
        winAuction(command.itemNumber);

        registerBid(groupId, "2014-01-01 12:00:00.00Z");

        var scheduled = scheduledBids.find(groupId);
        assertFalse(scheduled.isPresent());
    }

    private static final BigDecimal ANY_PRICE = new BigDecimal("123.45");

    private void registerBid(GroupId groupId, String auctionEnd) {
        registerBid(groupId, Instant.parse(auctionEnd));
    }

    private int counter = 0;

    private RegisterBidApplicationService.RegisterBid registerBid(GroupId groupId, Instant auctionEnd) {
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

    private void setAuction(String itemNumber, Instant date) {
        // TODO
    }

    private void winAuction(String itemNumber) {
        // TODO Auto-generated method stub

    }

    CreateBidGroupApplicationService createGroupService;

    private GroupId createGroup(int count) {
        var command = new CreateBidGroupApplicationService.CreateBiddingGroup();
        command.count = count;
        command.name = "test";
        command.userId = "userId";
        return GroupId.parse(createGroupService.create(command).groupId);
    }
}
