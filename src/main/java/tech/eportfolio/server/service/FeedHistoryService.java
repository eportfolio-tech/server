package tech.eportfolio.server.service;

import tech.eportfolio.server.model.FeedHistory;
import tech.eportfolio.server.model.FeedItem;

import java.util.List;
import java.util.Optional;

public interface FeedHistoryService {
    Optional<FeedHistory> findByUserId(String userId);

    FeedHistory appendToHistory(String userId, List<FeedItem> feedItems);

    FeedHistory save(FeedHistory feedHistory);
}
