package de.malkusch.autoebay.bidding.model;

import java.util.Optional;

public interface MandateRepository {

    Optional<Mandate> find(UserId userId);

    default Mandate findAndAssertExisting(UserId userId) {
        return find(userId).orElseThrow(() -> new IllegalArgumentException("Not mandate for user " + userId));
    }
}
