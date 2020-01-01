package de.malkusch.autoebay.bidding.model;

import static java.util.Objects.requireNonNull;

public final class GroupId {

    private final UserId userId;
    private final String id;

    public GroupId(UserId userId, String id) {
        this.userId = requireNonNull(userId);
        this.id = requireNonNull(id);
        if (id.isBlank()) {
            throw new IllegalArgumentException("Group Id must not be empty");
        }
    }

    public static GroupId parse(String id) {
        var parts = id.split("/");
        var userId = new UserId(parts[0]);
        var groupId = parts[1];
        return new GroupId(userId, groupId);
    }

    @Override
    public String toString() {
        return String.format("%s/%s", userId, id);
    }

    @Override
    public int hashCode() {
        return userId.hashCode() + id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof GroupId)) {
            return false;
        }
        var other = (GroupId) obj;
        return userId.equals(other.userId) && id.equals(other.id);
    }
}
