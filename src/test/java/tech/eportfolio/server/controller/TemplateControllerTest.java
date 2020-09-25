package tech.eportfolio.server.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import tech.eportfolio.server.dto.TemplateDTO;
import tech.eportfolio.server.dto.UserDTO;
import tech.eportfolio.server.model.Portfolio;
import tech.eportfolio.server.model.User;
import tech.eportfolio.server.service.PortfolioService;
import tech.eportfolio.server.service.UserCommentService;
import tech.eportfolio.server.service.UserService;

import java.util.HashMap;

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
@AutoConfigureDataMongo
public class TemplateControllerTest {

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

    }

    @Test
    @WithMockUser(username = "test")
    public void ifFindAllTemplatesSuccessfulThenReturn200() throws Exception {

        this.mockMvc.perform(get("/templates/")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
        ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    @WithMockUser(username = "test")
    public void ifCreateNewTemplateSuccessfulThenReturn200() throws Exception {
        TemplateDTO templateDTO = TemplateDTO.mock();
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<HashMap<String, Object>> typeRef = new TypeReference<>() {
        };
        HashMap<String, Object> map = mapper.convertValue(templateDTO.getBoilerplate(), typeRef);

        String body = (new ObjectMapper()).valueToTree(templateDTO).toString();

        this.mockMvc.perform(post("/templates/")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .content(body)
        ).andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.template.title").value(templateDTO.getTitle()))
                .andExpect(jsonPath("$.data.template.boilerplate").value(map))
                .andExpect(jsonPath("$.data.template.description").value(templateDTO.getDescription()));
    }

    @Test
    @WithMockUser(username = "test")
    public void ifTryToFindANotExistedTemplateThenReturn404() throws Exception {
        String templateId = RandomStringUtils.randomAlphabetic(8);

        this.mockMvc.perform(get("/templates/" + templateId)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .param("templateId", templateId)
        ).andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("fail"));
    }


    @After
    public void afterClass() {
        mongoTemplate.getDb().drop();
    }

}