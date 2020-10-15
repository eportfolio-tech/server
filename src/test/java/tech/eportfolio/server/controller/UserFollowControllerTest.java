package tech.eportfolio.server.controller;

import com.github.javafaker.Faker;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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
import tech.eportfolio.server.service.UserCommentService;
import tech.eportfolio.server.service.UserFollowService;
import tech.eportfolio.server.service.UserService;

import java.util.Objects;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureDataMongo
public class UserFollowControllerTest {

    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    private CacheManager cacheManager;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserService userService;
    @Autowired
    private UserFollowService userFollowService;
    @Autowired
    private UserCommentService userCommentService;
    private User testUser;
    private User secondUser;
    private UserDTO testUserDTO;
    private PortfolioDTO testPortfolioDTO;
    private Portfolio testPortfolio;

    @Before
    public void init() {

        testUserDTO = UserDTO.mock();
        testUserDTO.setUsername("test");

        testUser = userService.register(userService.fromUserDTO(testUserDTO), false);
        secondUser = userService.register(userService.fromUserDTO(UserDTO.mock()), false);
    }

    @Test
    @WithMockUser(username = "test")
    public void ifFollowedAndGetFollowersThenReturn200AndTrueForFollowed() throws Exception {
        userFollowService.follow(testUser, testUser.getUsername());
        this.mockMvc.perform(get("/users/test/followers")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
        ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.followed").value("true"))
                .andExpect(jsonPath("$.data.followers", hasSize(1)));
    }

    @Test
    @WithMockUser(username = "test")
    public void ifNotFollowedThenGetFollowersThenReturn200AndFalseForFollowed() throws Exception {
        userFollowService.follow(secondUser, testUser.getUsername());
        this.mockMvc.perform(get("/users/test/followers")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
        ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.followed").value("false"))
                .andExpect(jsonPath("$.data.followers", hasSize(1)));
    }

    @Test
    public void ifNotLoginThenThenReturn200AndGetAllFollowers() throws Exception {
        userFollowService.follow(secondUser, testUser.getUsername());
        this.mockMvc.perform(get("/users/test/followers")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
        ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.followed").value("false"))
                .andExpect(jsonPath("$.data.followers", hasSize(1)));
    }

    @Test
    public void ifNotLoginThenGetFollowingThenReturn401() throws Exception {
        this.mockMvc.perform(get("/users/test/following")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
        ).andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value("fail"));
    }

    @Test
    @WithMockUser(username = "test")
    public void ifLoginThenGetFollowingThenReturnAllFollowingUsers() throws Exception {
        userFollowService.follow(testUser, secondUser.getUsername());
        this.mockMvc.perform(get("/users/test/following")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
        ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.followings", hasSize(1)));
    }

    @Test
    @WithMockUser(username = "test")
    public void ifAttemptToSelfFollowThenReturn500() throws Exception {
        this.mockMvc.perform(post("/users/test/followers")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
        ).andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value("error"));
    }

    @Test
    @WithMockUser(username = "test")
    public void ifFollowExistUserThenReturn200() throws Exception {
        this.mockMvc.perform(post("/users/{%s}/followers", secondUser.getUsername())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
        ).andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("success"));
        assertTrue(userFollowService.findBySourceUsernameAndDestinationNameAndDeleted(testUser.getUsername(), secondUser.getUsername(), false).isPresent());
    }

    @Test
    @WithMockUser(username = "test")
    public void ifFollowNonExistUserThenReturn404() throws Exception {
        this.mockMvc.perform(post("/users/{%s}/followers", Faker.instance().name().fullName())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
        ).andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("fail"));
    }

    @Test
    @WithMockUser(username = "test")
    public void ifFollowTheSameUserAgainThenReturn409() throws Exception {
        userFollowService.follow(testUser, secondUser.getUsername());
        this.mockMvc.perform(post("/users/{%s}/followers", secondUser.getUsername())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
        ).andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value("fail"));
    }

    @Test
    @WithMockUser(username = "test")
    public void ifCancelExistingFollowThenReturn200() throws Exception {
        userFollowService.follow(testUser, secondUser.getUsername());
        this.mockMvc.perform(delete("/users/{%s}/followers", secondUser.getUsername())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
        ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }


    @Test
    @WithMockUser(username = "test")
    public void ifCancelNonExistFollowThenReturn404() throws Exception {
        this.mockMvc.perform(delete("/users/{%s}/followers", Faker.instance().name().fullName())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
        ).andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("fail"));
    }


    @After
    public void afterClass() {
        cacheManager.getCacheNames().forEach(cacheName -> Objects.requireNonNull(cacheManager.getCache(cacheName)).clear());
        mongoTemplate.getDb().drop();
    }

}