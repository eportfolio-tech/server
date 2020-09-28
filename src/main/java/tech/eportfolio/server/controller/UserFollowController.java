package tech.eportfolio.server.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import tech.eportfolio.server.common.exception.PortfolioNotFoundException;
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
@RequestMapping("/users/{followerName}")
public class UserFollowController {

    private final UserFollowService userFollowerService;

    private final UserService userService;


    @Autowired
    public UserFollowController(UserService userService, UserFollowService userFollowerService) {
        this.userService = userService;
        this.userFollowerService = userFollowerService;
    }

    @GetMapping("/follow")
    @ApiOperation(value = "", authorizations = {@Authorization(value = "JWT")})
    public ResponseEntity<SuccessResponse<Object>> findWhoFollowedThisUser(@PathVariable String followerName) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        User follower = userService.findByUsername(followerName).orElseThrow(() -> new UserNotFoundException(followerName));
        List<UserFollow> userFollows = userFollowerService.findByFollower(follower);
        Optional<UserFollow> loginUserFollower = userFollowerService.findByUsernameAndFollowerNameAndDeleted(username, followerName);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("followed", loginUserFollower.isPresent());
        hashMap.put("user-follow", userFollows);
        SuccessResponse<Object> response = new SuccessResponse<>();
        response.setData(hashMap);
        return response.toOk();
    }


    @PostMapping("/follow")
    @ApiOperation(value = "", authorizations = {@Authorization(value = "JWT")})
    public ResponseEntity<SuccessResponse<Object>> followUser(@PathVariable String followerName) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        // The condition when a user is following himself/herself
        if (username.equals(followerName)) {
            throw new SelfFollowCanNotBeAllowedException(username);
        }

        User user = userService.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));
        User owner = userService.findByUsername(followerName).orElseThrow(() -> new PortfolioNotFoundException(followerName));
        userFollowerService.follow(user, owner.getUsername());
        return new SuccessResponse<>().toOk();
    }

    @DeleteMapping("/follow")
    @ApiOperation(value = "", authorizations = {@Authorization(value = "JWT")})
    public ResponseEntity<SuccessResponse<Object>> unlikePortfolio(@PathVariable String followerName) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        // The condition when a user is following himself/herself
        if (username.equals(followerName)) {
            throw new SelfUnfollowCanNotBeAllowedException(username);
        }

        User user = userService.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));
        User owner = userService.findByUsername(followerName).orElseThrow(() -> new PortfolioNotFoundException(followerName));
        userFollowerService.unfollow(user, owner.getUsername());
        return new SuccessResponse<>().toOk();
    }


}
