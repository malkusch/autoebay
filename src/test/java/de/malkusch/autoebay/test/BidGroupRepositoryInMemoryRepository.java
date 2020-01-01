package de.malkusch.autoebay.test;

import de.malkusch.autoebay.bidding.model.BidGroup;
import de.malkusch.autoebay.bidding.model.BidGroupRepository;
import de.malkusch.autoebay.bidding.model.GroupId;

public class BidGroupRepositoryInMemoryRepository extends InMemoryRepository<GroupId, BidGroup>
        implements BidGroupRepository {

    @Override
    public void store(BidGroup group) {
        entities.put(group.id(), group);
    }
}
