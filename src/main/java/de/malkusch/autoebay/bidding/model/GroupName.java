package de.malkusch.autoebay.bidding.model;

import static java.util.Objects.requireNonNull;

public final class GroupName {
    private final String name;

    public GroupName(String name) {
        this.name = requireNonNull(name);
        if (name.isBlank()) {
            throw new IllegalArgumentException("Group name must not be empty");
        }
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof GroupName)) {
            return false;
        }
        var other = (GroupName) obj;
        return name.equals(other.name);
    }
}
