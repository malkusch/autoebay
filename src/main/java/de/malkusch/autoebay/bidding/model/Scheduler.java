package de.malkusch.autoebay.bidding.model;

import static java.util.Objects.requireNonNull;

public interface Scheduler {

    public final class Id {

        private final String id;

        public Id(String id) {
            this.id = requireNonNull(id);
            if (id.isBlank()) {
                throw new IllegalArgumentException("Scheduler Id must not be empty");
            }
        }

        @Override
        public String toString() {
            return id;
        }

        @Override
        public int hashCode() {
            return id.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Id)) {
                return false;
            }
            var other = (Id) obj;
            return id.equals(other.id);
        }
    }

    boolean schedule(ScheduledBid bid);

    void unschedule(GroupId id);
}