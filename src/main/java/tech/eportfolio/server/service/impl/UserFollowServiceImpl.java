package tech.eportfolio.server.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.eportfolio.server.common.exception.UserFollowExistException;
import tech.eportfolio.server.common.exception.UserFollowNotExistException;
import tech.eportfolio.server.model.User;
import tech.eportfolio.server.model.UserFollow;
import tech.eportfolio.server.repository.UserFollowRepository;
import tech.eportfolio.server.service.UserFollowService;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class UserFollowServiceImpl implements UserFollowService {

    private final UserFollowRepository userFollowerRepository;

    @Autowired
    public UserFollowServiceImpl(UserFollowRepository userFollowerRepository) {
        this.userFollowerRepository = userFollowerRepository;
    }


    @Override
    public List<UserFollow> findByFollower(User user) {
        return userFollowerRepository.findByFollowerNameAndDeleted(user.getUsername(), false);
    }


    @Override
    public UserFollow follow(User user, String followerName) {
        Optional<UserFollow> follower = this.findByUsernameAndFollowerName(user.getUsername(), followerName);
        if (follower.isPresent()) {
            UserFollow userFollower = follower.get();
            if (userFollower.isDeleted()) {
                userFollower.setDeleted(false);
                userFollower.setCreatedDate(new Date());
                return userFollowerRepository.save(userFollower);
            } else {
                throw new UserFollowExistException(user.getUsername(), followerName);
            }
        }
        UserFollow newUserFollower = new UserFollow();
        newUserFollower.setUsername(user.getUsername());
        newUserFollower.setFollowerName(followerName);
        return userFollowerRepository.save(newUserFollower);
    }

    @Override
    public UserFollow unfollow(User user, String followerName) {
        String username = user.getUsername();
        UserFollow userLike = this.findByUsernameAndFollowerNameAndDeleted(user.getUsername(), followerName).orElseThrow(
                () -> new UserFollowNotExistException(username, followerName));
        return this.delete(userLike);
    }

    @Override
    public Optional<UserFollow> findByUsernameAndFollowerName(String username, String followerName) {
        return Optional.ofNullable(userFollowerRepository.findByUsernameAndFollowerName(username, followerName));
    }

    @Override
    public Optional<UserFollow> findByUsernameAndFollowerNameAndDeleted(String username, String followerName) {
        return Optional.ofNullable(userFollowerRepository.findByUsernameAndFollowerNameAndDeleted(username, followerName, false));
    }

    @Override
    public UserFollow delete(UserFollow userFollower) {
        userFollower.setDeleted(true);
        userFollowerRepository.save(userFollower);
        return userFollower;
    }
}
