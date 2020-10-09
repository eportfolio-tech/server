package tech.eportfolio.server.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.eportfolio.server.common.exception.UserNotFoundException;
import tech.eportfolio.server.common.jsend.SuccessResponse;
import tech.eportfolio.server.model.Activity;
import tech.eportfolio.server.model.User;
import tech.eportfolio.server.service.FeedHistoryService;
import tech.eportfolio.server.service.UserFollowService;
import tech.eportfolio.server.service.UserService;

import java.util.List;

@Validated
@RestController
@RequestMapping("/feed")
public class FeedController {

    private final UserFollowService userFollowService;

    private final UserService userService;

    private final FeedHistoryService feedHistoryService;


    @Autowired
    public FeedController(UserFollowService userFollowService, UserService userService, FeedHistoryService feedHistoryService) {
        this.userFollowService = userFollowService;
        this.userService = userService;
        this.feedHistoryService = feedHistoryService;
    }

    @GetMapping("/")
    @ApiOperation(value = "", authorizations = {@Authorization(value = "JWT")})
    public ResponseEntity<SuccessResponse<List<Activity>>> getActives() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));
        List<Activity> activityList = userFollowService.getActivitiesFromQueue(user);
        // Append activity list to history so the user won't see the same activity again
        return new SuccessResponse<>("activities", activityList).toOk();
    }

}
