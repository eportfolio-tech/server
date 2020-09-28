package tech.eportfolio.server.service;

import tech.eportfolio.server.model.User;
import tech.eportfolio.server.model.UserFollow;

import java.util.List;
import java.util.Optional;

public interface UserFollowService {

    UserFollow follow(User user, String followerName);

    UserFollow unfollow(User user, String followerName);

    Optional<UserFollow> findByUsernameAndFollowerName(String username, String followerName);

    Optional<UserFollow> findByUsernameAndFollowerNameAndDeleted(String username, String followerName);

    UserFollow delete(UserFollow userLike);

    List<UserFollow> findByFollower(User user);

}
