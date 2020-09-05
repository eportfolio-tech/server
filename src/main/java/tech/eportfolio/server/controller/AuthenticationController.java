package tech.eportfolio.server.controller;

import com.auth0.jwt.JWTVerifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.eportfolio.server.constant.Role;
import tech.eportfolio.server.dto.UserDTO;
import tech.eportfolio.server.exception.EmailVerificationFailException;
import tech.eportfolio.server.exception.UserNotFoundException;
import tech.eportfolio.server.exception.handler.AuthenticationExceptionHandler;
import tech.eportfolio.server.model.User;
import tech.eportfolio.server.model.UserPrincipal;
import tech.eportfolio.server.security.SecurityConstant;
import tech.eportfolio.server.service.EmailService;
import tech.eportfolio.server.service.UserService;
import tech.eportfolio.server.utility.JWTTokenProvider;

import javax.validation.Valid;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/authentication")
public class AuthenticationController extends AuthenticationExceptionHandler {

    private final UserService userService;

    private final AuthenticationManager authenticationManager;

    private final JWTTokenProvider jwtTokenProvider;

    private final EmailService emailService;

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

    @Transactional
    @PostMapping("/verify")
    public ResponseEntity<User> verify(@RequestParam("token") String token) {
        JWTVerifier jwtVerifier = jwtTokenProvider.getJWTVerifier();
        String username = jwtVerifier.verify(token).getSubject();
        Optional<User> verifyUser = userService.findByUsername(username);
        if (verifyUser.isPresent() && jwtTokenProvider.isTokenValid(username, token)) {
            UserPrincipal userPrincipal = new UserPrincipal(verifyUser.get());
            HttpHeaders jwtHeader = getJwtHeader(userPrincipal);
            verifyUser.get().setRoles(Role.ROLE_VERIFIED_USER.name());
            verifyUser.get().setAuthorities(Role.ROLE_VERIFIED_USER.getAuthorities());
            verifyUser.get().setUpdatedOn(new Date());

            return new ResponseEntity<>(verifyUser.get(), jwtHeader, HttpStatus.OK);
        } else {
            throw new EmailVerificationFailException();
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
        response.put("token", "Bearer " + jwtTokenProvider.generateJWTToken(userPrincipal));
        return new ResponseEntity<>(response, jwtHeader, HttpStatus.OK);
    }

    private HttpHeaders getJwtHeader(UserPrincipal userPrincipal) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(SecurityConstant.JWT_TOKEN_HEADER, jwtTokenProvider.generateJWTToken(userPrincipal));
        return httpHeaders;
    }

    private void authenticate(String username, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }

}
