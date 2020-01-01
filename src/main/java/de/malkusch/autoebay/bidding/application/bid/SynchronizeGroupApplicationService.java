package de.malkusch.autoebay.bidding.application.bid;

import static de.malkusch.autoebay.bidding.application.ApplicationExceptions.notFound;
import static de.malkusch.autoebay.shared.infrastructure.persistance.TransactionService.IsolationLevel.SERIALIZABLE;

import de.malkusch.autoebay.bidding.model.BidGroupRepository;
import de.malkusch.autoebay.bidding.model.GroupId;
import de.malkusch.autoebay.bidding.model.SynchronizeBidGroupService;
import de.malkusch.autoebay.shared.infrastructure.persistance.TransactionService;

public final class SynchronizeGroupApplicationService {

    private final BidGroupRepository groups;
    private final TransactionService tx;
    private final SynchronizeBidGroupService synchronizer;

    SynchronizeGroupApplicationService(BidGroupRepository groups, TransactionService tx,
            SynchronizeBidGroupService synchronizer) {

        this.groups = groups;
        this.tx = tx;
        this.synchronizer = synchronizer;
    }

    public static final class SynchronizeGroup {
        public String groupId;
    }

    public void synchronize(SynchronizeGroup command) {
        tx.tx(SERIALIZABLE, () -> {
            var groupId = GroupId.parse(command.groupId);
            var group = groups.find(groupId).orElseThrow(notFound("Group " + groupId + " not found"));

            synchronizer.synchronize(group);
            groups.store(group);
        });
    }
}
