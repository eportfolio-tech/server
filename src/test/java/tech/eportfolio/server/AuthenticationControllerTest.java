package tech.eportfolio.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.andreinc.mockneat.MockNeat;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import tech.eportfolio.server.dto.UserDTO;
import tech.eportfolio.server.model.User;
import tech.eportfolio.server.service.UserService;

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
public class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;


    private User existingUser;

    @Before
    public void init() {
        existingUser = userService.register(userService.fromUserDTO(UserDTO.mock()));
    }

    @Test
    public void ifUserNotExistThenLoginShouldReturn401() throws Exception {
        String username = RandomStringUtils.randomAlphabetic(8);
        String password = RandomStringUtils.randomAlphabetic(8);
        this.mockMvc.perform(post("/authentication/login")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .param("username", username)
                .param("password", password)
        ).andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value("fail"));
    }

    @Test
    public void ifPasswordIsIncorrectThenLoginShouldReturn401() throws Exception {
        this.mockMvc.perform(post("/authentication/login")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .param("username", existingUser.getUsername())
                .param("password", MockNeat.threadLocal().passwords().val())
        ).andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value("fail"));
    }

    @Test
    public void ifSuccessThenSignUpShouldReturn200() throws Exception {
        UserDTO user = UserDTO.mock();
        String body = (new ObjectMapper()).valueToTree(user).toString();
        this.mockMvc.perform(post("/authentication/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
        ).andDo(print())
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void ifPasswordIsTooWeakThenSignUpShouldReturn400() throws Exception {
        UserDTO user = UserDTO.mock();
        user.setPassword(MockNeat.threadLocal().passwords().weak().val());
        String body = (new ObjectMapper()).valueToTree(user).toString();
        this.mockMvc.perform(post("/authentication/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
        ).andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("fail"))
                .andReturn();
    }

    @Test
    public void ifEmailAddressIsInValidThenSignUpShouldReturn400() throws Exception {

        UserDTO userDTO = UserDTO.mock();
        // Generate a random string for email address
        userDTO.setEmail(RandomStringUtils.random(8, true, true));
        String body = (new ObjectMapper()).valueToTree(userDTO).toString();

        this.mockMvc.perform(post("/authentication/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
        ).andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("fail"))
                .andReturn();
    }

    @Test
    public void ifEmailAddressAlreadyExistThenSignUpShouldReturn409() throws Exception {
        UserDTO userWithExistEmail = UserDTO.mock();
        userWithExistEmail.setEmail(existingUser.getEmail());

        String body = (new ObjectMapper()).valueToTree(userWithExistEmail).toString();
        this.mockMvc.perform(post("/authentication/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
        ).andDo(print())
                .andExpect(status().is(HttpStatus.CONFLICT.value()))
                .andExpect(jsonPath("$.status").value("fail"))
                .andReturn();
    }

    @Test
    public void ifUsernameAlreadyExistThenSignUpShouldReturn409() throws Exception {
        UserDTO userWithExistUsername = UserDTO.mock();
        userWithExistUsername.setUsername(existingUser.getUsername());

        String body = (new ObjectMapper()).valueToTree(userWithExistUsername).toString();
        this.mockMvc.perform(post("/authentication/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
        ).andDo(print())
                .andExpect(status().is(HttpStatus.CONFLICT.value()))
                .andExpect(jsonPath("$.status").value("fail"))
                .andReturn();
    }
}