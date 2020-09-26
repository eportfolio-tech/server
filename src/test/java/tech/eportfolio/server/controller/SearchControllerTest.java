package tech.eportfolio.server.controller;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import tech.eportfolio.server.common.constant.Role;
import tech.eportfolio.server.common.constant.Visibility;
import tech.eportfolio.server.dto.PortfolioDTO;
import tech.eportfolio.server.dto.UserDTO;
import tech.eportfolio.server.model.Portfolio;
import tech.eportfolio.server.model.User;
import tech.eportfolio.server.repository.PortfolioRepository;
import tech.eportfolio.server.service.PortfolioService;
import tech.eportfolio.server.service.UserService;
import tech.eportfolio.server.service.VerificationService;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

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
public class SearchControllerTest {

    private static final String BASE_PATH = "/search";
    @Autowired
    VerificationService verificationService;
    @Autowired
    PortfolioRepository portfolioRepository;
    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserService userService;
    @Autowired
    private PortfolioService portfolioService;
    private User testUser;
    private User secondUser;
    private User thirdUser;
    private Portfolio testPortfolio;

    private PortfolioDTO portfolioDTO;
    private int count = 0;


    @Before
    public void init() {
        UserDTO userDTO = UserDTO.mock();
        userDTO.setUsername("test");

        // Verify testUser
        testUser = userService.register(userService.fromUserDTO(userDTO), false);
        testUser = verificationService.verify(testUser);

        secondUser = userService.register(userService.fromUserDTO(UserDTO.mock()), false);
        thirdUser = userService.register(userService.fromUserDTO(UserDTO.mock()), false);

        portfolioDTO = PortfolioDTO.mock();
        testPortfolio = portfolioService.create(testUser, portfolioService.fromPortfolioDTO(portfolioDTO));
        count++;

        portfolioDTO.setVisibility(Visibility.VERIFIED_USER);
        portfolioService.create(secondUser, portfolioService.fromPortfolioDTO(portfolioDTO));
        count++;

        portfolioDTO.setVisibility(Visibility.UNVERIFIED_USER);
        portfolioService.create(thirdUser, portfolioService.fromPortfolioDTO(portfolioDTO));
        count++;
    }

    @Test
    @WithMockUser(username = "test")
    public void ifFoundNothingThenReturn200AndExpectEmpty() throws Exception {
        String query = RandomStringUtils.randomAlphabetic(8);
        this.mockMvc.perform(get(BASE_PATH + "/keyword")
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
    public void ifSizeIsLargeThenResultCountThenReturnLastPage() throws Exception {
        String query = portfolioDTO.getTitle();
        int size = 5;
        this.mockMvc.perform(get(BASE_PATH + "/keyword")
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
                .andExpect(jsonPath("$.data.last").value(String.valueOf(size > count)));
    }

    @Test
    @WithMockUser(username = "test")
    public void ifSizeIsSmallerThenResultCountThenReturnFirstPage() throws Exception {
        String query = portfolioDTO.getTitle();
        int size = 1;
        this.mockMvc.perform(get(BASE_PATH + "/keyword")
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
                .andExpect(jsonPath("$.data.last").value(String.valueOf(size > count)));
    }

    @Test
    @WithMockUser(username = "test")
    public void ifSizeEqualsResultCountThenReturnLastPage() throws Exception {
        String query = portfolioDTO.getTitle();
        int size = count;
        this.mockMvc.perform(get(BASE_PATH + "/keyword")
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
                .andExpect(jsonPath("$.data.last").value(String.valueOf(size == count)));

    }

    @Test
    @WithAnonymousUser
    public void ifNotLoginThenReturnPublicPortfolio() throws Exception {
        // Expect 1 public, 1 private portfolio in the database
        String query = portfolioDTO.getTitle();
        List<Portfolio> publicResult = portfolioService.searchWithVisibilities(query, Collections.singletonList(Visibility.PUBLIC));
        int size = 5;
        this.mockMvc.perform(get(BASE_PATH + "/keyword")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .param("query", query)
                .param("page", "0")
                .param("size", String.valueOf(size))
        ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.numberOfElements").value(String.valueOf(publicResult.size())));
    }

    @Test
    @WithMockUser("test")
    public void ifUnverifiedThenReturnPublicAndUnverifiedPortfolio() throws Exception {
        // Update test user's role to unverified
        testUser.setRoles(Role.ROLE_UNVERIFIED_USER.name());
        userService.save(testUser);

        String query = portfolioDTO.getTitle();
        List<Visibility> searchVisibility = new LinkedList<>();
        searchVisibility.add(Visibility.PUBLIC);
        searchVisibility.add(Visibility.UNVERIFIED_USER);
        List<Portfolio> searchResult = portfolioService.searchWithVisibilities(query, searchVisibility);
        int size = 5;
        this.mockMvc.perform(get(BASE_PATH + "/keyword")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .param("query", query)
                .param("page", "0")
                .param("size", String.valueOf(size))
        ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.numberOfElements").value(String.valueOf(searchResult.size())));
    }

    @Test
    @WithMockUser("test")
    public void ifVerifiedThenReturnAllExceptPrivatePortfolio() throws Exception {
        String query = portfolioDTO.getTitle();
        List<Visibility> searchVisibility = new LinkedList<>();
        searchVisibility.add(Visibility.PUBLIC);
        searchVisibility.add(Visibility.UNVERIFIED_USER);
        searchVisibility.add(Visibility.VERIFIED_USER);
        List<Portfolio> searchResult = portfolioService.searchWithVisibilities(query, searchVisibility);
        int size = 5;
        this.mockMvc.perform(get(BASE_PATH + "/keyword")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .param("query", query)
                .param("page", "0")
                .param("size", String.valueOf(size))
        ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.numberOfElements").value(String.valueOf(searchResult.size())));
    }


    @After
    public void afterClass() {
        mongoTemplate.getDb().drop();
    }
}
