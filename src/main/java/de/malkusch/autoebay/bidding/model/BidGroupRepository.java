package de.malkusch.autoebay.bidding.model;

import java.util.Optional;

public interface BidGroupRepository {

    void store(BidGroup group);

    Optional<BidGroup> find(GroupId groupId);

    default BidGroup findAndAssertExisting(GroupId groupId) {
        return find(groupId).orElseThrow(() -> new IllegalArgumentException("Group not found " + groupId));
    }
}
