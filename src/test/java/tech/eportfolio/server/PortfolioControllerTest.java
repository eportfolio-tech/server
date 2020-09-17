package tech.eportfolio.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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
import tech.eportfolio.server.service.UserService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class PortfolioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private PortfolioService portfolioService;

    private UserDTO userDTO;
    private UserDTO anotherUserDTO;

    private PortfolioDTO portfolioDTO;

    private User testUser;
    private User anotherTestUser;

    private Portfolio testPortfolio;
    private Portfolio anotherTestPortfolio;

    @Before
    public void init() {
        // Two users to create two same portfolios to test pagination
        userDTO = UserDTO.mock();
        userDTO.setUsername("test");
        anotherUserDTO = UserDTO.mock();

        testUser = userService.register(userService.fromUserDTO(userDTO));
        anotherTestUser = userService.register(userService.fromUserDTO(anotherUserDTO));

        portfolioDTO = PortfolioDTO.mock();
        testPortfolio = portfolioService.create(testUser, portfolioService.fromPortfolioDTO(portfolioDTO));
        anotherTestPortfolio = portfolioService.create(anotherTestUser, portfolioService.fromPortfolioDTO(portfolioDTO));
    }

    @Test
    public void ifFoundSomethingThenReturn200AndExpectNotEmpty() throws Exception {
        String query = testPortfolio.getDescription();
        this.mockMvc.perform(get("/portfolio/search")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .param("query", query)
                .param("page", "0")
                .param("size", "10")
        ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.content").isNotEmpty());
    }

    @Test
    public void ifFoundNothingThenReturn200AndExpectEmpty() throws Exception {
        String query = RandomStringUtils.randomAlphabetic(8);
        this.mockMvc.perform(get("/portfolio/search")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .param("query", query)
                .param("page", "0")
                .param("size", "10")
        ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.content").isEmpty());
    }

    @Test
    @WithMockUser(username = "test")
    public void ifUserAlreadyHasPortfolioButTryToCreateAnotherThenReturn500() throws Exception {
        String body = (new ObjectMapper()).valueToTree(userDTO).toString();
        this.mockMvc.perform(post("/portfolio/test")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .content(body)
        ).andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value("error"));
    }


    @Test
    public void ifPaginationThenReturn200AndMatchElementSize() throws Exception {
        String query = testPortfolio.getDescription();
        int pageSize = 1;
        this.mockMvc.perform(get("/portfolio/search")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .param("query", query)
                .param("page", "0")
                .param("size", String.valueOf(pageSize))
        ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.numberOfElements").value(pageSize));
    }


}
