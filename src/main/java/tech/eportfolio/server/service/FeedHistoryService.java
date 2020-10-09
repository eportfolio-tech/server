package tech.eportfolio.server.service;

import tech.eportfolio.server.model.Activity;
import tech.eportfolio.server.model.FeedHistory;

import java.util.List;
import java.util.Optional;

public interface FeedHistoryService {
    Optional<FeedHistory> findByUserId(String userId);

    FeedHistory appendToHistory(String userId, List<Activity> activities);

    FeedHistory save(FeedHistory feedHistory);
}
