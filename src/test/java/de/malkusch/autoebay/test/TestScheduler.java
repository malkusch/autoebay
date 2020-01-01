package de.malkusch.autoebay.test;

import java.time.Instant;
import java.util.function.Consumer;

import de.malkusch.autoebay.bidding.model.GroupId;
import de.malkusch.autoebay.bidding.model.ScheduledBid;
import de.malkusch.autoebay.bidding.model.Scheduler;

public class TestScheduler implements Scheduler {

    private final Scheduler scheduler;

    public TestScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public void setTime(Instant time) {

    }

    @Override
    public void schedule(ScheduledBid bid, Consumer<ScheduledBid> task) {
        // TODO Auto-generated method stub

    }

    @Override
    public void schedule(Instant time, ScheduledBid bid, Consumer<ScheduledBid> task) {
        // TODO Auto-generated method stub

    }

    @Override
    public void unschedule(GroupId groupId) {
        // TODO Auto-generated method stub

    }

    @Override
    public Id id() {
        // TODO Auto-generated method stub
        return null;
    }

}
