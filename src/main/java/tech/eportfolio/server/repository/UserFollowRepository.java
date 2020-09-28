package tech.eportfolio.server.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import tech.eportfolio.server.model.UserFollow;

import java.util.List;

@Repository
public interface UserFollowRepository extends MongoRepository<UserFollow, String> {

    UserFollow findByUsernameAndFollowerName(String username, String followerName);

    UserFollow findByUsernameAndFollowerNameAndDeleted(String username, String followerName, boolean deleted);

    UserFollow findByUsernameAndIdAndDeleted(String username, String id, boolean deleted);

    List<UserFollow> findByFollowerNameAndDeleted(String follower, boolean deleted);

}
