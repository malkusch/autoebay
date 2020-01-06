package de.malkusch.autoebay.test;

import static org.mockito.Mockito.mock;

import java.time.Clock;
import java.time.Duration;

import de.malkusch.autoebay.bidding.Configuration;
import de.malkusch.autoebay.bidding.model.AuctionRepository;
import de.malkusch.autoebay.bidding.model.BidGroupRepository;
import de.malkusch.autoebay.bidding.model.BidService;
import de.malkusch.autoebay.bidding.model.MandateRepository;
import de.malkusch.autoebay.bidding.model.Scheduler;
import de.malkusch.autoebay.bidding.model.SchedulesBidRepository;
import de.malkusch.autoebay.bidding.model.SynchronizeBidGroupService;
import de.malkusch.autoebay.shared.infrastructure.persistance.TransactionService;

public class TestConfigurationBuilder {

    public TransactionService tx = new NullTransactionService();
    public AuctionRepository auctions = new AuctionInMemoryRepository();
    public BidGroupRepository groups = new BidGroupRepositoryInMemoryRepository();
    public MandateRepository mandates = new TestMandateRepository();
    public BidService.Api bidApi = mock(BidService.Api.class);
    public SchedulesBidRepository schedules = new SchedulesBidInMemoryRepository();
    public Scheduler scheduler = new TestScheduler();
    public SynchronizeBidGroupService.Api syncApi = mock(SynchronizeBidGroupService.Api.class);
    public Clock clock = Clock.systemUTC();
    public Duration biddingWindow = Duration.ofSeconds(5);
    public Duration auctionEndWindow = Duration.ofSeconds(3);

    public Configuration build() {
        return new Configuration(tx, auctions, groups, mandates, bidApi, schedules, scheduler, syncApi, clock,
                biddingWindow, auctionEndWindow);
    }
}
