package de.malkusch.autoebay.bidding.model;

import static java.util.Objects.requireNonNull;

import java.util.Optional;

public class ScheduledBidFactory {

    private final SchedulesBidRepository schedules;
    private final Scheduler scheduler;

    ScheduledBidFactory(SchedulesBidRepository schedules, Scheduler scheduler) {
        this.schedules = schedules;
        this.scheduler = scheduler;
    }

    public Optional<ScheduledBid> scheduledBid(BidGroup group) {
        requireNonNull(group);

        schedules.delete(group.id());
        scheduler.unschedule(group.id());
        return group.nextOpenBid().map(it -> new ScheduledBid(group.id(), it.bidTime()));
    }
}
