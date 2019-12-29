package de.malkusch.autoebay.bidding.model;

import java.util.Optional;

public interface AuctionRepository {

    public Optional<Auction> find(ItemNumber number);

}
