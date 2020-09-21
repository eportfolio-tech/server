package tech.eportfolio.server.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.eportfolio.server.common.exception.PortfolioNotFoundException;
import tech.eportfolio.server.common.exception.UserNotFoundException;
import tech.eportfolio.server.common.jsend.SuccessResponse;
import tech.eportfolio.server.model.UserLike;
import tech.eportfolio.server.repository.mongodb.PortfolioRepository;
import tech.eportfolio.server.service.PortfolioService;
import tech.eportfolio.server.service.SocialityService;
import tech.eportfolio.server.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/portfolios/{username}")
public class SocialityController {

    private final PortfolioService portfolioService;

    private final UserService userService;

    private final SocialityService socialityService;

    private final PortfolioRepository portfolioRepository;

    @Autowired
    public SocialityController(PortfolioService portfolioService, UserService userService, SocialityService socialityService, PortfolioRepository portfolioRepository) {
        this.portfolioService = portfolioService;
        this.userService = userService;
        this.socialityService = socialityService;
        this.portfolioRepository = portfolioRepository;
    }

    @GetMapping("/like")
    @ApiOperation(value = "", authorizations = {@Authorization(value = "JWT")})
    public ResponseEntity<SuccessResponse<List<UserLike>>> findAllLikedPortfolios(@PathVariable String username) {
        userService.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));
        List<UserLike> userLikes = socialityService.findAllLiked(username);
        return new SuccessResponse<>("user_like", userLikes).toOk();
    }

    @PostMapping("/like")
    @ApiOperation(value = "", authorizations = {@Authorization(value = "JWT")})
    public ResponseEntity<SuccessResponse<UserLike>> likePortfolio(@PathVariable String username, @RequestParam String portfolioId) {
        userService.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));
        portfolioRepository.findById(portfolioId).orElseThrow(() -> new PortfolioNotFoundException(portfolioId));
        UserLike userLike = socialityService.likePortfolio(username, portfolioId);

        return new SuccessResponse<>("user_like", userLike).toOk();
    }

    @DeleteMapping("/like")
    @ApiOperation(value = "", authorizations = {@Authorization(value = "JWT")})
    public ResponseEntity<SuccessResponse<UserLike>> unlikePortfolio(@PathVariable String username, @RequestParam String portfolioId) {
        userService.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));
        portfolioRepository.findById(portfolioId).orElseThrow(() -> new PortfolioNotFoundException(portfolioId));
        UserLike userLike = socialityService.unlikePortfolio(username, portfolioId);

        return new SuccessResponse<>("user_like", userLike).toOk();
    }


}
