package tech.eportfolio.server.listener;

import com.github.javafaker.Faker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import tech.eportfolio.server.model.User;

import tech.eportfolio.server.service.UserService;

/**
 * Create test account on start up
 */
@Component
public class CreateTestUserOnStartupListener {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${spring.security.user.name:test")
    private String username;

    @Value("${spring.security.user.password:test")
    private String password;

    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (userService.findByUsername("test").isEmpty()) {
            User test = User.builder()
                    .avatarUrl(Faker.instance().internet().avatar())
                    .username(username)
                    .email("")
                    .firstName("test")
                    .lastName("man")
                    .phone("(03)90355511")
                    .title("Good tester")
                    .password(password)
                    .build();
            userService.register(test, false);
            logger.info("Test account created: username {} password {}", test.getUsername(), test.getPassword());
        } else {
            logger.info("Test account already exist");
        }

    }
}