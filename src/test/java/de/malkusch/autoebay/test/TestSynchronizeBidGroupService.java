package de.malkusch.autoebay.test;

import de.malkusch.autoebay.bidding.model.ItemNumber;
import de.malkusch.autoebay.bidding.model.Mandate;
import de.malkusch.autoebay.bidding.model.SynchronizeBidGroupService;

public final class TestSynchronizeBidGroupService implements SynchronizeBidGroupService.Api {

    @Override
    public boolean isWon(Mandate mandate, ItemNumber itemNumber) {
        return false;
    }

    public void setWon(ItemNumber itemNumber) {
        // TODO
    }

}
