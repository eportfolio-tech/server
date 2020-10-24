package tech.eportfolio.server.controller;

import com.github.javafaker.Faker;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import tech.eportfolio.server.common.constant.ActivityType;
import tech.eportfolio.server.dto.PortfolioDTO;
import tech.eportfolio.server.dto.UserDTO;
import tech.eportfolio.server.model.Activity;
import tech.eportfolio.server.model.Portfolio;
import tech.eportfolio.server.model.User;
import tech.eportfolio.server.service.PortfolioService;
import tech.eportfolio.server.service.TagService;
import tech.eportfolio.server.service.UserFollowService;
import tech.eportfolio.server.service.UserService;

import java.util.HashMap;

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
    private MockMvc mockMvc;
    @Autowired
    private UserService userService;
    @Autowired
    private PortfolioService portfolioService;
    @Autowired
    private UserFollowService userFollowService;
    @Autowired
    private TagService tagService;

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
        testUser.setAvatarUrl(Faker.instance().internet().avatar());
        userService.save(testUser);
        // Purge the remaining messages in the queue
        rabbitAdmin.purgeQueue("test");

        followingUserDTO = UserDTO.mock();
        followingUser = userService.register(userService.fromUserDTO(followingUserDTO), false);
        followingUser.setAvatarUrl(Faker.instance().internet().avatar());
        userService.save(followingUser);

        portfolioDTO = PortfolioDTO.mock();
        followingPortfolio = portfolioService.create(followingUser, portfolioService.fromPortfolioDTO(portfolioDTO));
        // drop table after each test
        mongoTemplate.dropCollection(Activity.class);
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
                .andExpect(jsonPath("$.data.activities[0].activityType").value(ActivityType.UPDATE.toString()))
                .andExpect(jsonPath("$.data.activities[0].portfolio").exists())
                .andExpect(jsonPath("$.data.activities[0].avatar").value(followingUser.getAvatarUrl()));
    }

    @Test
    @WithMockUser(username = "test")
    public void ifAddNewTagThenAppearInFeed() throws Exception {
        tagService.create("test");
        this.mockMvc.perform(get("/feed/")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
        ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.activities", hasSize(1)))
                .andExpect(jsonPath("$.data.activities[0].activityType").value(ActivityType.TAG.toString()))
                .andExpect(jsonPath("$.data.activities[0].tag").exists())
                .andExpect(jsonPath("$.data.activities[0].avatar").value(testUser.getAvatarUrl()));
    }

    @Test
    @WithMockUser(username = "test")
    public void ifAddNewPortfolioThenAppearInFeed() throws Exception {
        Portfolio portfolio = portfolioService.create(testUser, portfolioService.fromPortfolioDTO(portfolioDTO));
        portfolioService.updateContent(portfolio, new HashMap<>());
        this.mockMvc.perform(get("/feed/")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
        ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.activities", hasSize(1)))
                .andExpect(jsonPath("$.data.activities[0].activityType").value(ActivityType.PORTFOLIO.toString()))
                .andExpect(jsonPath("$.data.activities[0].portfolio").exists())
                .andExpect(jsonPath("$.data.activities[0].avatar").value(testUser.getAvatarUrl()));
    }

    @Test
    @WithMockUser(username = "test")
    public void UpdateShouldAppearFirstInFeed() throws Exception {
        Portfolio portfolio = portfolioService.create(testUser, portfolioService.fromPortfolioDTO(portfolioDTO));
        // Let test follows another user
        userFollowService.follow(testUser, followingUser.getUsername());
        // Update portfolio of the following user, this should send a message to test user
        portfolioService.updateContent(followingPortfolio, new HashMap<>());

        this.mockMvc.perform(get("/feed/")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
        ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.activities", hasSize(2)))
                .andExpect(jsonPath("$.data.activities[0].activityType").value(ActivityType.UPDATE.toString()))
                .andExpect(jsonPath("$.data.activities[0].portfolio").exists())
                .andExpect(jsonPath("$.data.activities[0].avatar").value(followingUser.getAvatarUrl()))
                .andExpect(jsonPath("$.data.activities[1].activityType").value(ActivityType.PORTFOLIO.toString()))
                .andExpect(jsonPath("$.data.activities[1].portfolio").exists())
                .andExpect(jsonPath("$.data.activities[1].avatar").value(testUser.getAvatarUrl()));
    }


    @After
    public void afterClass() {
        mongoTemplate.getDb().drop();
    }

}