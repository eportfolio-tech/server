package tech.eportfolio.server.job;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tech.eportfolio.server.common.constant.Visibility;
import tech.eportfolio.server.common.mock.EditorState;
import tech.eportfolio.server.common.utility.ParagraphProvider;
import tech.eportfolio.server.dto.PortfolioDTO;
import tech.eportfolio.server.dto.UserDTO;
import tech.eportfolio.server.model.Portfolio;
import tech.eportfolio.server.model.User;
import tech.eportfolio.server.repository.UserRepository;
import tech.eportfolio.server.service.AzureStorageService;
import tech.eportfolio.server.service.PortfolioService;
import tech.eportfolio.server.service.UserService;
import tech.eportfolio.server.service.VerificationService;

import java.util.Map;


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

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        int total = userRepository.countAllByDeleted(false);
        if (total < 1000) {
            createUserAndPortfolio();
        }
    }

    private void createUserAndPortfolio() {
        int batchSize = 50;
        for (int i = 0; i < batchSize; i++) {
            User user = userService.register(userService.fromUserDTO(UserDTO.mock()), false);
            verificationService.verify(user);
            user.setAvatarUrl(Faker.instance().internet().avatar());
            user = userRepository.save(user);
            logger.info("MockContentJob: User created {}", user.getUsername());
            String description = ParagraphProvider.sentence();
            String title = ParagraphProvider.sentence();
            if (title.length() > 50) {
                title = title.substring(0, 50);
            }
            Portfolio portfolio = portfolioService.create(user,
                    portfolioService.fromPortfolioDTO(PortfolioDTO.builder().
                            title(title).
                            description(description).
                            visibility(Visibility.PUBLIC).
                            build()));
            logger.info("MockContentJob: portfolio created {}", portfolio.getId());
            ObjectMapper objectMapper = new ObjectMapper();
            @SuppressWarnings("uncheck")
            Map<String, Object> map = objectMapper.convertValue(EditorState.generateState(), Map.class);
            portfolio = portfolioService.updateContent(portfolio, map);
            logger.info("MockContentJob: content added {}", portfolio.getContent());
        }
    }

}
