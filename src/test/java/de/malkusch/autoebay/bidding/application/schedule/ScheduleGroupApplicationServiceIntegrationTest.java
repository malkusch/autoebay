package de.malkusch.autoebay.bidding.application.schedule;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;

import de.malkusch.autoebay.bidding.application.AplicationServiceIntegrationTests;
import de.malkusch.autoebay.bidding.application.auctionGroup.RegisterBidApplicationService.RegisterBid;
import de.malkusch.autoebay.bidding.model.BidGroupRepository;
import de.malkusch.autoebay.bidding.model.BidService;
import de.malkusch.autoebay.bidding.model.Count;
import de.malkusch.autoebay.bidding.model.ItemNumber;
import de.malkusch.autoebay.bidding.model.Mandate;
import de.malkusch.autoebay.bidding.model.Price;
import de.malkusch.autoebay.bidding.model.SchedulesBidRepository;

public class ScheduleGroupApplicationServiceIntegrationTest {

    BidService.Api api = mock(BidService.Api.class);
    SchedulesBidRepository scheduledBids;
    BidGroupRepository groups;
    AplicationServiceIntegrationTests applicationServiceTests;

    @Test
    public void shouldBidOnCurrent() throws Exception {
        applicationServiceTests.setTime("2013-01-02");
        var groupId = applicationServiceTests.createGroup(3);
        var command = applicationServiceTests.registerBid(groupId, "2014-01-01");

        applicationServiceTests.winAuction(command.itemNumber);

        verify(api).bid(mandate(command), itemNumber(command), price(command));
    }

    @Test
    public void shouldBidOnNextAfterWinning() throws Exception {
        applicationServiceTests.setTime("2013-01-02");
        var groupId = applicationServiceTests.createGroup(3);
        var command1 = applicationServiceTests.registerBid(groupId, "2014-01-01");
        var command2 = applicationServiceTests.registerBid(groupId, "2014-01-02");

        applicationServiceTests.winAuction(command1.itemNumber);
        applicationServiceTests.winAuction(command2.itemNumber);

        verify(api).bid(mandate(command2), itemNumber(command2), price(command2));
    }

    @Test
    public void shouldBidOnNextAfterLosing() throws Exception {
        applicationServiceTests.setTime("2013-01-02");
        var groupId = applicationServiceTests.createGroup(3);
        var command1 = applicationServiceTests.registerBid(groupId, "2014-01-01");
        var command2 = applicationServiceTests.registerBid(groupId, "2014-01-02");

        applicationServiceTests.loseAuction(command1.itemNumber);
        applicationServiceTests.winAuction(command2.itemNumber);

        verify(api).bid(mandate(command2), itemNumber(command2), price(command2));
    }

    @Test
    public void shouldStopBiddingOnceGroupIsComplete() throws Exception {
        applicationServiceTests.setTime("2013-01-02");
        var groupId = applicationServiceTests.createGroup(2);
        var command1 = applicationServiceTests.registerBid(groupId, "2014-01-01");
        var command2 = applicationServiceTests.registerBid(groupId, "2014-01-02");
        var command3 = applicationServiceTests.registerBid(groupId, "2014-01-03");
        var command4 = applicationServiceTests.registerBid(groupId, "2014-01-04");
        applicationServiceTests.registerBid(groupId, "2014-01-05");

        applicationServiceTests.winAuction(command1.itemNumber);
        applicationServiceTests.loseAuction(command2.itemNumber);
        applicationServiceTests.winAuction(command3.itemNumber);

        var scheduled = scheduledBids.find(groupId);
        assertFalse(scheduled.isPresent());

        applicationServiceTests.winAuction(command4.itemNumber);
        verify(api, never()).bid(mandate(command4), itemNumber(command4), price(command4));
    }

    @Test
    public void shouldStopBiddingOnceGroupHasNoMoreBids() {
        applicationServiceTests.setTime("2013-01-02");
        var groupId = applicationServiceTests.createGroup(10);
        var command1 = applicationServiceTests.registerBid(groupId, "2014-01-01");
        var command2 = applicationServiceTests.registerBid(groupId, "2014-01-02");

        applicationServiceTests.winAuction(command1.itemNumber);
        applicationServiceTests.winAuction(command2.itemNumber);

        var scheduled = scheduledBids.find(groupId);
        assertFalse(scheduled.isPresent());
    }

    @Test
    public void shouldDecreaseRemainingWhenWon() {
        applicationServiceTests.setTime("2013-01-02");
        var groupId = applicationServiceTests.createGroup(10);
        var command1 = applicationServiceTests.registerBid(groupId, "2014-01-01");

        applicationServiceTests.winAuction(command1.itemNumber);

        assertEquals(new Count(9), groups.findAndAssertExisting(groupId).remaining());
    }

    @Test
    public void shouldNotDecreaseRemainingWhenOutbid() {
        applicationServiceTests.setTime("2013-01-02");
        var groupId = applicationServiceTests.createGroup(10);
        var command1 = applicationServiceTests.registerBid(groupId, "2014-01-01");

        applicationServiceTests.loseAuction(command1.itemNumber);

        assertEquals(new Count(10), groups.findAndAssertExisting(groupId).remaining());
    }

    @Test
    public void shouldNotDecreaseRemainingWhenAuctionWasInPast() {
        applicationServiceTests.setTime("2014-01-02");
        var groupId = applicationServiceTests.createGroup(10);
        applicationServiceTests.registerBid(groupId, "2014-01-01");
        var command2 = applicationServiceTests.registerBid(groupId, "2014-01-03");

        applicationServiceTests.loseAuction(command2.itemNumber);

        assertEquals(new Count(10), groups.findAndAssertExisting(groupId).remaining());
    }

    private Price price(RegisterBid command) {
        // TODO Auto-generated method stub
        return null;
    }

    private ItemNumber itemNumber(RegisterBid command) {
        // TODO Auto-generated method stub
        return null;
    }

    private Mandate mandate(RegisterBid command) {
        // TODO Auto-generated method stub
        return null;
    }
}
