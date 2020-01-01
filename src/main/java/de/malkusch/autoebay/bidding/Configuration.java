package de.malkusch.autoebay.bidding;

import java.time.Clock;
import java.time.Duration;

import de.malkusch.autoebay.bidding.application.auctionGroup.CreateBidGroupApplicationService;
import de.malkusch.autoebay.bidding.application.auctionGroup.RegisterBidApplicationService;
import de.malkusch.autoebay.bidding.application.bid.BidApplicationService;
import de.malkusch.autoebay.bidding.application.bid.SynchronizeGroupApplicationService;
import de.malkusch.autoebay.bidding.application.schedule.ScheduleGroupApplicationService;
import de.malkusch.autoebay.bidding.model.AuctionRepository;
import de.malkusch.autoebay.bidding.model.BidGroupRepository;
import de.malkusch.autoebay.bidding.model.BidService;
import de.malkusch.autoebay.bidding.model.MandateRepository;
import de.malkusch.autoebay.bidding.model.ScheduledBidFactory;
import de.malkusch.autoebay.bidding.model.Scheduler;
import de.malkusch.autoebay.bidding.model.SchedulesBidRepository;
import de.malkusch.autoebay.bidding.model.SynchronizeBidGroupService;
import de.malkusch.autoebay.shared.infrastructure.persistance.TransactionService;

public final class Configuration {

    public Configuration(TransactionService tx, AuctionRepository auctions, BidGroupRepository groups,
            MandateRepository mandates, BidService.Api bidApi, SchedulesBidRepository schedules, Scheduler scheduler,
            SynchronizeBidGroupService.Api syncApi, Clock clock, Duration biddingWindow, Duration auctionEndWindow) {

        var model = new Model(auctions, groups, mandates, bidApi, schedules, scheduler, syncApi, clock);
        application = new Application(model, tx, biddingWindow, auctionEndWindow, clock);
    }

    public final Application application;

    public static final class Application {
        public final CreateBidGroupApplicationService createBidGroupApplicationService;
        public final RegisterBidApplicationService registerBidApplicationService;
        public final BidApplicationService bidApplicationService;
        public final SynchronizeGroupApplicationService synchronizeGroupApplicationService;
        public final ScheduleGroupApplicationService scheduleGroupApplicationService;

        public Application(Model model, TransactionService tx, Duration biddingWindow, Duration auctionEndWindow,
                Clock clock) {
            createBidGroupApplicationService = new CreateBidGroupApplicationService(model.mandates, model.groups, tx);
            registerBidApplicationService = new RegisterBidApplicationService(model.auctions, model.groups,
                    biddingWindow, tx);
            bidApplicationService = new BidApplicationService(model.groups, tx, model.bidService);
            synchronizeGroupApplicationService = new SynchronizeGroupApplicationService(model.groups, tx,
                    model.synchronizeBidGroupService);
            scheduleGroupApplicationService = new ScheduleGroupApplicationService(model.scheduledBidFactory,
                    model.schedules, auctionEndWindow, model.groups, tx, model.scheduler, bidApplicationService,
                    synchronizeGroupApplicationService, clock);
        }

    }

    public static final class Model {
        public final AuctionRepository auctions;
        public final BidGroupRepository groups;
        public final MandateRepository mandates;
        public final BidService bidService;
        public final ScheduledBidFactory scheduledBidFactory;
        public final SchedulesBidRepository schedules;
        public final Scheduler scheduler;
        public final SynchronizeBidGroupService synchronizeBidGroupService;

        public Model(AuctionRepository auctions, BidGroupRepository groups, MandateRepository mandates,
                BidService.Api bidApi, SchedulesBidRepository schedules, Scheduler scheduler,
                SynchronizeBidGroupService.Api syncApi, Clock clock) {

            this.auctions = auctions;
            this.groups = groups;
            this.mandates = mandates;
            bidService = new BidService(bidApi, mandates);
            this.scheduler = scheduler;
            this.schedules = schedules;
            scheduledBidFactory = new ScheduledBidFactory(schedules, scheduler);
            synchronizeBidGroupService = new SynchronizeBidGroupService(syncApi, clock, mandates);
        }
    }

}
