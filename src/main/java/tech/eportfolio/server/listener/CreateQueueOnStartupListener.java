package tech.eportfolio.server.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import tech.eportfolio.server.model.User;
import tech.eportfolio.server.repository.UserFollowRepository;
import tech.eportfolio.server.service.UserFollowService;
import tech.eportfolio.server.service.UserService;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Reconstruct missing queue and binding on start up
 */
@Component
public class CreateQueueOnStartupListener {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private UserService userService;

    private UserFollowService userFollowService;

    private UserFollowRepository userFollowRepository;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setUserFollowRepository(UserFollowRepository userFollowRepository) {
        this.userFollowRepository = userFollowRepository;
    }

    @Autowired
    public void setUserFollowService(UserFollowService userFollowService) {
        this.userFollowService = userFollowService;
    }

    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent event) {
        List<User> users = userService.findAllActive();
        AtomicInteger queueCounter = new AtomicInteger();
        users.parallelStream().filter(Objects::nonNull).forEach(user -> {
            if (!userFollowService.hasQueue(user.getUsername())) {
                userFollowService.createQueueAndExchange(user.getUsername());
                queueCounter.incrementAndGet();
            }
        });
        logger.info("Recreated {} message queues on start up", queueCounter.get());

        AtomicInteger bindingCounter = new AtomicInteger();
        userFollowRepository.findAll().parallelStream().filter(Objects::nonNull).forEach(e -> {
            userFollowService.bind(e.getSourceUsername(), e.getDestinationUsername());
            bindingCounter.incrementAndGet();
        });
        logger.info("Recreated {} bindings on start up", bindingCounter.get());

    }
}