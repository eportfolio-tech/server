package tech.eportfolio.server.controller;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import tech.eportfolio.server.dto.PortfolioDTO;
import tech.eportfolio.server.dto.UserDTO;
import tech.eportfolio.server.model.Portfolio;
import tech.eportfolio.server.model.User;
import tech.eportfolio.server.service.PortfolioService;
import tech.eportfolio.server.service.UserFollowService;
import tech.eportfolio.server.service.UserService;

import java.util.HashMap;
import java.util.Objects;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureDataMongo
public class FeedControllerTest {

    @Autowired
    private AmqpAdmin rabbitAdmin;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private CacheManager cacheManager;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserService userService;
    @Autowired
    private PortfolioService portfolioService;
    @Autowired
    private UserFollowService userFollowService;
    private User testUser;
    private User followingUser;
    private UserDTO followingUserDTO;
    private UserDTO testUserDTO;
    private PortfolioDTO portfolioDTO;
    private Portfolio followingPortfolio;

    @Before
    public void init() {
        testUserDTO = UserDTO.mock();
        testUserDTO.setUsername("test");
        testUser = userService.register(userService.fromUserDTO(testUserDTO), false);
        // Purge the remaining messages in the queue
        rabbitAdmin.purgeQueue("test");

        followingUserDTO = UserDTO.mock();
        followingUser = userService.register(userService.fromUserDTO(followingUserDTO), false);

        portfolioDTO = PortfolioDTO.mock();
        followingPortfolio = portfolioService.create(followingUser, portfolioService.fromPortfolioDTO(portfolioDTO));
    }

    @Test
    @WithMockUser(username = "test")
    public void ifFollowingUserUpdateTheirPortfolioThenSeeAUpdateInFeed() throws Exception {
        // Let test follows another user
        userFollowService.follow(testUser, followingUser.getUsername());
        // Update portfolio of the following user, this should send a message to test user
        portfolioService.updateContent(followingPortfolio, new HashMap<>());
        this.mockMvc.perform(get("/feed/")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
        ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.activities", hasSize(1)))
                .andExpect(jsonPath("$.data.activities[0].portfolio").exists());
    }


    @After
    public void afterClass() {
        cacheManager.getCacheNames().forEach(cacheName -> Objects.requireNonNull(cacheManager.getCache(cacheName)).clear());
        mongoTemplate.getDb().drop();
    }

}