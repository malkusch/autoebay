package de.malkusch.autoebay.bidding.model;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.empty;

import java.time.Instant;
import java.util.Optional;

public final class ScheduledBid {

    private final GroupId groupId;
    private final Instant time;
    private final Optional<Scheduler.Id> scheduledAt;

    public ScheduledBid(GroupId groupId, Instant time) {
        this(groupId, time, empty());
    }

    public ScheduledBid(GroupId groupId, Instant time, Optional<Scheduler.Id> scheduledAt) {
        this.groupId = requireNonNull(groupId);
        this.time = requireNonNull(time);
        this.scheduledAt = requireNonNull(scheduledAt);
    }

    public Instant time() {
        return time;
    }
}