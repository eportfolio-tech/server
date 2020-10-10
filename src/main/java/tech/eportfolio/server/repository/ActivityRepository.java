package tech.eportfolio.server.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import tech.eportfolio.server.model.Activity;

import java.util.List;

@Repository
public interface ActivityRepository extends MongoRepository<Activity, String> {

    /**
     * Find feed items by feed type
     *
     * @param findByActivityType portfolio | tag | update
     * @param deleted            deleted
     * @return
     */

    List<Activity> findFeedItemsByFeedTypeAndDeleted(String findByActivityType, boolean deleted);

    /**
     * Find feed items by parent type
     *
     * @param parentType
     * @param deleted    deleted
     * @return
     */
    List<Activity> findFeedItemByParentTypeAndDeleted(String parentType, boolean deleted);

    /**
     * Find feed items which are not in given feed history
     *
     * @param history
     * @param deleted deleted
     * @return
     */
    List<Activity> findFeedItemsByIdNotInAndDeleted(List<String> history, boolean deleted);

}
