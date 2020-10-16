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
import org.springframework.cache.CacheManager;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import tech.eportfolio.server.dto.TemplateDTO;
import tech.eportfolio.server.dto.UserDTO;
import tech.eportfolio.server.model.Template;
import tech.eportfolio.server.model.User;
import tech.eportfolio.server.service.TemplateService;
import tech.eportfolio.server.service.UserService;

import java.util.HashMap;
import java.util.Objects;

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
public class TemplateControllerTest {

    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    private CacheManager cacheManager;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserService userService;
    @Autowired
    private TemplateService templateService;

    private User testUser;

    private UserDTO testUserDTO;

    private TemplateDTO templateDTO;

    @Before
    public void init() {

        testUserDTO = UserDTO.mock();
        testUserDTO.setUsername("test");

        testUser = userService.register(userService.fromUserDTO(testUserDTO), false);

        templateDTO = TemplateDTO.mock();

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
    public void ifCreateNewTemplateSuccessfulThenReturn201() throws Exception {
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
    public void ifTryToFindAExistingTemplateThenReturn200() throws Exception {
        Template template = templateService.create(testUser, templateDTO);

        this.mockMvc.perform(get("/templates/" + template.getId())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .param("templateId", template.getId())
        ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.template.id").value(template.getId()))
                .andExpect(jsonPath("$.data.template.title").value(template.getTitle()))
                .andExpect(jsonPath("$.data.template.description").value(template.getDescription()))
                .andExpect(jsonPath("$.data.template.boilerplate").value(template.getBoilerplate()));
    }

    @Test
    @WithMockUser(username = "test")
    public void ifTryToFindANotExistingTemplateThenReturn404() throws Exception {
        String templateId = RandomStringUtils.randomAlphabetic(8);

        this.mockMvc.perform(get("/templates/" + templateId)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .param("templateId", templateId)
        ).andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("fail"));
    }

    @Test
    @WithMockUser(username = "test")
    public void ifTryToDeleteAnExistingTemplateThenReturn204() throws Exception {
        Template template = templateService.create(testUser, templateDTO);

        this.mockMvc.perform(delete("/templates/" + template.getId())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .param("templateId", template.getId())
        ).andDo(print())
                .andExpect(status().isNoContent())
                .andReturn();
    }

    @Test
    @WithMockUser(username = "test")
    public void ifTryToDoubleDeleteATemplateThenReturn404() throws Exception {
        Template template = templateService.create(testUser, templateDTO);
        templateService.delete(template);

        this.mockMvc.perform(delete("/templates/" + template.getId())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .param("templateId", template.getId())
        ).andDo(print())
                .andExpect(status().isNotFound())
                .andReturn();
    }


    @After
    public void afterClass() {
        cacheManager.getCacheNames().forEach(cacheName -> Objects.requireNonNull(cacheManager.getCache(cacheName)).clear());
        mongoTemplate.getDb().drop();
    }

}