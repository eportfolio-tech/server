package tech.eportfolio.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.andreinc.mockneat.MockNeat;
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
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import tech.eportfolio.server.dto.PasswordResetRequestBody;
import tech.eportfolio.server.dto.UserDTO;
import tech.eportfolio.server.dto.UserPatchRequestBody;
import tech.eportfolio.server.service.UserService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@AutoConfigureDataMongo
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    private UserDTO testUserDTO;


    @Autowired
    MongoTemplate mongoTemplate;

    @Before
    public void init() {
        testUserDTO = UserDTO.mock();
        testUserDTO.setUsername("test");
        userService.register(userService.fromUserDTO(testUserDTO), false);

    }

    @Test
    @WithAnonymousUser
    public void ifNotLoginInThenGetUserShouldReturn401() throws Exception {
        this.mockMvc.perform(post("/users/whatever")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
        ).andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value("fail"));
    }

    @Test
    @WithMockUser(username = "test")
    public void ifLoginInThenGetUserShouldReturnUser() throws Exception {
        this.mockMvc.perform(get("/users/test")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
        ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.user.username").value("test"));
    }

    @Test
    @WithMockUser(username = "test")
    public void ifRequestIsCorrectThenPatchUserShouldReturn200() throws Exception {
        UserPatchRequestBody userPatchRequestBody = UserPatchRequestBody.mock();
        String body = (new ObjectMapper()).valueToTree(userPatchRequestBody).toString();

        this.mockMvc.perform(patch("/users/test/")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .content(body)
        ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.user.title").value(userPatchRequestBody.getTitle()))
                .andExpect(jsonPath("$.data.user.email").value(userPatchRequestBody.getEmail()))
                .andExpect(jsonPath("$.data.user.avatarUrl").value(userPatchRequestBody.getAvatarUrl()))
                .andExpect(jsonPath("$.data.user.firstName").value(userPatchRequestBody.getFirstName()))
                .andExpect(jsonPath("$.data.user.lastName").value(userPatchRequestBody.getLastName()));
    }

    @Test
    @WithMockUser(username = "test")
    public void ifAvatarUrlIsTooShortThenPatchUserShouldReturn400() throws Exception {
        UserPatchRequestBody userPatchRequestBody = UserPatchRequestBody.mock();
        userPatchRequestBody.setAvatarUrl("");
        String body = (new ObjectMapper()).valueToTree(userPatchRequestBody).toString();

        this.mockMvc.perform(patch("/users/test/")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .content(body)
        ).andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("fail"))
                .andExpect(jsonPath("$.data.avatarUrl").value("size must be between 10 and 2147483647"));
    }

    @Test
    @WithAnonymousUser
    public void ifNotLoginInThenPatchUserShouldReturn401() throws Exception {
        this.mockMvc.perform(patch("/users/test")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
        ).andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value("fail"));
    }


    @Test
    @WithMockUser(username = "test")
    public void ifPasswordResetSuccessThenShouldReturn202() throws Exception {
        String newPassword = MockNeat.threadLocal().passwords().strong().val() + "Aa1";
        PasswordResetRequestBody passwordResetRequestBody = PasswordResetRequestBody.builder()
                .oldPassword(testUserDTO.getPassword())
                .newPassword(newPassword).build();
        String body = (new ObjectMapper()).valueToTree(passwordResetRequestBody).toString();

        this.mockMvc.perform(post("/users/test/password-reset")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .content(body)
        ).andDo(print())
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    @WithMockUser(username = "test")
    public void ifNewPasswordIsInvalidThenPasswordResetShouldReturn400() throws Exception {
        String newPassword = MockNeat.threadLocal().passwords().weak().val();
        PasswordResetRequestBody passwordResetRequestBody = PasswordResetRequestBody.builder()
                .oldPassword(testUserDTO.getPassword())
                .newPassword(newPassword).build();
        String body = (new ObjectMapper()).valueToTree(passwordResetRequestBody).toString();

        this.mockMvc.perform(post("/users/test/password-reset")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .content(body)
        ).andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("fail"));
    }

    @Test
    @WithMockUser(username = "test")
    public void ifOldPasswordIsIncorrectThenPasswordResetShouldReturn500() throws Exception {
        // Create a strong new password
        String newPassword = MockNeat.threadLocal().passwords().strong().val() + "Aa1";
        PasswordResetRequestBody passwordResetRequestBody = PasswordResetRequestBody.builder()
                // generate a random password for old password
                .oldPassword(MockNeat.threadLocal().passwords().strong().val())
                .newPassword(newPassword).build();
        String body = (new ObjectMapper()).valueToTree(passwordResetRequestBody).toString();

        this.mockMvc.perform(post("/users/test/password-reset")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .content(body)
        ).andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value("error"));
    }

    @After
    public void afterClass() {
        mongoTemplate.getDb().drop();
    }
}