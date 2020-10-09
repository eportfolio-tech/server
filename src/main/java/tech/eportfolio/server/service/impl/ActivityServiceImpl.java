package tech.eportfolio.server.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.eportfolio.server.common.constant.FeedType;
import tech.eportfolio.server.common.constant.ParentType;
import tech.eportfolio.server.model.*;
import tech.eportfolio.server.repository.ActivityRepository;
import tech.eportfolio.server.service.ActivityService;
import tech.eportfolio.server.service.FeedHistoryService;

import java.util.ArrayList;
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
    public List<Activity> getFeedItems(User user) {
        Optional<FeedHistory> history = feedHistoryService.findByUserId(user.getId());
        List<String> historyFeedItemIds = new ArrayList<>();
        history.ifPresent(feedHistory -> historyFeedItemIds.addAll(feedHistory.getFeedItems()));
        List<Activity> feed = activityRepository.findFeedItemsByIdNotInAndDeleted(historyFeedItemIds, false);
        updateHistory(user.getId(), feed);
        return feed;
    }

    private void updateHistory(String userId, List<Activity> activities) {
        feedHistoryService.appendToHistory(userId, activities);
    }

    @Override
    public Activity addPortfolio(Portfolio portfolio) {
        return Activity.builder()
                .feedType(FeedType.PORTFOLIO)
                .parentType(ParentType.PORTFOLIO)
                .parentId(portfolio.getId())
                .username(portfolio.getUsername())
                .deleted(false).build();
    }

    @Override
    public Activity addTag(Tag tag) {
        return Activity.builder()
                .feedType(FeedType.TAG)
                .parentType(ParentType.TAG)
                .parentId(tag.getId())
                .username(tag.getCreatedBy())
                .deleted(false).build();
    }

    @Override
    public Activity save(Activity item) {
        return activityRepository.save(item);
    }


}
