package de.malkusch.autoebay.test;

import de.malkusch.autoebay.bidding.model.Mandate;
import de.malkusch.autoebay.bidding.model.MandateRepository;
import de.malkusch.autoebay.bidding.model.UserId;

public class MandateInMemoryRepository extends InMemoryRepository<UserId, Mandate> implements MandateRepository {

}
