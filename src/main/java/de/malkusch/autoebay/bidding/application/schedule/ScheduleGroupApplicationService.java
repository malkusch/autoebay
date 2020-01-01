package de.malkusch.autoebay.bidding.application.schedule;

import static de.malkusch.autoebay.bidding.application.ApplicationExceptions.notFound;

import de.malkusch.autoebay.bidding.model.BidGroupRepository;
import de.malkusch.autoebay.bidding.model.GroupId;
import de.malkusch.autoebay.bidding.model.ScheduleGroupService;

public final class ScheduleGroupApplicationService {

    private final ScheduleGroupService scheduler;
    private final BidGroupRepository groups;

    ScheduleGroupApplicationService(ScheduleGroupService scheduler, BidGroupRepository groups) {
        this.scheduler = scheduler;
        this.groups = groups;
    }

    public static final class ScheduleGroup {
        public String groupId;
    }

    public void schedule(ScheduleGroup command) {
        var groupId = GroupId.parse(command.groupId);
        var group = groups.find(groupId).orElseThrow(notFound("Group " + groupId + " not found"));

        scheduler.schedule(group);
    }
}
