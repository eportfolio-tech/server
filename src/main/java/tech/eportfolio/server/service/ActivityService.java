package tech.eportfolio.server.service;

import tech.eportfolio.server.model.Activity;
import tech.eportfolio.server.model.Portfolio;
import tech.eportfolio.server.model.Tag;
import tech.eportfolio.server.model.User;

import java.util.List;

public interface ActivityService {
    List<Activity> getFeedItems(User user);

    Activity addPortfolio(Portfolio portfolio);

    Activity addTag(Tag tag);

    Activity save(Activity activity);
}
