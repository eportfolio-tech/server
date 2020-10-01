package tech.eportfolio.server.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import net.andreinc.mockneat.MockNeat;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import tech.eportfolio.server.dto.PortfolioDTO;
import tech.eportfolio.server.dto.UserDTO;
import tech.eportfolio.server.model.Portfolio;
import tech.eportfolio.server.model.User;
import tech.eportfolio.server.repository.PortfolioRepository;
import tech.eportfolio.server.service.PortfolioService;
import tech.eportfolio.server.service.UserService;
import tech.eportfolio.server.service.VerificationService;

import java.util.HashMap;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class PortfolioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private PortfolioService portfolioService;

    @Autowired
    VerificationService verificationService;

    @Autowired
    PortfolioRepository portfolioRepository;

    @Autowired
    MongoTemplate mongoTemplate;


    private static final String BASE_PATH = "/portfolios";

    private User testUser;

    private Portfolio testPortfolio;

    private PortfolioDTO portfolioDTO;


    @Before
    public void init() {
        UserDTO userDTO = UserDTO.mock();
        userDTO.setUsername("test");

        // Verify testUser
        testUser = userService.register(userService.fromUserDTO(userDTO), false);
        testUser = verificationService.verify(testUser);

        portfolioDTO = PortfolioDTO.mock();
        testPortfolio = portfolioService.create(testUser, portfolioService.fromPortfolioDTO(portfolioDTO));

    }

    @Test
    @WithMockUser(username = "test")
    public void ifUserAlreadyHasPortfolioButTryToCreateAnotherThenReturn409() throws Exception {
        String body = (new ObjectMapper()).valueToTree(portfolioDTO).toString();
        this.mockMvc.perform(post(BASE_PATH + "/test")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .content(body)
        ).andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value("fail"));
    }

    @Test
    @WithMockUser(username = "test")
    public void ifUserDoesntHaveAPortfolioThenReturn404() throws Exception {
        portfolioRepository.delete(testPortfolio);
        this.mockMvc.perform(get(BASE_PATH + "/test")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
        ).andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("fail"));
    }

    @Test
    @WithMockUser("test")
    public void IfPatchPortfolioThenUpdateMetaInfo() throws Exception {
        String updatedDescription = MockNeat.threadLocal().celebrities().actors().val();
        String imageUrl = "https://images.unsplash.com/photo-1511496920016-89698ccce955?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=1650&q=80";
        portfolioDTO.setCoverImage(imageUrl);
        portfolioDTO.setDescription(updatedDescription);
        String body = (new ObjectMapper()).valueToTree(portfolioDTO).toString();
        this.mockMvc.perform(patch(BASE_PATH + "/test")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .content(body)
        ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.portfolio.description").value(updatedDescription))
                .andExpect(jsonPath("$.data.portfolio.coverImage").value(imageUrl));
    }

    @Test
    @WithMockUser("test")
    public void ifDeleteContentThenReturn200AndExpectContentToBeNull() throws Exception {
        this.mockMvc.perform(delete(BASE_PATH + "/test/content")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .param("username", "test")
        ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.content.content").isEmpty())
                .andReturn();
    }

    @Test
    @WithMockUser("test")
    public void ifGetPortfolioThenReturnPortfolio() throws Exception {
        this.mockMvc.perform(get(BASE_PATH + "/test")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .param("username", "test")
        ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.portfolio.username").value("test"))
                .andExpect(jsonPath("$.data.portfolio.title").value(portfolioDTO.getTitle()))
                .andReturn();
    }


    @Test
    @WithMockUser("test")
    public void ifPutContentThenReturn200AndExpectContentToMatch() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode();
        JsonNode jsonNode = objectNode.put(RandomStringUtils.randomAlphabetic(4), RandomStringUtils.randomAlphanumeric(8));
        TypeReference<HashMap<String, Object>> typeRef = new TypeReference<>() {
        };
        HashMap<String, Object> map = mapper.convertValue(jsonNode, typeRef);
        String body = (new ObjectMapper()).valueToTree(jsonNode).toString();
        this.mockMvc.perform(put(BASE_PATH + "/test/content")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .param("username", "test")
                .content(body)
        ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.content").value(map))
                .andReturn();
    }

    @After
    public void afterClass() {
        mongoTemplate.getDb().drop();
    }
}
