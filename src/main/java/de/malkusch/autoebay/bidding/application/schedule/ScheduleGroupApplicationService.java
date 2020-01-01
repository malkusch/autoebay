package de.malkusch.autoebay.bidding.application.schedule;

import static de.malkusch.autoebay.bidding.application.ApplicationExceptions.notFound;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

import de.malkusch.autoebay.bidding.application.bid.BidApplicationService;
import de.malkusch.autoebay.bidding.application.bid.SynchronizeGroupApplicationService;
import de.malkusch.autoebay.bidding.model.BidGroup.BidRegistered;
import de.malkusch.autoebay.bidding.model.BidGroupRepository;
import de.malkusch.autoebay.bidding.model.BidService.Api.ApiException;
import de.malkusch.autoebay.bidding.model.BidService.BidPlaced;
import de.malkusch.autoebay.bidding.model.GroupId;
import de.malkusch.autoebay.bidding.model.ScheduledBid;
import de.malkusch.autoebay.bidding.model.ScheduledBidFactory;
import de.malkusch.autoebay.bidding.model.Scheduler;
import de.malkusch.autoebay.bidding.model.SchedulesBidRepository;
import de.malkusch.autoebay.shared.infrastructure.persistance.TransactionService;
import de.malkusch.autoebay.shared.infrastructure.persistance.TransactionService.IsolationLevel;

public final class ScheduleGroupApplicationService {

    private final ScheduledBidFactory factory;
    private final BidGroupRepository groups;
    private final TransactionService tx;
    private final SchedulesBidRepository schedulesBids;
    private final Scheduler scheduler;
    private final BidApplicationService bidder;
    private final SynchronizeGroupApplicationService synchronizer;
    private final Duration auctionEndWindow;
    private final Clock clock;

    ScheduleGroupApplicationService(ScheduledBidFactory factory, SchedulesBidRepository schedulesBids,
            Duration auctionEndWindow, BidGroupRepository groups, TransactionService tx, Scheduler scheduler,
            BidApplicationService bidder, SynchronizeGroupApplicationService synchronizer, Clock clock) {

        this.factory = factory;
        this.schedulesBids = schedulesBids;
        this.groups = groups;
        this.tx = tx;
        this.scheduler = scheduler;
        this.bidder = bidder;
        this.auctionEndWindow = auctionEndWindow;
        this.synchronizer = synchronizer;
        this.clock = clock;
    }

    public void handleEvent(BidRegistered event) {
        var command = new ScheduleGroup();
        command.groupId = event.groupId;
        command.time = clock.instant();
        schedule(command);
    }

    public void handleEven(BidPlaced event) {
        var command = new ScheduleGroup();
        command.groupId = event.groupId;
        command.time = event.auctionEnd.plus(auctionEndWindow);
        schedule(command);
    }

    public static final class ScheduleGroup {
        public String groupId;
        public Instant time;
    }

    public void schedule(ScheduleGroup command) {
        tx.afterCurrentTx(() -> {
            var scheduledBid = tx.tx(IsolationLevel.SERIALIZABLE, () -> {
                var groupId = GroupId.parse(command.groupId);
                var group = groups.find(groupId).orElseThrow(notFound("Group " + groupId + " not found"));

                var result = factory.scheduledBid(group);
                result.ifPresent(it -> {
                    it.scheduleAt(scheduler.id());
                    schedulesBids.store(it);
                });
                return result;
            });

            scheduledBid.ifPresent(
                    it -> scheduler.schedule(command.time, it, this::scheduledSynchronizeAndThenScheduleBid));
        });
    }

    private void scheduledSynchronizeAndThenScheduleBid(ScheduledBid bid) {
        var command = new SynchronizeGroupApplicationService.SynchronizeGroup();
        command.groupId = bid.groupId().toString();
        synchronizer.synchronize(command);

        scheduler.schedule(bid, this::scheduledBid);
    }

    private void scheduledBid(ScheduledBid bid) {
        try {
            var command = new BidApplicationService.PlaceBid();
            command.groupId = bid.groupId().toString();

            bidder.placeBid(command);

        } catch (ApiException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
