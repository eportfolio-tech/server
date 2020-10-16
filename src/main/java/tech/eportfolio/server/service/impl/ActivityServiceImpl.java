package tech.eportfolio.server.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import tech.eportfolio.server.common.constant.FeedType;
import tech.eportfolio.server.model.Activity;
import tech.eportfolio.server.model.FeedHistory;
import tech.eportfolio.server.model.User;
import tech.eportfolio.server.repository.ActivityRepository;
import tech.eportfolio.server.service.ActivityService;
import tech.eportfolio.server.service.FeedHistoryService;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
public class ActivityServiceImpl implements ActivityService {

    private final ActivityRepository activityRepository;

    private final FeedHistoryService feedHistoryService;

    @Autowired
    public ActivityServiceImpl(ActivityRepository activityRepository, FeedHistoryService feedHistoryService) {
        this.activityRepository = activityRepository;
        this.feedHistoryService = feedHistoryService;
    }

    @Override
    public List<Activity> pull(User user, int tagCount, int portfolioCount) {
        Optional<FeedHistory> history = feedHistoryService.findByUserId(user.getId());
        List<String> historyFeedItemIds = new ArrayList<>();
        history.ifPresent(feedHistory -> historyFeedItemIds.addAll(feedHistory.getFeedItems()));
        List<Activity> feed = new LinkedList<>();
        feed.addAll(activityRepository.findByIdNotInAndFeedTypeAndDeleted(historyFeedItemIds, FeedType.PORTFOLIO, false, PageRequest.of(0, portfolioCount)));
        feed.addAll(activityRepository.findByIdNotInAndFeedTypeAndDeleted(historyFeedItemIds, FeedType.TAG, false, PageRequest.of(0, tagCount)));
        updateHistory(user.getId(), feed);
        return feed;
    }

    private void updateHistory(String userId, List<Activity> activities) {
        feedHistoryService.appendToHistory(userId, activities);
    }

    @Override
    public Activity save(Activity item) {
        return activityRepository.save(item);
    }

    @Override
    public List<Activity> saveAll(List<Activity> activities) {
        return activityRepository.saveAll(activities);
    }
}
