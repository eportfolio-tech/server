package tech.eportfolio.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import tech.eportfolio.server.dto.UserDTO;
import tech.eportfolio.server.exception.UserNotFoundException;
import tech.eportfolio.server.service.UserService;

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

    @Autowired
    private UserService userService;

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

    //    Test for successfully sign up only for the "test" User
    @Test
    public void ifSignupSuccessThenReturn200() throws Exception {
        if (userService.findByEmail("test@eportfolio.tech").isPresent()) {
            return;
        }
        UserDTO userDTO = generateTestUserDTO();
        String body = (new ObjectMapper()).valueToTree(userDTO).toString();
        this.mockMvc.perform(post("/authentication/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
        ).andDo(print())
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void ifEmailAddressNotValidThenReturn400() throws Exception {

        UserDTO userDTO = generateTestUserDTO();
        userDTO.setEmail(RandomStringUtils.random(8, true, true));
        String body = (new ObjectMapper()).valueToTree(userDTO).toString();

        this.mockMvc.perform(post("/authentication/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
        ).andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    public void ifEmailAddressAlreadyExistThenReturn409() throws Exception {

        UserDTO userDTO = generateTestUserDTO();
        userDTO.setUsername("");
        String body = (new ObjectMapper()).valueToTree(userDTO).toString();
        this.mockMvc.perform(post("/authentication/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
        ).andDo(print())
                .andExpect(status().is(HttpStatus.CONFLICT.value()))
                .andReturn();
    }

    @Test
    public void ifUsernameAlreadyExistThenReturn409() throws Exception {

        UserDTO userDTO = generateTestUserDTO();
        userDTO.setEmail(RandomStringUtils.randomAlphabetic(8) + "test@eportfolio.tech");
        String body = (new ObjectMapper()).valueToTree(userDTO).toString();
        this.mockMvc.perform(post("/authentication/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
        ).andDo(print())
                .andExpect(status().is(HttpStatus.CONFLICT.value()))
                .andReturn();
    }


    private UserDTO generateTestUserDTO() {
        String email = "test@eportfolio.tech";
        String firstName = "test";
        String lastName = "man";
        String password = "WhatSoEverWhoCare123";
        String title = "Mr.";
        String phone = "(03)90355511";
        String username = "test";

        UserDTO userDTO = new UserDTO();
        userDTO.setEmail(email);
        userDTO.setPassword(password);
        userDTO.setFirstName(firstName);
        userDTO.setLastName(lastName);
        userDTO.setPhone(phone);
        userDTO.setTitle(title);
        userDTO.setUsername(username);

        return userDTO;
    }


}