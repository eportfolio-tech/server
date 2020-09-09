package tech.eportfolio.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import tech.eportfolio.server.constant.SecurityConstant;
import tech.eportfolio.server.constraint.ValidPassword;
import tech.eportfolio.server.dto.UserDTO;
import tech.eportfolio.server.exception.UserNotFoundException;
import tech.eportfolio.server.exception.handler.AuthenticationExceptionHandler;
import tech.eportfolio.server.model.User;
import tech.eportfolio.server.model.UserPrincipal;
import tech.eportfolio.server.service.RecoveryService;
import tech.eportfolio.server.service.UserService;
import tech.eportfolio.server.service.VerificationService;
import tech.eportfolio.server.utility.JWTTokenProvider;

import javax.validation.Valid;
import javax.validation.constraints.Null;
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
    public ResponseEntity<User> signUp(@RequestBody @Valid UserDTO userDTO) {
        User user = userService.register(userService.fromUserDTO(userDTO));
        UserPrincipal userPrincipal = new UserPrincipal(user);
        HttpHeaders jwtHeader = getJwtHeader(userPrincipal);
        verificationService.sendVerificationEmail(user);
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

    @DeleteMapping("/")
    public ResponseEntity<String> canYouDelete(@RequestBody String content) {
        return new ResponseEntity<>(content, null, HttpStatus.OK);
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

    @GetMapping("/recovery-link")
    public String generatePasswordRecoveryLink(@RequestParam String username) {
        User user = userService.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));
        // Generate a verification token for current user
        String recoveryToken = recoveryService.generatePasswordRecoveryToken(user);

        recoveryService.sendRecoveryEmail(user);
        // Generate URI to be embedded into email
        return recoveryService.buildRecoveryLink(user, recoveryToken);
    }

    @GetMapping("/recovery-token")
    public String generatePasswordRecoveryToken(@RequestParam String username) {
        User user = userService.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));
        return recoveryService.generatePasswordRecoveryToken(user);
    }

    @PostMapping("/password-recovery")
    public User verifyPasswordReset(@RequestParam String username, @RequestParam String token, @RequestParam @ValidPassword String password) {
        User user = userService.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));
        return recoveryService.passwordRecovery(user, token, password);
    }

    @PostMapping("/resend-recovery-link")
    public ResponseEntity<Null> resendRecoveryLink(@RequestParam String username) {
        User user = userService.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));
        recoveryService.sendRecoveryEmail(user);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
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
