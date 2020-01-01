package de.malkusch.autoebay.bidding.model;

import java.util.Optional;

public interface SchedulesBidRepository {

    Optional<ScheduledBid> find(GroupId groupId);

    void store(ScheduledBid scheduledBid);

    void delete(GroupId id);

}
