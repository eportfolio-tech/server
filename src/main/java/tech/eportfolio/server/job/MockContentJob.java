package tech.eportfolio.server.job;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import tech.eportfolio.server.common.constant.Visibility;
import tech.eportfolio.server.common.mock.EditorState;
import tech.eportfolio.server.common.unsplash.Client;
import tech.eportfolio.server.common.utility.JWTTokenProvider;
import tech.eportfolio.server.common.utility.ParagraphProvider;
import tech.eportfolio.server.dto.PortfolioDTO;
import tech.eportfolio.server.dto.UserDTO;
import tech.eportfolio.server.model.Portfolio;
import tech.eportfolio.server.model.Tag;
import tech.eportfolio.server.model.User;
import tech.eportfolio.server.repository.UserRepository;
import tech.eportfolio.server.service.*;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;


/**
 * This job clean deleted users' container
 * If a user has been marked as delete,
 * its container will be removed after 7 days of grace period
 */

@Component
public class MockContentJob implements Job {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private UserService userService;
    @Autowired
    private AzureStorageService azureStorageService;
    @Autowired
    private VerificationService verificationService;
    @Autowired
    private PortfolioService portfolioService;
    @Autowired
    private UserRepository userRepository;
    private static final int THRESHOLD = 1000;
    @Autowired
    private TagService tagService;
    @Autowired
    private UserTagService userTagService;
    @Autowired
    private JWTTokenProvider jwtTokenProvider;
    @Autowired
    private Client unsplashClient;

    @Override
    public void execute(JobExecutionContext context) {
        int userCount = userRepository.countAllByDeleted(false);
        if (userCount < THRESHOLD) {
            List<User> users = new ArrayList<>();
            for (int i = 0; i < 50; i++) {
                User user = createUser();
                createPortfolio(user);
                users.add(user);
            }
            logger.info("MockContentJob: {} users created", users.size());
            List<Tag> tags = createTag(users, 20);
            assignTag(users, tags);
        } else {
            logger.info("MockContentJob: Skipped: User {} > THRESHOLD {}", userCount, THRESHOLD);
        }
    }

    private User createUser() {
        // Create an new user from mocked DTO
        User user = userService.register(userService.fromUserDTO(UserDTO.mock()), false);
        // verify user
        verificationService.verify(user);
        // Set user avatar
        user.setAvatarUrl(Faker.instance().internet().avatar());
        user = userRepository.save(user);
        // Log result
        logger.debug("User created {}", user.getUsername());
        return user;
    }

    private List<Tag> createTag(List<User> users, int count) {
        User user = users.get(ThreadLocalRandom.current().nextInt(users.size()));
        Authentication auth = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword(),
                Arrays.stream(user.getAuthorities()).map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
        SecurityContextHolder.getContext().setAuthentication(auth);

        List<Tag> tags = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            String tagName = Faker.instance().lorem().word();
            try {
                tags.add(tagService.create(tagName));
            } catch (RuntimeException ex) {
                logger.debug("MockContentJob: create tag {} failed: {}", tagName, ex.getMessage());
            }
        }
        logger.info("MockContentJob: {} tags created", tags.size());
        return tags;
    }

    private void assignTag(List<User> users, List<Tag> tags) {
        users.forEach(user -> {
            Authentication auth = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword(),
                    Arrays.stream(user.getAuthorities()).map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
            SecurityContextHolder.getContext().setAuthentication(auth);
            Set<Tag> toAssign = new HashSet<>();
            // For each user, we will assign 0 to 5 tags
            for (int i = 0; i < ThreadLocalRandom.current().nextInt(5); i++) {
                toAssign.add(tags.get(ThreadLocalRandom.current().nextInt(tags.size())));
            }
            userTagService.batchAssign(user, new ArrayList<>(toAssign));
        });
    }

    private Portfolio createPortfolio(User user) {
        String description = ParagraphProvider.sentence();
        // Generate sufficient long title
        String title = ParagraphProvider.sentence();
        if (title.length() > 50) {
            title = title.substring(0, 50);
        }

        String coverImage = null;
        try {
            coverImage = unsplashClient.randomImage().getJSONObject("urls").getString("regular");
        } catch (Exception exception) {
            coverImage = "https://images.unsplash.com/photo-1603536764976-e2d8a6d805fc?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=3167&q=80";
            logger.info("Failed to fetch image from unsplash {}", exception.getMessage());
        }
        // Create a portfolio
        Portfolio portfolio = portfolioService.create(user,
                portfolioService.fromPortfolioDTO(PortfolioDTO.builder().
                        title(title).
                        description(description).
                        visibility(Visibility.PUBLIC).
                        coverImage(coverImage).
                        build()));
        logger.debug("MockContentJob: portfolio created {}", portfolio.getId());
        // Create content
        ObjectMapper objectMapper = new ObjectMapper();
        @SuppressWarnings("unchecked")
        Map<String, Object> map = objectMapper.convertValue(EditorState.generateState(), Map.class);
        // Save content
        portfolio = portfolioService.updateContent(portfolio, map);
        logger.debug("MockContentJob: content added {}", portfolio.getContent());
        return portfolio;
    }
}
