package tech.eportfolio.server.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.eportfolio.server.common.constant.FeedType;
import tech.eportfolio.server.common.constant.ParentType;
import tech.eportfolio.server.model.*;
import tech.eportfolio.server.repository.FeedItemRepository;
import tech.eportfolio.server.service.FeedHistoryService;
import tech.eportfolio.server.service.FeedItemService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class FeedItemServiceImpl implements FeedItemService {

    private final FeedItemRepository feedItemRepository;

    private final FeedHistoryService feedHistoryService;

    @Autowired
    public FeedItemServiceImpl(FeedItemRepository feedItemRepository, FeedHistoryService feedHistoryService) {
        this.feedItemRepository = feedItemRepository;
        this.feedHistoryService = feedHistoryService;
    }

    @Override
    public List<FeedItem> getFeedItems(User user) {
        Optional<FeedHistory> history = feedHistoryService.findByUserId(user.getId());
        List<String> historyFeedItemIds = new ArrayList<>();
        history.ifPresent(feedHistory -> historyFeedItemIds.addAll(feedHistory.getFeedItems()));
        List<FeedItem> feed = feedItemRepository.findFeedItemsByIdNotInAndDeleted(historyFeedItemIds, false);
        updateHistory(user.getId(), feed);
        return feed;
    }

    private void updateHistory(String userId, List<FeedItem> feedItems) {
        feedHistoryService.appendToHistory(userId, feedItems);
    }

    @Override
    public FeedItem addPortfolio(Portfolio portfolio) {
        return FeedItem.builder()
                .feedType(FeedType.PORTFOLIO)
                .parentType(ParentType.PORTFOLIO)
                .parentId(portfolio.getId())
                .username(portfolio.getUsername())
                .deleted(false).build();
    }

    @Override
    public FeedItem addTag(Tag tag) {
        return FeedItem.builder()
                .feedType(FeedType.TAG)
                .parentType(ParentType.TAG)
                .parentId(tag.getId())
                .username(tag.getCreatedBy())
                .deleted(false).build();
    }

    @Override
    public FeedItem save(FeedItem item) {
        return feedItemRepository.save(item);
    }


}
