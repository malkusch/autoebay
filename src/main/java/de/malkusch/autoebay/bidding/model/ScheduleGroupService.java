package de.malkusch.autoebay.bidding.model;

import static java.util.Objects.requireNonNull;

public class ScheduleGroupService {

    private final SynchronizeBidGroupService synchronizer;
    private final SchedulesBidRepository schedules;
    private final Scheduler scheduler;

    ScheduleGroupService(SynchronizeBidGroupService synchronizer, SchedulesBidRepository schedules,
            Scheduler scheduler) {

        this.synchronizer = synchronizer;
        this.schedules = schedules;
        this.scheduler = scheduler;
    }

    public void schedule(BidGroup group) {
        requireNonNull(group);
        synchronizer.synchronize(group);

        scheduler.unschedule(group.id());
        schedules.delete(group.id());
        var maybeBid = group.nextOpenBid().map(it -> new ScheduledBid(group.id(), it.bidTime()));
        if (maybeBid.isEmpty()) {
            return;
        }
        var bid = maybeBid.get();

        schedules.store(bid);
        scheduler.schedule(bid);
    }
}
