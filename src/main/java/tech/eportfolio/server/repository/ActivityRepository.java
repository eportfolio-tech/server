package tech.eportfolio.server.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import tech.eportfolio.server.common.constant.ActivityType;
import tech.eportfolio.server.model.Activity;

import java.util.List;

@Repository
public interface ActivityRepository extends MongoRepository<Activity, String> {

    List<Activity> findByIdNotInAndActivityTypeAndDeleted(List<String> ids, ActivityType activityType, boolean deleted, Pageable pageable);
}
