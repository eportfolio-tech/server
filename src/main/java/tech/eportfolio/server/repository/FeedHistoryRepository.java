package tech.eportfolio.server.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import tech.eportfolio.server.model.FeedHistory;

import java.util.Optional;

@Repository
public interface FeedHistoryRepository extends MongoRepository<FeedHistory, String> {
    /**
     * Return feed history of a user by userId
     *
     * @param userId
     * @param deleted
     * @return
     */
    Optional<FeedHistory> findFeedHistoryByUserId(String userId, boolean deleted);

}
