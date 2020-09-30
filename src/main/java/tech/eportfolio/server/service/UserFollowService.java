package tech.eportfolio.server.service;

import tech.eportfolio.server.model.User;
import tech.eportfolio.server.model.UserFollow;

import java.util.List;
import java.util.Optional;

public interface UserFollowService {

    UserFollow follow(User sourceUser, String destinationUsername);

    UserFollow unfollow(User sourceUser, String destinationUsername);

    Optional<UserFollow> findBySourceUsernameAndDestinationName(String sourceUsername, String destinationUsername);

    Optional<UserFollow> findBySourceUsernameAndDestinationNameAndDeleted(String sourceUsername, String destinationUsername);

    UserFollow delete(UserFollow userFollow);

    List<UserFollow> findByDestinationUser(User destinationUser);

}
