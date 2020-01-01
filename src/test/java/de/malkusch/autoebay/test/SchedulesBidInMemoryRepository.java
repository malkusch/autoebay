package de.malkusch.autoebay.test;

import de.malkusch.autoebay.bidding.model.GroupId;
import de.malkusch.autoebay.bidding.model.ScheduledBid;
import de.malkusch.autoebay.bidding.model.SchedulesBidRepository;

public class SchedulesBidInMemoryRepository extends InMemoryRepository<GroupId, ScheduledBid>
        implements SchedulesBidRepository {

    @Override
    public void store(ScheduledBid scheduledBid) {
        entities.put(scheduledBid.groupId(), scheduledBid);
    }
}
