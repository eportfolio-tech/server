//package tech.eportfolio.server;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.MediaType;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.annotation.DirtiesContext;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.junit4.SpringRunner;
//import org.springframework.test.web.servlet.MockMvc;
//import tech.eportfolio.server.dto.UserDTO;
//import tech.eportfolio.server.dto.UserPatchRequestBody;
//import tech.eportfolio.server.model.User;
//import tech.eportfolio.server.service.UserService;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@SpringBootTest
//@RunWith(SpringRunner.class)
//@ActiveProfiles("test")
//@AutoConfigureMockMvc
//@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
//@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
//public class SocialityControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private UserService userService;
//
//    private User testUser;
//
//    private UserDTO testUserDTO;
//
//    @Before
//    public void init() {
//        testUserDTO = UserDTO.mock();
//        testUserDTO.setUsername("test");
//        testUser = userService.register(userService.fromUserDTO(testUserDTO));
//    }
//
//    @Test
//    @WithMockUser(username = "test")
//    public void ifUserLikeAPortfolioWithoutReturn200() throws Exception {
//        UserPatchRequestBody userPatchRequestBody = UserPatchRequestBody.mock();
//        String body = (new ObjectMapper()).valueToTree(userPatchRequestBody).toString();
//
//        this.mockMvc.perform(patch("/users/test/")
//                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
//                .content(body)
//        ).andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.status").value("success"))
//                .andExpect(jsonPath("$.data.user.title").value(userPatchRequestBody.getTitle()))
//                .andExpect(jsonPath("$.data.user.email").value(userPatchRequestBody.getEmail()))
//                .andExpect(jsonPath("$.data.user.avatarUrl").value(userPatchRequestBody.getAvatarUrl()))
//                .andExpect(jsonPath("$.data.user.firstName").value(userPatchRequestBody.getFirstName()))
//                .andExpect(jsonPath("$.data.user.lastName").value(userPatchRequestBody.getLastName()));
//    }
//
//}