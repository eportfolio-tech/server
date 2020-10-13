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
import java.util.Optional;

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
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("followers", userFollows);

        if (authentication instanceof AnonymousAuthenticationToken) {
            hashMap.put("followed", false);
        } else {
            Optional<UserFollow> loginUserFollower = userFollowerService.findBySourceUsernameAndDestinationNameAndDeleted(sourceUsername, destinationUsername, false);
            hashMap.put("followed", loginUserFollower.isPresent());
        }

        SuccessResponse<Object> response = new SuccessResponse<>();
        response.setData(hashMap);
        return response.toOk();
    }

    @GetMapping("/{username}/following")
    @ApiOperation(value = "", authorizations = {@Authorization(value = "JWT")})
    public ResponseEntity<SuccessResponse<Object>> findWhoIamFollowing(@PathVariable String username) {
        User user = userService.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));
        List<UserFollow> following = userFollowerService.findBySourceUser(user);
        SuccessResponse<Object> response = new SuccessResponse<>("following", following);
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
