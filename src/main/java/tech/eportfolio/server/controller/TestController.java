package tech.eportfolio.server.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.eportfolio.server.common.exception.UserNotFoundException;
import tech.eportfolio.server.common.jsend.SuccessResponse;
import tech.eportfolio.server.common.utility.JWTTokenProvider;
import tech.eportfolio.server.model.Portfolio;
import tech.eportfolio.server.model.User;
import tech.eportfolio.server.model.UserPrincipal;
import tech.eportfolio.server.repository.UserRepository;
import tech.eportfolio.server.service.PortfolioService;
import tech.eportfolio.server.service.UserFollowService;
import tech.eportfolio.server.service.UserService;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/test")
public class TestController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private JWTTokenProvider jwtTokenProvider;

    private UserService userService;

    private PortfolioService portfolioService;

    private UserRepository userRepository;

    private UserFollowService userFollowService;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setUserFollowService(UserFollowService userFollowService) {
        this.userFollowService = userFollowService;
    }

    @Autowired
    public void setJwtTokenProvider(JWTTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Autowired
    public void setPortfolioService(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/notify-test")
    public ResponseEntity<SuccessResponse<Object>> notifyTest() {
        Optional<User> loginUser = userService.findByUsername("test");
        User user;
        if (loginUser.isEmpty()) {
            user = createTestUser();
        } else {
            user = loginUser.get();
        }
        userService.findAllActive().stream().filter(e -> portfolioService.findByUsername(e.getUsername()).isPresent()).limit(1).forEach(dest -> {
            if (userFollowService.findBySourceUsernameAndDestinationNameAndDeleted(user.getUsername(), dest.getUsername(), false).isEmpty()) {
                userFollowService.follow(user, dest.getUsername());
            }
            Optional<Portfolio> result = portfolioService.findByUsername(dest.getUsername());
            if (result.isPresent()) {
                Portfolio portfolio = result.get();
                logger.info("{} has notified test for updating", dest.getUsername());
                portfolioService.updateContent(portfolio, portfolio.getContent().toMap());
            }
        });
        return new SuccessResponse<>().toOk();
    }

    private User createTestUser() {
        User test = new User();
        test.setPassword("WhatSoEverWhoCare123");
        test.setUsername("test");
        test.setEmail("test@eportfolio.tech");
        test.setFirstName("test");
        test.setLastName("man");
        test.setPhone("(03)90355511");
        test.setTitle("Mr.");
        return userService.register(test, false);
    }

    @GetMapping("/quick-test")
    public ResponseEntity<SuccessResponse<Object>> quickTest() {
        return new SuccessResponse<>().toOk();
    }


    @DeleteMapping("/delete-test")
    public ResponseEntity<SuccessResponse<Object>> deleteTest() {
        String username = "test";
        User user = userService.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));
        userService.delete(user);
        // Generate URI to be embedded into email
        return new SuccessResponse<>().toOk();
    }


    @GetMapping("/login-as-test")
    public ResponseEntity<SuccessResponse<Object>> letMeLogIn() {
        Optional<User> loginUser = userService.findByUsername("test");
        User user;
        if (loginUser.isEmpty()) {
            user = createTestUser();
        } else {
            user = loginUser.get();
        }
        UserPrincipal userPrincipal = new UserPrincipal(user);
        Map<String, Object> response = new HashMap<>();
        response.put("access-token", jwtTokenProvider.generateAccessToken(userPrincipal));
        response.put("refresh-token", jwtTokenProvider.generateRefreshToken(userPrincipal));
        return new SuccessResponse<>(response).toOk();
    }
}
