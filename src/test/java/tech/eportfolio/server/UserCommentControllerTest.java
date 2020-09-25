package tech.eportfolio.server;

import com.github.javafaker.Faker;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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
import tech.eportfolio.server.dto.PortfolioDTO;
import tech.eportfolio.server.dto.UserDTO;
import tech.eportfolio.server.model.Portfolio;
import tech.eportfolio.server.model.User;
import tech.eportfolio.server.model.UserComment;
import tech.eportfolio.server.service.PortfolioService;
import tech.eportfolio.server.service.UserCommentService;
import tech.eportfolio.server.service.UserService;

import static org.hamcrest.Matchers.hasSize;
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
public class UserCommentControllerTest {

    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserService userService;
    @Autowired
    private PortfolioService portfolioService;
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

        testPortfolioDTO = PortfolioDTO.mock();
        testPortfolio = portfolioService.create(testUser, portfolioService.fromPortfolioDTO(testPortfolioDTO));
        portfolioService.create(secondUser, portfolioService.fromPortfolioDTO(PortfolioDTO.mock()));

    }

    @Test
    @WithMockUser(username = "test")
    public void ifPostCommentSuccessThenShouldReturn200() throws Exception {
        String comment = Faker.instance().gameOfThrones().quote();

        this.mockMvc.perform(post("/portfolios/test/comments/")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .param("ownerUsername", "test")
                .param("comment", comment)
        ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.user-comment").isNotEmpty());
    }

    @Test
    @WithMockUser(username = "test")
    public void ifGetCommentsSuccessThenShouldReturnComments() throws Exception {
        userCommentService.create(testUser, testPortfolio, Faker.instance().gameOfThrones().quote());
        userCommentService.create(testUser, testPortfolio, Faker.instance().gameOfThrones().quote());
        userCommentService.create(testUser, testPortfolio, Faker.instance().gameOfThrones().quote());

        this.mockMvc.perform(get("/portfolios/test/comments/")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .param("ownerUsername", "test")
        ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.user-comment", hasSize(3)));
    }

    @Test
    @WithMockUser(username = "test")
    public void ifDeleteCommentNotFoundCommentThenReturn404() throws Exception {
        String id = RandomStringUtils.randomAlphabetic(8);

        this.mockMvc.perform(delete("/portfolios/test/comments/" + id)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .param("ownerUsername", "test")
                .param("id", id)
        ).andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("fail"));
    }

    @Test
    @WithMockUser(username = "test")
    public void ifDeletedCommentSuccessThenReturn200() throws Exception {
        String comment = RandomStringUtils.randomAlphabetic(8);
        UserComment userComment = userCommentService.create(testUser, testPortfolio, comment);

        this.mockMvc.perform(delete("/portfolios/test/comments/" + userComment.getId())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .param("ownerUsername", "test")
                .param("id", userComment.getId())
        ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    @WithMockUser(username = "test")
    public void ifDoubleDeleteCommentThenReturn404() throws Exception {
        String comment = RandomStringUtils.randomAlphabetic(8);
        UserComment userComment = userCommentService.create(testUser, testPortfolio, comment);
        userCommentService.delete(userComment);

        this.mockMvc.perform(delete("/portfolios/test/comments/" + userComment.getId())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .param("ownerUsername", "test")
                .param("id", userComment.getId())
        ).andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("fail"));
    }

    @Test
    @WithMockUser(username = "test")
    public void ifDeleteOthersCommentThenReturn404() throws Exception {
        String comment = RandomStringUtils.randomAlphabetic(8);
        UserComment userComment = userCommentService.create(secondUser, testPortfolio, comment);

        this.mockMvc.perform(delete("/portfolios/test/comments/" + userComment.getId())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .param("ownerUsername", "test")
                .param("id", userComment.getId())
        ).andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("fail"));
    }


    @After
    public void afterClass() {
        mongoTemplate.getDb().drop();
    }

}