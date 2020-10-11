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
import tech.eportfolio.server.common.exception.PortfolioNotFoundException;
import tech.eportfolio.server.common.exception.UserNotFoundException;
import tech.eportfolio.server.common.jsend.SuccessResponse;
import tech.eportfolio.server.model.Portfolio;
import tech.eportfolio.server.model.User;
import tech.eportfolio.server.model.UserLike;
import tech.eportfolio.server.service.PortfolioService;
import tech.eportfolio.server.service.UserLikeService;
import tech.eportfolio.server.service.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/portfolios/{ownerUsername}")
public class UserLikeController {

    private final PortfolioService portfolioService;

    private final UserService userService;

    private final UserLikeService userLikeService;

    @Autowired
    public UserLikeController(PortfolioService portfolioService, @Qualifier("UserServiceCacheImpl") UserService userService, UserLikeService userLikeService, PortfolioService portfolioRepository) {
        this.portfolioService = portfolioService;
        this.userService = userService;
        this.userLikeService = userLikeService;
    }

    @GetMapping("/like")
    public ResponseEntity<SuccessResponse<Object>> findWhoLikedThisPortfolio(@PathVariable String ownerUsername) {

        // Retrieve authentication from security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        Portfolio portfolio = portfolioService.findByUsername(ownerUsername).orElseThrow(() -> new PortfolioNotFoundException(ownerUsername));
        HashMap<String, Object> hashMap = new HashMap<>();
        List<UserLike> userLikes = userLikeService.findByPortfolio(portfolio);
        hashMap.put("user-like", userLikes);

        if (authentication instanceof AnonymousAuthenticationToken) {
            hashMap.put("liked", false);
        } else {
            Optional<UserLike> loginUserLike = userLikeService.findByPortfolioAndUsername(portfolio, username);
            hashMap.put("liked", loginUserLike.isPresent());
        }

        SuccessResponse<Object> response = new SuccessResponse<>();
        response.setData(hashMap);
        return response.toOk();
    }


    @PostMapping("/like")
    @ApiOperation(value = "", authorizations = {@Authorization(value = "JWT")})
    public ResponseEntity<SuccessResponse<Object>> likePortfolio(@PathVariable String ownerUsername) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));
        Portfolio portfolio = portfolioService.findByUsername(ownerUsername).orElseThrow(() -> new PortfolioNotFoundException(ownerUsername));
        userLikeService.like(user, portfolio);
        return new SuccessResponse<>().toOk();
    }

    @DeleteMapping("/like")
    @ApiOperation(value = "", authorizations = {@Authorization(value = "JWT")})
    public ResponseEntity<SuccessResponse<Object>> unlikePortfolio(@PathVariable String ownerUsername) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));
        Portfolio portfolio = portfolioService.findByUsername(ownerUsername).orElseThrow(() -> new PortfolioNotFoundException(ownerUsername));
        userLikeService.unlike(user, portfolio);
        return new SuccessResponse<>().toOk();
    }


}
