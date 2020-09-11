package tech.eportfolio.server;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import tech.eportfolio.server.exception.UserNotFoundException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void ifUserNotExistThenReturn404() throws Exception {
        String username = RandomStringUtils.randomAlphabetic(8);
        String password = RandomStringUtils.randomAlphabetic(8);
        this.mockMvc.perform(post("/authentication/login")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .param("username", username)
                .param("password", password)
        ).andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.message").value(new UserNotFoundException(username).getMessage()))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }
}