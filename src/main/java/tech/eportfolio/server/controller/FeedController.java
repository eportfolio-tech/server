package tech.eportfolio.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.eportfolio.server.common.constant.ParentType;
import tech.eportfolio.server.common.exception.UserNotFoundException;
import tech.eportfolio.server.common.jsend.SuccessResponse;
import tech.eportfolio.server.model.Activity;
import tech.eportfolio.server.model.Portfolio;
import tech.eportfolio.server.model.Tag;
import tech.eportfolio.server.model.User;
import tech.eportfolio.server.service.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Validated
@RestController
@RequestMapping("/feed")
public class FeedController {

    private final UserFollowService userFollowService;

    private final UserService userService;

    private final PortfolioService portfolioService;

    private final TagService tagService;

    private final ObjectMapper objectMapper;

    private final ActivityService activityService;

    @Autowired
    public FeedController(UserFollowService userFollowService, UserService userService, PortfolioService portfolioService, TagService tagService, ObjectMapper objectMapper, ActivityService activityService) {
        this.userFollowService = userFollowService;
        this.userService = userService;
        this.portfolioService = portfolioService;
        this.tagService = tagService;
        this.objectMapper = objectMapper;
        this.activityService = activityService;
    }

    @GetMapping("/")
    @ApiOperation(value = "", authorizations = {@Authorization(value = "JWT")})
    public ResponseEntity<SuccessResponse<List<Map<String, Object>>>> getActivities() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));

        List<Activity> feed = new ArrayList<>();
        List<Map<String, Object>> result = new ArrayList<>();
        feed.addAll(activityService.pull(user, 3, 10));
        feed.addAll(userFollowService.getActivitiesFromQueue(user));

        List<String> usernames = feed.stream().map(Activity::getUsername).collect(Collectors.toList());
        Map<String, User> userMap = userService.findByUsernameIn(usernames).stream().collect(Collectors.toMap(User::getUsername, Function.identity()));

        List<String> tagIds = feed.stream().filter(e -> e.getParentType().equals(ParentType.TAG)).map(Activity::getParentId).collect(Collectors.toList());
        Map<String, Tag> tagMap = tagService.findByIdIn(tagIds).stream().collect(Collectors.toMap(Tag::getId, Function.identity()));

        List<String> portfolioIds = feed.stream().filter(e -> e.getParentType().equals(ParentType.PORTFOLIO)).map(Activity::getParentId).collect(Collectors.toList());
        Map<String, Portfolio> portfolioMap = portfolioService.findByIdIn(portfolioIds).stream().collect(Collectors.toMap(Portfolio::getId, Function.identity()));

        feed.forEach(e -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = objectMapper.convertValue(e, Map.class);
            map.put("avatar", userMap.get(e.getUsername()).getAvatarUrl());
            if (e.getParentType().equals(ParentType.PORTFOLIO)) {
                @SuppressWarnings("unchecked")
                Map<String, Object> portFolioMap = objectMapper.convertValue(portfolioMap.get(e.getParentId()), Map.class);
                // Removes portfolio content to reduce json size
                portFolioMap.remove("content");
                // Add portfolio detail to the map
                map.put(ParentType.PORTFOLIO.toString().toLowerCase(), portFolioMap);
                result.add(map);
            } else if (e.getParentType().equals(ParentType.TAG)) {
                // Add portfolio detail to the map
                map.put(ParentType.TAG.toString().toLowerCase(), tagMap.get(e.getParentId()));
                result.add(map);
            }

        });
        // Append activity list to history so the user won't see the same activity again
        return new SuccessResponse<>("activities", result).toOk();
    }

}
