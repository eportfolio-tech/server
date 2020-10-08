package tech.eportfolio.server.service;

import tech.eportfolio.server.model.FeedItem;
import tech.eportfolio.server.model.Portfolio;
import tech.eportfolio.server.model.Tag;
import tech.eportfolio.server.model.User;

import java.util.List;

public interface FeedItemService {
    List<FeedItem> getFeedItems(User user);

    FeedItem addPortfolio(Portfolio portfolio);

    FeedItem addTag(Tag tag);

    FeedItem save(FeedItem feedItem);
}
