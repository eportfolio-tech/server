package tech.eportfolio.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import tech.eportfolio.server.constant.SecurityConstant;
import tech.eportfolio.server.constant.VerificationConstant;
import tech.eportfolio.server.dto.PasswordRecoveryRequestBody;
import tech.eportfolio.server.dto.UserDTO;
import tech.eportfolio.server.exception.UserNotFoundException;
import tech.eportfolio.server.exception.handler.AuthenticationExceptionHandler;
import tech.eportfolio.server.model.User;
import tech.eportfolio.server.model.UserPrincipal;
import tech.eportfolio.server.service.EmailService;
import tech.eportfolio.server.service.UserService;
import tech.eportfolio.server.utility.JWTTokenProvider;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/authentication")
public class AuthenticationController extends AuthenticationExceptionHandler {

    private final UserService userService;

    private final AuthenticationManager authenticationManager;

    private final EmailService emailService;

    private final JWTTokenProvider jwtTokenProvider;

    @Autowired
    public AuthenticationController(UserService userService, AuthenticationManager authenticationManager, JWTTokenProvider jwtTokenProvider, EmailService emailService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.emailService = emailService;
    }

    @PostMapping("/signup")
    public ResponseEntity<User> signUp(@RequestBody @Valid UserDTO userDTO) {
        User user = userService.register(userService.fromUserDTO(userDTO));
        UserPrincipal userPrincipal = new UserPrincipal(user);
        HttpHeaders jwtHeader = getJwtHeader(userPrincipal);
        emailService.sendSimpleMessage(user.getEmail(), "A welcome message from eportfolio.tech", "Welcome! Thank you for joining us. You can set up your e-portfolio to demonstrate your experience and skills");
        return new ResponseEntity<>(user, jwtHeader, HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestParam String username, @RequestParam String password) {
        authenticate(username, password);
        Optional<User> loginUser = userService.findByUsername(username);
        if (loginUser.isPresent()) {
            UserPrincipal userPrincipal = new UserPrincipal(loginUser.get());
            HttpHeaders jwtHeader = getJwtHeader(userPrincipal);
            return new ResponseEntity<>(loginUser.get(), jwtHeader, HttpStatus.OK);
        } else {
            throw new UserNotFoundException(username);
        }
    }

    @GetMapping("/letMeLogIn")
    public ResponseEntity<Map<String, Object>> letMeLogIn() {
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
        response.put("token", "Bearer " + jwtTokenProvider.generateJWTToken(userPrincipal, SecurityConstant.SECRET));
        return new ResponseEntity<>(response, jwtHeader, HttpStatus.OK);
    }

    @GetMapping("/link")
    public String generatePasswordRecoveryLink(@RequestParam String username) {
        User user = userService.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));
        // Generate a verification token for current user
        String verificationToken = userService.generatePasswordRecoveryToken(user);
        // Generate URI to be embedded into email
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .scheme(VerificationConstant.SCHEME_HTTPS).
                        host(VerificationConstant.HOST).path(VerificationConstant.PATH).
                        queryParam("token", verificationToken).
                        queryParam("username", username).build();
        return uriComponents.toUriString();
    }

    @GetMapping("/token")
    public String generatePasswordRecoveryToken(@RequestParam String username) {
        User user = userService.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));
        return userService.generatePasswordRecoveryToken(user);
    }

    @PostMapping("/verify")
    public User verifyPasswordReset(@RequestParam String username, @RequestBody @Valid PasswordRecoveryRequestBody passwordRecoveryRequestBody) {

        User user = userService.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));
        return userService.passwordRecovery(user, passwordRecoveryRequestBody.getToken(), passwordRecoveryRequestBody.getNewPassword());

    }

    private HttpHeaders getJwtHeader(UserPrincipal userPrincipal) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(SecurityConstant.JWT_TOKEN_HEADER, jwtTokenProvider.generateJWTToken(userPrincipal, SecurityConstant.SECRET));
        return httpHeaders;
    }

    private void authenticate(String username, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }

}
