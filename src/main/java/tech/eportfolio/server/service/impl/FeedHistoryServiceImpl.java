package tech.eportfolio.server.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.eportfolio.server.model.FeedHistory;
import tech.eportfolio.server.model.FeedItem;
import tech.eportfolio.server.repository.FeedHistoryRepository;
import tech.eportfolio.server.service.FeedHistoryService;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FeedHistoryServiceImpl implements FeedHistoryService {

    private final FeedHistoryRepository feedHistoryRepository;

    @Autowired
    public FeedHistoryServiceImpl(FeedHistoryRepository feedHistoryRepository) {
        this.feedHistoryRepository = feedHistoryRepository;
    }

    @Override
    public Optional<FeedHistory> findByUserId(String userId) {
        return feedHistoryRepository.findFeedHistoryByUserId(userId, false);
    }

    @Override
    public FeedHistory appendToHistory(String userId, List<FeedItem> feedItems) {
        FeedHistory feedHistory = feedHistoryRepository.findFeedHistoryByUserId(userId, false).
                orElse(FeedHistory.builder().userId(userId).build());
        Set<String> items = feedHistory.getFeedItems();
        // add ids of newly pushed feed items to the history set
        items.addAll(feedItems.stream().map(FeedItem::getId).collect(Collectors.toSet()));
        feedHistory.setFeedItems(items);

        // save the history
        feedHistoryRepository.save(feedHistory);

        return null;
    }

    @Override
    public FeedHistory save(FeedHistory feedHistory) {
        return feedHistoryRepository.save(feedHistory);
    }
}
