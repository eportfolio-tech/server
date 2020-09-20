package tech.eportfolio.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import tech.eportfolio.server.common.constant.SecurityConstant;
import tech.eportfolio.server.common.constraint.ValidPassword;
import tech.eportfolio.server.common.exception.EmailNotFoundException;
import tech.eportfolio.server.common.exception.UserNotFoundException;
import tech.eportfolio.server.common.exception.handler.AuthenticationExceptionHandler;
import tech.eportfolio.server.common.jsend.SuccessResponse;
import tech.eportfolio.server.common.utility.JWTTokenProvider;
import tech.eportfolio.server.dto.LoginRequestBody;
import tech.eportfolio.server.dto.UserDTO;
import tech.eportfolio.server.model.User;
import tech.eportfolio.server.model.UserPrincipal;
import tech.eportfolio.server.service.RecoveryService;
import tech.eportfolio.server.service.UserService;
import tech.eportfolio.server.service.VerificationService;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/authentication")
public class AuthenticationController extends AuthenticationExceptionHandler {

    private final UserService userService;

    private final AuthenticationManager authenticationManager;

    private final VerificationService verificationService;

    private final JWTTokenProvider jwtTokenProvider;

    private final RecoveryService recoveryService;

    @Autowired
    public AuthenticationController(UserService userService, AuthenticationManager authenticationManager, VerificationService verificationService, JWTTokenProvider jwtTokenProvider, RecoveryService recoveryService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.verificationService = verificationService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.recoveryService = recoveryService;
    }


    @PostMapping("/signup")
    public ResponseEntity<SuccessResponse<User>> signUp(@RequestBody @Valid UserDTO userDTO) {
        User user = userService.register(userService.fromUserDTO(userDTO));
        UserPrincipal userPrincipal = new UserPrincipal(user);
        HttpHeaders jwtHeader = getJwtHeader(userPrincipal);
        verificationService.sendVerificationEmail(user);
        return new SuccessResponse<>("user", user).toOk(jwtHeader);
    }

    @PostMapping("/login")
    public ResponseEntity<SuccessResponse<User>> login(@RequestBody @Valid LoginRequestBody loginRequestBody) {
        String username = loginRequestBody.getUsername();
        authenticate(username, loginRequestBody.getPassword());
        User loginUser = userService.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));
        UserPrincipal userPrincipal = new UserPrincipal(loginUser);
        HttpHeaders jwtHeader = getJwtHeader(userPrincipal);
        return new SuccessResponse<>("user", loginUser).toOk(jwtHeader);
    }

    @DeleteMapping("/ 大哥大嫂过年好")
    public ResponseEntity<SuccessResponse<String>> happyNewYear() {
        return new SuccessResponse<>("price", "大锤80小锤40").toOk();
    }

    @GetMapping("/loginAsTest")
    public ResponseEntity<SuccessResponse<Object>> letMeLogIn() {
        Optional<User> loginUser = userService.findByUsername("test");
        User user;
        if (loginUser.isEmpty()) {
            User test = new User();
            test.setPassword("WhatSoEverWhoCare123");
            test.setUsername("test");
            test.setEmail("test@eportfolio.tech");
            test.setFirstName("test");
            test.setLastName("man");
            test.setPhone("(03)90355511");
            test.setTitle("Mr.");
            user = userService.register(test);
        } else {
            user = loginUser.get();
        }
        UserPrincipal userPrincipal = new UserPrincipal(user);
        HttpHeaders jwtHeader = getJwtHeader(userPrincipal);
        Map<String, Object> response = new HashMap<>();
        response.put("user", user);
        response.put("token", "Bearer " + jwtTokenProvider.generateJWTToken(userPrincipal, SecurityConstant.AUTHENTICATION_SECRET));
        return new SuccessResponse<>(response).toOk(jwtHeader);
    }

    @DeleteMapping("/deleteTest")
    public ResponseEntity<SuccessResponse<Object>> deleteTest() {
        String username = "test";
        User user = userService.findByUsername(username).orElseThrow(() -> new EmailNotFoundException(username));
        userService.delete(user);
        // Generate URI to be embedded into email
        return new SuccessResponse<>().toOk();
    }


    @GetMapping("/recovery-link")
    public ResponseEntity<SuccessResponse<String>> generatePasswordRecoveryLink(@RequestParam String email) {
        User user = userService.findByEmail(email).orElseThrow(() -> new EmailNotFoundException(email));
        // Generate a verification token for current user
        String recoveryToken = recoveryService.generatePasswordRecoveryToken(user);
        // Generate URI to be embedded into email
        return new SuccessResponse<>("recovery-link", recoveryService.buildRecoveryLink(user, recoveryToken)).toOk();
    }

    @GetMapping("/recovery-token")
    public ResponseEntity<SuccessResponse<String>> generatePasswordRecoveryToken(@RequestParam String username) {
        User user = userService.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));
        return new SuccessResponse<>("recovery-token", recoveryService.generatePasswordRecoveryToken(user)).toOk();
    }

    @PostMapping("/send-recovery-link")
    public ResponseEntity<SuccessResponse<Object>> sendPasswordRecoveryLink(@RequestParam String email) {
        User user = userService.findByEmail(email).orElseThrow(() -> new EmailNotFoundException(email));
        recoveryService.sendRecoveryEmail(user);
        return new SuccessResponse<>().toOk();
    }

    @PostMapping("/password-recovery")
    public ResponseEntity<SuccessResponse<Object>> verifyPasswordReset(@RequestParam String username, @RequestParam String token, @RequestParam @ValidPassword String password) {
        User user = userService.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));
        recoveryService.passwordRecovery(user, token, password);
        return new SuccessResponse<>().toOk();
    }

    private HttpHeaders getJwtHeader(UserPrincipal userPrincipal) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(SecurityConstant.JWT_TOKEN_HEADER, jwtTokenProvider.generateJWTToken(userPrincipal, SecurityConstant.AUTHENTICATION_SECRET));
        return httpHeaders;
    }

    private void authenticate(String username, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }

}
