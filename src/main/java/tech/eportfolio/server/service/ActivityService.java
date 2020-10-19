package tech.eportfolio.server.service;

import tech.eportfolio.server.model.Activity;
import tech.eportfolio.server.model.User;

import java.util.List;

public interface ActivityService {

    List<Activity> pull(User user, int tagCount, int portfolioCount);

    Activity save(Activity activity);

    List<Activity> saveAll(List<Activity> activities);
}
