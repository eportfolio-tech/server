package tech.eportfolio.server.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import tech.eportfolio.server.common.exception.SelfFollowCanNotBeAllowedException;
import tech.eportfolio.server.common.exception.SelfUnfollowCanNotBeAllowedException;
import tech.eportfolio.server.common.exception.UserNotFoundException;
import tech.eportfolio.server.common.jsend.SuccessResponse;
import tech.eportfolio.server.model.User;
import tech.eportfolio.server.model.UserFollow;
import tech.eportfolio.server.service.UserFollowService;
import tech.eportfolio.server.service.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserFollowController {

    private final UserFollowService userFollowerService;

    private final UserService userService;


    @Autowired
    public UserFollowController(@Qualifier("UserServiceCacheImpl") UserService userService, UserFollowService userFollowerService) {
        this.userService = userService;
        this.userFollowerService = userFollowerService;
    }

    @GetMapping("/{destinationUsername}/followers")
    @ApiOperation(value = "", authorizations = {@Authorization(value = "JWT")})
    public ResponseEntity<SuccessResponse<Object>> findWhoFollowedThisUser(@PathVariable String destinationUsername) {

        // Retrieve authentication from security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String sourceUsername = authentication.getName();

        User follower = userService.findByUsername(destinationUsername).orElseThrow(() -> new UserNotFoundException(destinationUsername));
        List<UserFollow> userFollows = userFollowerService.findByDestinationUser(follower);

        List<User> sourceUsers = userService.findByIdIn(userFollows.stream().map(UserFollow::getSourceUserId).collect(Collectors.toList()));

        Map<String, Object> map = sourceUsers.stream().collect(Collectors.toMap(User::getUsername,
                e -> {
                    HashMap<String, Object> hashmap = new HashMap<>();

                    hashmap.put("firstName", e.getFirstName());
                    hashmap.put("lastName", e.getLastName());
                    hashmap.put("avatarUrl", e.getAvatarUrl());

                    return hashmap;
                }));

        List<Object> result = userFollows.stream()
                .map(userFollow -> {
                    HashMap<String, Object> hashmap = new HashMap<>();

                    hashmap.put("user_follow", userFollow);
                    hashmap.put("source_user", map.get(userFollow.getSourceUsername()));

                    return hashmap;

                }).collect(Collectors.toList());

        HashMap<String, Object> hashmap = new HashMap<>();
        hashmap.put("followers", result);
        hashmap.put("firstName", follower.getFirstName());
        hashmap.put("lastName", follower.getLastName());
        hashmap.put("avatarUrl", follower.getAvatarUrl());

        if (authentication instanceof AnonymousAuthenticationToken) {
            hashmap.put("followed", false);
        } else {
            Optional<UserFollow> loginUserFollower = userFollowerService.findBySourceUsernameAndDestinationNameAndDeleted(sourceUsername, destinationUsername, false);
            hashmap.put("followed", loginUserFollower.isPresent());
        }

        SuccessResponse<Object> response = new SuccessResponse<>();
        response.setData(hashmap);
        return response.toOk();
    }

    @GetMapping("/{username}/following")
    @ApiOperation(value = "", authorizations = {@Authorization(value = "JWT")})
    public ResponseEntity<SuccessResponse<Object>> findWhoIamFollowing(@PathVariable String username) {
        User user = userService.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));
        List<UserFollow> followings = userFollowerService.findBySourceUser(user);

        List<User> destinationUsers = userService.findByUsernameIn(followings.stream().map(UserFollow::getDestinationUsername).collect(Collectors.toList()));

        Map<String, Object> map = destinationUsers.stream().collect(Collectors.toMap(User::getUsername,
                e -> {
                    HashMap<String, Object> hashmap = new HashMap<>();

                    hashmap.put("firstName", e.getFirstName());
                    hashmap.put("lastName", e.getLastName());
                    hashmap.put("avatarUrl", e.getAvatarUrl());

                    return hashmap;
                }));

        List<Object> result = followings.stream()
                .map(following -> {
                    HashMap<String, Object> hashmap = new HashMap<>();

                    hashmap.put("user_follow", following);
                    hashmap.put("destination_user", map.get(following.getSourceUsername()));

                    return hashmap;

                }).collect(Collectors.toList());

        HashMap<String, Object> hashmap = new HashMap<>();
        hashmap.put("followings", result);
        hashmap.put("firstName", user.getFirstName());
        hashmap.put("lastName", user.getLastName());
        hashmap.put("avatarUrl", user.getAvatarUrl());

        SuccessResponse<Object> response = new SuccessResponse<>();
        response.setData(hashmap);
        return response.toOk();
    }


    @PostMapping("/{destinationUsername}/followers")
    @ApiOperation(value = "", authorizations = {@Authorization(value = "JWT")})
    public ResponseEntity<SuccessResponse<Object>> followUser(@PathVariable String destinationUsername) {
        String sourceUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        // The condition when a user is following himself/herself
        if (sourceUsername.equals(destinationUsername)) {
            throw new SelfFollowCanNotBeAllowedException(sourceUsername);
        }

        User sourceUser = userService.findByUsername(sourceUsername).orElseThrow(() -> new UserNotFoundException(sourceUsername));
        User destinationUser = userService.findByUsername(destinationUsername).orElseThrow(() -> new UserNotFoundException(destinationUsername));
        userFollowerService.follow(sourceUser, destinationUser.getUsername());
        return new SuccessResponse<>().toCreated();
    }

    @DeleteMapping("/{destinationUsername}/followers")
    @ApiOperation(value = "", authorizations = {@Authorization(value = "JWT")})
    public ResponseEntity<SuccessResponse<Object>> unfollowUser(@PathVariable String destinationUsername) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        // The condition when a user is following himself/herself
        if (username.equals(destinationUsername)) {
            throw new SelfUnfollowCanNotBeAllowedException(username);
        }

        User sourceUser = userService.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));
        User destinationUser = userService.findByUsername(destinationUsername).orElseThrow(() -> new UserNotFoundException(destinationUsername));
        userFollowerService.unfollow(sourceUser, destinationUser.getUsername());
        return new SuccessResponse<>().toOk();
    }


}
