package de.malkusch.autoebay.bidding.application.schedule;

import static de.malkusch.autoebay.bidding.application.ApplicationExceptions.notFound;

import de.malkusch.autoebay.bidding.model.BidGroupRepository;
import de.malkusch.autoebay.bidding.model.GroupId;
import de.malkusch.autoebay.bidding.model.ScheduleGroupService;
import de.malkusch.autoebay.shared.infrastructure.persistance.TransactionService;
import de.malkusch.autoebay.shared.infrastructure.persistance.TransactionService.IsolationLevel;

public final class ScheduleGroupApplicationService {

    private final ScheduleGroupService scheduler;
    private final BidGroupRepository groups;
    private final TransactionService tx;

    ScheduleGroupApplicationService(ScheduleGroupService scheduler, BidGroupRepository groups, TransactionService tx) {
        this.scheduler = scheduler;
        this.groups = groups;
        this.tx = tx;
    }

    public static final class ScheduleGroup {
        public String groupId;
    }

    public void schedule(ScheduleGroup command) {
        tx.tx(IsolationLevel.SERIALIZABLE, () -> {
            var groupId = GroupId.parse(command.groupId);
            var group = groups.find(groupId).orElseThrow(notFound("Group " + groupId + " not found"));

            scheduler.schedule(group);
        });
    }
}
