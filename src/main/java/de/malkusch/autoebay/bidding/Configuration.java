package de.malkusch.autoebay.bidding;

import java.time.Clock;
import java.time.Duration;

import de.malkusch.autoebay.bidding.application.auctionGroup.CreateBidGroupApplicationService;
import de.malkusch.autoebay.bidding.application.auctionGroup.RegisterBidApplicationService;
import de.malkusch.autoebay.bidding.model.AuctionRepository;
import de.malkusch.autoebay.bidding.model.BidGroupRepository;
import de.malkusch.autoebay.bidding.model.MandateRepository;
import de.malkusch.autoebay.shared.infrastructure.event.EventDispatcher;
import de.malkusch.autoebay.shared.infrastructure.persistance.TransactionService;

public final class Configuration {

    public Configuration(TransactionService tx, AuctionRepository auctions, BidGroupRepository groups,
            MandateRepository mandates, Clock clock, Duration biddingWindow, Duration auctionEndWindow,
            EventDispatcher eventDispatcher) {

        model = new Model(auctions, groups, mandates, clock);
        application = new Application(model, tx, biddingWindow, auctionEndWindow, clock, eventDispatcher);
    }

    public final Application application;

    public static final class Application {
        public final Duration auctionEndWindow;
        public final Duration biddingWindow;
        public final CreateBidGroupApplicationService createBidGroupApplicationService;
        public final RegisterBidApplicationService registerBidApplicationService;

        public Application(Model model, TransactionService tx, Duration biddingWindow, Duration auctionEndWindow,
                Clock clock, EventDispatcher eventDispatcher) {

            this.auctionEndWindow = auctionEndWindow;
            this.biddingWindow = biddingWindow;
            createBidGroupApplicationService = new CreateBidGroupApplicationService(model.mandates, model.groups, tx);
            registerBidApplicationService = new RegisterBidApplicationService(model.auctions, model.groups,
                    biddingWindow, tx);
        }

    }

    public final Model model;

    public static final class Model {
        public final AuctionRepository auctions;
        public final BidGroupRepository groups;
        public final MandateRepository mandates;

        public Model(AuctionRepository auctions, BidGroupRepository groups, MandateRepository mandates, Clock clock) {
            this.auctions = auctions;
            this.groups = groups;
            this.mandates = mandates;
        }
    }

}
