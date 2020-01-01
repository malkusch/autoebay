package de.malkusch.autoebay.test;

import de.malkusch.autoebay.bidding.model.Auction;
import de.malkusch.autoebay.bidding.model.AuctionRepository;
import de.malkusch.autoebay.bidding.model.ItemNumber;

public class AuctionInMemoryRepository extends InMemoryRepository<ItemNumber, Auction> implements AuctionRepository {

}
