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
import tech.eportfolio.server.model.User;
import tech.eportfolio.server.service.PortfolioService;
import tech.eportfolio.server.service.UserService;

import static org.hamcrest.Matchers.hasSize;
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

    private User testUser;
    private User user2;

    private PortfolioDTO portfolioDTO;
    private int count = 0;

    @Before
    public void init() {
        // Two users to create two same portfolios to test pagination
        UserDTO userDTO = UserDTO.mock();
        userDTO.setUsername("test");

        testUser = userService.register(userService.fromUserDTO(userDTO));
        user2 = userService.register(userService.fromUserDTO(UserDTO.mock()));

        portfolioDTO = PortfolioDTO.mock();
        portfolioService.create(testUser, portfolioService.fromPortfolioDTO(portfolioDTO));
        count++;
        portfolioService.create(user2, portfolioService.fromPortfolioDTO(portfolioDTO));
        count++;
    }

//    @Test
//    public void ifFoundSomethingThenReturn200AndExpectNotEmpty() throws Exception {
//        String query = portfolioDTO.getDescription();
//        this.mockMvc.perform(get("/portfolio/search")
//                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
//                .param("query", query)
//                .param("page", "0")
//                .param("size", "10")
//        ).andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.status").value("success"))
//                .andExpect(jsonPath("$.data.content").isNotEmpty());
//    }

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
        String body = (new ObjectMapper()).valueToTree(portfolioDTO).toString();
        this.mockMvc.perform(post("/portfolio/test")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .content(body)
        ).andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value("error"));
    }


    @Test
    public void ifSizeIsLargeThanResultCountThenReturnLastPage() throws Exception {
        String query = portfolioDTO.getTitle();
        int size = 5;
        this.mockMvc.perform(get("/portfolio/search")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .param("query", query)
                .param("page", "0")
                .param("size", String.valueOf(size))
        ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.content", hasSize(count)))
                .andExpect(jsonPath("$.data.numberOfElements").value(String.valueOf(count)))
                .andExpect(jsonPath("$.data.totalElements").value(count))
                .andExpect(jsonPath("$.data.last").value("true"));
    }

    @Test
    public void ifSizeIsSmallerThanResultCountThenReturnFirstPage() throws Exception {
        String query = portfolioDTO.getTitle();
        int size = 1;
        this.mockMvc.perform(get("/portfolio/search")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .param("query", query)
                .param("page", "0")
                .param("size", String.valueOf(size))
        ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.content", hasSize(size)))
                .andExpect(jsonPath("$.data.numberOfElements").value(String.valueOf(size)))
                .andExpect(jsonPath("$.data.totalElements").value(count))
                .andExpect(jsonPath("$.data.last").value("false"));
    }

    @Test
    public void ifSizeEqualsResultCountThenReturnLastPage() throws Exception {
        String query = portfolioDTO.getTitle();
        int size = 2;
        this.mockMvc.perform(get("/portfolio/search")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .param("query", query)
                .param("page", "0")
                .param("size", String.valueOf(size))
        ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.content", hasSize(size)))
                .andExpect(jsonPath("$.data.numberOfElements").value(String.valueOf(size)))
                .andExpect(jsonPath("$.data.totalElements").value(count))
                .andExpect(jsonPath("$.data.last").value("true"));

    }


}
