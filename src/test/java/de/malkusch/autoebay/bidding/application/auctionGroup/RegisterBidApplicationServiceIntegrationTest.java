package de.malkusch.autoebay.bidding.application.auctionGroup;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.malkusch.autoebay.bidding.application.AplicationServiceIntegrationTests;
import de.malkusch.autoebay.bidding.model.SchedulesBidRepository;

public class RegisterBidApplicationServiceIntegrationTest {

    RegisterBidApplicationService registerBidService;
    SchedulesBidRepository scheduledBids;
    Duration biddingWindow;
    AplicationServiceIntegrationTests applicationServiceTests;

    @BeforeEach
    public void setUp() {
        applicationServiceTests = new AplicationServiceIntegrationTests();
        biddingWindow = applicationServiceTests.configuration.application.biddingWindow;
        scheduledBids = applicationServiceTests.configuration.model.schedules;
        registerBidService = applicationServiceTests.configuration.application.registerBidApplicationService;
    }

    @Test
    public void shouldScheduleFirstRegisteredBid() {
        var groupId = applicationServiceTests.createGroup(1);
        var auctionEnd = Instant.parse("2012-01-01T12:00:00.00Z");

        applicationServiceTests.registerBid(groupId, auctionEnd);

        var scheduled = scheduledBids.find(groupId).get();
        assertTrue(scheduled.scheduledAt().isPresent());
        assertTrue(scheduled.time().isBefore(auctionEnd));
        assertEquals(auctionEnd.minus(biddingWindow), scheduled.time());
    }

    @Test
    public void shouldNotScheduleNewBidWhenAfterNextBid() {
        var groupId = applicationServiceTests.createGroup(1);
        var firstTime = Instant.parse("2012-01-01T12:00:00.00Z");
        var secondTime = Instant.parse("2013-01-01T12:00:00.00Z");

        applicationServiceTests.registerBid(groupId, firstTime);
        applicationServiceTests.registerBid(groupId, secondTime);

        var scheduled = scheduledBids.find(groupId).get();
        assertTrue(scheduled.time().isBefore(firstTime));
    }

    @Test
    public void shouldScheduleNewBidWhenBeforeNextBid() {
        var groupId = applicationServiceTests.createGroup(1);
        var firstTime = Instant.parse("2013-01-01T12:00:00.00Z");
        var secondTime = Instant.parse("2012-01-01T12:00:00.00Z");

        applicationServiceTests.registerBid(groupId, firstTime);
        applicationServiceTests.registerBid(groupId, secondTime);

        var scheduled = scheduledBids.find(groupId).get();
        assertTrue(scheduled.time().isBefore(secondTime));
    }

    @Test
    public void shouldNotScheduleNewBidWhenGroupIsComplete() {
        var groupId = applicationServiceTests.createGroup(1);
        var auctionEnd = Instant.parse("2013-01-01T12:00:00.00Z");
        var command = applicationServiceTests.registerBid(groupId, auctionEnd);
        applicationServiceTests.winAuction(command.itemNumber);

        applicationServiceTests.registerBid(groupId, "2014-01-01T12:00:00.00Z");

        var scheduled = scheduledBids.find(groupId);
        assertFalse(scheduled.isPresent());
    }
}
