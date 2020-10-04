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
    public List<UserFollow> findBySourceUser(User sourceUser) {
        return userFollowerRepository.findBySourceUsernameAndDeleted(sourceUser.getUsername(), false);
    }

    @Override
    public List<UserFollow> findByDestinationUser(User destinationUser) {
        return userFollowerRepository.findByDestinationUsernameAndDeleted(destinationUser.getUsername(), false);
    }


    @Override
    public UserFollow follow(User sourceUser, String destinationUsername) {
        Optional<UserFollow> follower = this.findBySourceUsernameAndDestinationNameAndDeleted(sourceUser.getUsername(), destinationUsername, false);
        if (follower.isPresent()) {
            UserFollow userFollower = follower.get();
            if (userFollower.isDeleted()) {
                userFollower.setDeleted(false);
                userFollower.setCreatedDate(new Date());
                return userFollowerRepository.save(userFollower);
            } else {
                throw new UserFollowExistException(sourceUser.getUsername(), destinationUsername);
            }
        }
        UserFollow newUserFollower = new UserFollow();
        newUserFollower.setSourceUsername(sourceUser.getUsername());
        newUserFollower.setDestinationUsername(destinationUsername);
        newUserFollower.setSourceUserId(sourceUser.getId());
        return userFollowerRepository.save(newUserFollower);
    }

    @Override
    public UserFollow unfollow(User sourceUser, String destinationUsername) {
        String username = sourceUser.getUsername();
        UserFollow userFollow = this.findBySourceUsernameAndDestinationNameAndDeleted(sourceUser.getUsername(), destinationUsername, false).orElseThrow(
                () -> new UserFollowNotExistException(username, destinationUsername));
        return this.delete(userFollow);
    }


    @Override
    public Optional<UserFollow> findBySourceUsernameAndDestinationNameAndDeleted(String sourceUser, String destinationUsername, boolean deleted) {
        return Optional.ofNullable(userFollowerRepository.findBySourceUsernameAndDestinationUsernameAndDeleted(sourceUser, destinationUsername, deleted));
    }

    @Override
    public UserFollow delete(UserFollow userFollow) {
        userFollow.setDeleted(true);
        userFollowerRepository.save(userFollow);
        return userFollow;
    }
}
