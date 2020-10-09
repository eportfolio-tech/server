package tech.eportfolio.server.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
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

    private final RabbitTemplate rabbitTemplate;

    private final AmqpAdmin amqpAdmin;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    public ActivityServiceImpl(ActivityRepository activityRepository, FeedHistoryService feedHistoryService, RabbitTemplate rabbitTemplate, AmqpAdmin amqpAdmin) {
        this.activityRepository = activityRepository;
        this.feedHistoryService = feedHistoryService;
        this.rabbitTemplate = rabbitTemplate;
        this.amqpAdmin = amqpAdmin;
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

    @Override
    public Activity addPortfolio(Portfolio portfolio) {
        return activityRepository.save(Activity.builder()
                .feedType(FeedType.PORTFOLIO)
                .parentType(ParentType.PORTFOLIO)
                .parentId(portfolio.getId())
                .username(portfolio.getUsername())
                .deleted(false).build());
    }

    private void updateHistory(String userId, List<Activity> activities) {
        feedHistoryService.appendToHistory(userId, activities);
    }

    @Override
    public Activity addPortfolioUpdate(Portfolio portfolio) {
        return activityRepository.save(Activity.builder()
                .feedType(FeedType.UPDATE)
                .parentType(ParentType.PORTFOLIO)
                .parentId(portfolio.getId())
                .username(portfolio.getUsername())
                .deleted(false).build());
    }

    @Override
    public Activity addTag(Tag tag) {
        return activityRepository.save(Activity.builder()
                .feedType(FeedType.TAG)
                .parentType(ParentType.TAG)
                .parentId(tag.getId())
                .username(tag.getCreatedBy())
                .deleted(false).build());
    }

    @Override
    public Activity save(Activity item) {
        return activityRepository.save(item);
    }


}
