package tech.eportfolio.server.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import tech.eportfolio.server.model.UserFollow;

import java.util.List;

@Repository
public interface UserFollowRepository extends MongoRepository<UserFollow, String> {

    UserFollow findBySourceUsernameAndDestinationUsername(String sourceUsername, String destinationUsername);

    UserFollow findBySourceUsernameAndDestinationUsernameAndDeleted(String sourceUsername, String destinationUsername, boolean deleted);

    UserFollow findBySourceUsernameAndIdAndDeleted(String sourceUsername, String id, boolean deleted);

    List<UserFollow> findByDestinationUsernameAndDeleted(String destinationUsername, boolean deleted);

}
