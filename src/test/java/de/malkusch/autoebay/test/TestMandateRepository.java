package de.malkusch.autoebay.test;

import de.malkusch.autoebay.bidding.model.Mandate;
import de.malkusch.autoebay.bidding.model.MandateRepository;
import de.malkusch.autoebay.bidding.model.UserId;

public class TestMandateRepository extends InMemoryRepository<UserId, Mandate> implements MandateRepository {

    private static class TestMandate implements Mandate {

        private final UserId userId;

        TestMandate(UserId userId) {
            this.userId = userId;
        }

        @Override
        public UserId userId() {
            return userId;
        }
    }

    public void grantMandate(UserId userId) {
        var mandate = new TestMandate(userId);
        entities.put(userId, mandate);
    }
}
