package de.malkusch.autoebay.bidding.application.schedule;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import de.malkusch.autoebay.bidding.application.auctionGroup.CreateBidGroupApplicationService;
import de.malkusch.autoebay.bidding.application.auctionGroup.RegisterBidApplicationService.RegisterBid;
import de.malkusch.autoebay.bidding.model.BidGroupRepository;
import de.malkusch.autoebay.bidding.model.BidService;
import de.malkusch.autoebay.bidding.model.Count;
import de.malkusch.autoebay.bidding.model.GroupId;
import de.malkusch.autoebay.bidding.model.ItemNumber;
import de.malkusch.autoebay.bidding.model.Mandate;
import de.malkusch.autoebay.bidding.model.Price;
import de.malkusch.autoebay.bidding.model.SchedulesBidRepository;

public class ScheduleGroupApplicationServiceIntegrationTest {

    BidService.Api api = Mockito.mock(BidService.Api.class);
    SchedulesBidRepository scheduledBids;
    BidGroupRepository groups;

    @Test
    public void shouldBidOnCurrent() throws Exception {
        setTime("2013-01-02");
        var groupId = createGroup(3);
        var command = registerBid(groupId, "2014-01-01");

        winAuction(command.itemNumber);

        verify(api).bid(mandate(command), itemNumber(command), price(command));
    }

    @Test
    public void shouldBidOnNextAfterWinning() throws Exception {
        setTime("2013-01-02");
        var groupId = createGroup(3);
        var command1 = registerBid(groupId, "2014-01-01");
        var command2 = registerBid(groupId, "2014-01-02");

        winAuction(command1.itemNumber);
        winAuction(command2.itemNumber);

        verify(api).bid(mandate(command2), itemNumber(command2), price(command2));
    }

    @Test
    public void shouldBidOnNextAfterLosing() throws Exception {
        setTime("2013-01-02");
        var groupId = createGroup(3);
        var command1 = registerBid(groupId, "2014-01-01");
        var command2 = registerBid(groupId, "2014-01-02");

        loseAuction(command1.itemNumber);
        winAuction(command2.itemNumber);

        verify(api).bid(mandate(command2), itemNumber(command2), price(command2));
    }

    @Test
    public void shouldStopBiddingOnceGroupIsComplete() throws Exception {
        setTime("2013-01-02");
        var groupId = createGroup(2);
        var command1 = registerBid(groupId, "2014-01-01");
        var command2 = registerBid(groupId, "2014-01-02");
        var command3 = registerBid(groupId, "2014-01-03");
        var command4 = registerBid(groupId, "2014-01-04");
        registerBid(groupId, "2014-01-05");

        winAuction(command1.itemNumber);
        loseAuction(command2.itemNumber);
        winAuction(command3.itemNumber);

        var scheduled = scheduledBids.find(groupId);
        assertFalse(scheduled.isPresent());

        winAuction(command4.itemNumber);
        verify(api, never()).bid(mandate(command4), itemNumber(command4), price(command4));
    }

    @Test
    public void shouldStopBiddingOnceGroupHasNoMoreBids() {
        setTime("2013-01-02");
        var groupId = createGroup(10);
        var command1 = registerBid(groupId, "2014-01-01");
        var command2 = registerBid(groupId, "2014-01-02");

        winAuction(command1.itemNumber);
        winAuction(command2.itemNumber);

        var scheduled = scheduledBids.find(groupId);
        assertFalse(scheduled.isPresent());
    }

    @Test
    public void shouldDecreaseRemainingWhenWon() {
        setTime("2013-01-02");
        var groupId = createGroup(10);
        var command1 = registerBid(groupId, "2014-01-01");

        winAuction(command1.itemNumber);

        assertEquals(new Count(9), groups.findAndAssertExisting(groupId).remaining());
    }

    @Test
    public void shouldNotDecreaseRemainingWhenOutbid() {
        setTime("2013-01-02");
        var groupId = createGroup(10);
        var command1 = registerBid(groupId, "2014-01-01");

        loseAuction(command1.itemNumber);

        assertEquals(new Count(10), groups.findAndAssertExisting(groupId).remaining());
    }

    @Test
    public void shouldNotDecreaseRemainingWhenAuctionWasInPast() {
        setTime("2012-01-02");
        var groupId = createGroup(10);
        var command1 = registerBid(groupId, "2014-01-01");
        var command2 = registerBid(groupId, "2014-01-03");

        loseAuction(command2.itemNumber);

        assertEquals(new Count(10), groups.findAndAssertExisting(groupId).remaining());
    }

    private void setTime(String time) {
        // TODO
    }

    private RegisterBid registerBid(GroupId groupId, String auctionEnd) {
        // TODO
        return null;
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

    private void loseAuction(String itemNumber) {
        // TODO Auto-generated method stub

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
