package de.malkusch.autoebay.bidding.application;

import static java.math.BigDecimal.ONE;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

import de.malkusch.autoebay.bidding.Configuration;
import de.malkusch.autoebay.bidding.application.auctionGroup.CreateBidGroupApplicationService;
import de.malkusch.autoebay.bidding.application.auctionGroup.RegisterBidApplicationService;
import de.malkusch.autoebay.bidding.model.Auction;
import de.malkusch.autoebay.bidding.model.AuctionRepository;
import de.malkusch.autoebay.bidding.model.GroupId;
import de.malkusch.autoebay.bidding.model.ItemNumber;
import de.malkusch.autoebay.bidding.model.Price;
import de.malkusch.autoebay.bidding.model.UserId;
import de.malkusch.autoebay.test.TestConfigurationBuilder;
import de.malkusch.autoebay.test.TestMandateRepository;

public class AplicationServiceIntegrationTests {

    private final RegisterBidApplicationService registerBidService;

    public AplicationServiceIntegrationTests() {
        var builder = new TestConfigurationBuilder();

        mandates = new TestMandateRepository();
        builder.mandates = mandates;

        auctions = mock(AuctionRepository.class);
        builder.auctions = auctions;

        configuration = builder.build();
        builder.enableEvents();

        this.registerBidService = configuration.application.registerBidApplicationService;
        this.createGroupService = configuration.application.createBidGroupApplicationService;
    }

    public final Configuration configuration;

    private static final BigDecimal ANY_PRICE = new BigDecimal("123.45");

    public RegisterBidApplicationService.RegisterBid registerBid(GroupId groupId, String auctionEnd) {
        return registerBid(groupId, Instant.parse(auctionEnd));
    }

    private int counter = 0;

    public RegisterBidApplicationService.RegisterBid registerBid(GroupId groupId, Instant auctionEnd) {
        var itemNumber = "item" + ++counter;
        var command = new RegisterBidApplicationService.RegisterBid();
        command.currency = "EUR";
        command.groupId = groupId.toString();
        command.itemNumber = itemNumber;
        command.price = ANY_PRICE;
        setAuction(itemNumber, auctionEnd);
        registerBidService.register(command);
        return command;
    }

    private final AuctionRepository auctions;
    private final static Price ONE_EURO = new Price(ONE, "EUR");

    private void setAuction(String itemNumber, Instant date) {
        var itemNumberVO = new ItemNumber(itemNumber);
        var auction = new Auction(itemNumberVO, ONE_EURO, date);
        when(auctions.find(itemNumberVO)).thenReturn(Optional.of(auction));
    }

    private final TestMandateRepository mandates;
    private final CreateBidGroupApplicationService createGroupService;

    public GroupId createGroup(int count) {
        var command = new CreateBidGroupApplicationService.CreateBiddingGroup();
        command.count = count;
        command.name = "test";
        command.userId = "userId";

        var userId = new UserId(command.userId);
        mandates.grantMandate(userId);

        return GroupId.parse(createGroupService.create(command).groupId);
    }
}
