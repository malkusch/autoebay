package de.malkusch.autoebay.test;

import java.time.Clock;
import java.time.Duration;

import de.malkusch.autoebay.bidding.Configuration;
import de.malkusch.autoebay.bidding.model.AuctionRepository;
import de.malkusch.autoebay.bidding.model.BidGroupRepository;
import de.malkusch.autoebay.bidding.model.MandateRepository;
import de.malkusch.autoebay.shared.infrastructure.event.EventDispatcher;
import de.malkusch.autoebay.shared.infrastructure.event.EventPublisher;
import de.malkusch.autoebay.shared.infrastructure.event.LocalEventDispatcher;
import de.malkusch.autoebay.shared.infrastructure.persistance.TransactionService;

public class TestConfigurationBuilder {

    public EventDispatcher eventDispatcher = new LocalEventDispatcher();
    public TransactionService tx = new NullTransactionService();
    public AuctionRepository auctions = new AuctionInMemoryRepository();
    public BidGroupRepository groups = new BidGroupRepositoryInMemoryRepository();
    public MandateRepository mandates = new TestMandateRepository();
    public Clock clock = Clock.systemUTC();
    public Duration biddingWindow = Duration.ofSeconds(5);
    public Duration auctionEndWindow = Duration.ofSeconds(3);

    public Configuration build() {
        return new Configuration(tx, auctions, groups, mandates, clock, biddingWindow, auctionEndWindow,
                eventDispatcher);
    }

    public void enableEvents() {
        new EventPublisher(eventDispatcher);
    }
}
