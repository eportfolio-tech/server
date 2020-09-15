package tech.eportfolio.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.andreinc.mockneat.MockNeat;
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
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import tech.eportfolio.server.dto.PasswordResetRequestBody;
import tech.eportfolio.server.dto.UserDTO;
import tech.eportfolio.server.model.UserStorageContainer.User;
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
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    private User testUser;

    private UserDTO testUserDTO;

    @Before
    public void init() {
        testUserDTO = UserDTO.mock();
        testUserDTO.setUsername("test");
        testUser = userService.register(userService.fromUserDTO(testUserDTO));
    }

    @Test
    @WithAnonymousUser
    public void ifNotLoginInThenShouldReturn403() throws Exception {
        this.mockMvc.perform(post("/users/whatever")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
        ).andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value("fail"));
    }

    @Test
    @WithMockUser(username = "test")
    public void ifLoginInThenShouldReturnUser() throws Exception {
        this.mockMvc.perform(get("/users/test")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
        ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.user.username").value("test"));
    }

    @Test
    @WithMockUser(username = "test")
    public void ifNewPasswordIsValidThenShouldChangePassword() throws Exception {
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
    public void ifNewPasswordIsInvalidThenShouldReturn400() throws Exception {
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
    public void ifOldPasswordIsIncorrectThenShouldReturn500() throws Exception {
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
}