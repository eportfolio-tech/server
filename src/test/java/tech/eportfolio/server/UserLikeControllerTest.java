package tech.eportfolio.server;

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
import tech.eportfolio.server.service.PortfolioService;
import tech.eportfolio.server.service.UserLikeService;
import tech.eportfolio.server.service.UserService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureDataMongo
public class UserLikeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private PortfolioService portfolioService;

    @Autowired
    private UserLikeService userLikeService;

    private User testUser;

    private UserDTO testUserDTO;

    private PortfolioDTO testPortfolioDTO;

    private Portfolio testPortfolio;

    @Autowired
    MongoTemplate mongoTemplate;

    @Before
    public void init() {
        // Based on the assumption that a user can like and unlike him/her self, only one test user is created
        testUserDTO = UserDTO.mock();
        testUserDTO.setUsername("test");
        testUser = userService.register(userService.fromUserDTO(testUserDTO));

        testPortfolioDTO = PortfolioDTO.mock();
        testPortfolio = portfolioService.create(testUser, portfolioService.fromPortfolioDTO(testPortfolioDTO));

    }

    @Test
    @WithMockUser(username = "test")
    public void ifUserLikeALikedPortfolioThenReturn500() throws Exception {
        userLikeService.like(testUser, testPortfolio);

        this.mockMvc.perform(post("/portfolios/test/like")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .param("ownerUsername", testUser.getUsername())
        ).andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value("error"));
    }

    @Test
    @WithMockUser(username = "test")
    public void ifUserUnlikeAnUnlikedPortfolioThenReturn500() throws Exception {
        this.mockMvc.perform(delete("/portfolios/test/like")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .param("ownerUsername", testUser.getUsername())
        ).andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value("error"));
    }

    @After
    public void afterClass() {
        mongoTemplate.getDb().drop();
    }

}