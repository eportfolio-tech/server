package tech.eportfolio.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.andreinc.mockneat.MockNeat;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import tech.eportfolio.server.dto.LoginRequestBody;
import tech.eportfolio.server.dto.UserDTO;
import tech.eportfolio.server.model.User;
import tech.eportfolio.server.service.UserService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@AutoConfigureDataMongo
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    private User existingUser;

    private UserDTO userDTO;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Before
    public void init() {
        userDTO = UserDTO.mock();
        existingUser = userService.register(userService.fromUserDTO(userDTO));
    }

    @Test
    public void ifUserNotExistThenLoginShouldReturn401() throws Exception {
        LoginRequestBody loginRequestBody = LoginRequestBody.builder().
                username(MockNeat.threadLocal().users().val()).
                password(MockNeat.threadLocal().passwords().strong().val()).build();
        String body = (new ObjectMapper()).valueToTree(loginRequestBody).toString();

        this.mockMvc.perform(post("/authentication/login")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .content(body)
        ).andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value("fail"));
    }

    @Test
    public void ifCredentialIsIncorrectThenLoginShouldReturn401() throws Exception {
        LoginRequestBody loginRequestBody = LoginRequestBody.builder().
                username(existingUser.getUsername()).
                password(MockNeat.threadLocal().passwords().strong().val()).build();
        String body = (new ObjectMapper()).valueToTree(loginRequestBody).toString();

        this.mockMvc.perform(post("/authentication/login")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .content(body)
        ).andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value("fail"));
    }

    @Test
    public void ifCredentialIsCorrectThenLoginShouldReturnJWTAndUser() throws Exception {
        LoginRequestBody loginRequestBody = LoginRequestBody.builder().
                username(userDTO.getUsername()).
                password(userDTO.getPassword()).build();
        String body = (new ObjectMapper()).valueToTree(loginRequestBody).toString();

        this.mockMvc.perform(post("/authentication/login")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .content(body)
        ).andDo(print())
                .andExpect(status().isOk()).andExpect(header().exists("x-jwt-token"))
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.user.username").value(existingUser.getUsername()))
                .andExpect(jsonPath("$.data.user.email").value(existingUser.getEmail()));
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

    @After
    public void afterClass() {
        mongoTemplate.getDb().drop();
    }
}