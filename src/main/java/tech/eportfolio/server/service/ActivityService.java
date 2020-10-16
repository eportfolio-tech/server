package tech.eportfolio.server.service;

import org.springframework.data.domain.PageRequest;
import tech.eportfolio.server.model.Activity;
import tech.eportfolio.server.model.User;

import java.util.List;

public interface ActivityService {

    Activity save(Activity activity);

    List<Activity> saveAll(List<Activity> activities);

    List<Activity> pull(User user, PageRequest pageRequest);
}
