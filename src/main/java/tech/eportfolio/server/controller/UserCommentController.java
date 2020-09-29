package tech.eportfolio.server.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import tech.eportfolio.server.common.exception.CommentNotFoundException;
import tech.eportfolio.server.common.exception.PortfolioNotFoundException;
import tech.eportfolio.server.common.exception.UserNotFoundException;
import tech.eportfolio.server.common.jsend.SuccessResponse;
import tech.eportfolio.server.dto.UserCommentOutputBody;
import tech.eportfolio.server.model.Portfolio;
import tech.eportfolio.server.model.User;
import tech.eportfolio.server.model.UserComment;
import tech.eportfolio.server.service.PortfolioService;
import tech.eportfolio.server.service.UserCommentService;
import tech.eportfolio.server.service.UserService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/portfolios/{ownerUsername}")
public class UserCommentController {

    private final PortfolioService portfolioService;

    private final UserService userService;

    private final UserCommentService userCommentService;

    private static final String COMMENTS = "comments";

    @Autowired
    public UserCommentController(PortfolioService portfolioService, UserService userService, UserCommentService userCommentService, PortfolioService portfolioRepository) {
        this.portfolioService = portfolioService;
        this.userService = userService;
        this.userCommentService = userCommentService;
    }

    @GetMapping("/comments")
    public ResponseEntity<SuccessResponse<List<UserCommentOutputBody>>> findWhoCommentedThisPortfolio(@PathVariable String ownerUsername) {
        Portfolio portfolio = portfolioService.findByUsername(ownerUsername).orElseThrow(() -> new PortfolioNotFoundException(ownerUsername));
        List<UserComment> userComments = userCommentService.findByPortfolio(portfolio);
        List<User> users = userCommentService.findUsersByUserComments(userComments);

        Map<String, String> map = users.stream().collect(Collectors.toMap(User::getUsername, User::getAvatarUrl));
        List<UserCommentOutputBody> result = userComments.stream()
                .map(userComment -> new UserCommentOutputBody(userComment, map.get(userComment.getUsername())))
                .collect(Collectors.toList());
        return new SuccessResponse<>(COMMENTS, result).toOk();
    }

    @PostMapping("/comments")
    @ApiOperation(value = "", authorizations = {@Authorization(value = "JWT")})
    public ResponseEntity<SuccessResponse<UserComment>> createComment(@PathVariable String ownerUsername, @RequestParam String content) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));
        Portfolio portfolio = portfolioService.findByUsername(ownerUsername).orElseThrow(() -> new PortfolioNotFoundException(ownerUsername));
        UserComment userComment = userCommentService.create(user, portfolio, content);
        return new SuccessResponse<>(COMMENTS, userComment).toOk();
    }

    @PostMapping("/comments/{commentId}/reply")
    @ApiOperation(value = "", authorizations = {@Authorization(value = "JWT")})
    public ResponseEntity<SuccessResponse<UserComment>> reply(@PathVariable String commentId, @RequestParam String content) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));
        UserComment inReplyTo = userCommentService.findById(commentId).orElseThrow(() -> new CommentNotFoundException(commentId));
        UserComment reply = userCommentService.reply(user, inReplyTo, content);
        return new SuccessResponse<>(COMMENTS, reply).toOk();
    }

    @DeleteMapping("/comments/{id}")
    @ApiOperation(value = "", authorizations = {@Authorization(value = "JWT")})
    public ResponseEntity<SuccessResponse<Object>> uncommentPortfolio(@PathVariable String ownerUsername, @PathVariable String id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserComment userComment = userCommentService.findByUsernameAndIdAndDeleted(username, id, false)
                .orElseThrow(() -> new CommentNotFoundException(id));
        userCommentService.delete(userComment);
        return new SuccessResponse<>().toOk();
    }

}
