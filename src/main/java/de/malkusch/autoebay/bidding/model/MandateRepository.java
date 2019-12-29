package de.malkusch.autoebay.bidding.model;

import java.util.Optional;

public interface MandateRepository {

    Optional<Mandate> find(UserId userId);
}
