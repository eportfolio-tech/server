package tech.eportfolio.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import tech.eportfolio.server.dto.UserDTO;
import tech.eportfolio.server.exception.UserNotFoundException;
import tech.eportfolio.server.exception.handler.AuthenticationExceptionHandler;
import tech.eportfolio.server.model.User;
import tech.eportfolio.server.model.UserPrincipal;
import tech.eportfolio.server.security.SecurityConstant;
import tech.eportfolio.server.service.UserService;
import tech.eportfolio.server.utility.JWTTokenProvider;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/authentication")
public class AuthenticationController extends AuthenticationExceptionHandler {

    private final UserService userService;

    private final AuthenticationManager authenticationManager;

    private final JWTTokenProvider jwtTokenProvider;

    @Autowired
    public AuthenticationController(UserService userService, AuthenticationManager authenticationManager, JWTTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/signup")
    public User signUp(@RequestBody @Valid UserDTO userDTO) {
        return userService.register(userService.fromUserDTO(userDTO));
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

    private HttpHeaders getJwtHeader(UserPrincipal userPrincipal) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(SecurityConstant.JWT_TOKEN_HEADER, jwtTokenProvider.generateJWTToken(userPrincipal));
        return httpHeaders;
    }

    private void authenticate(String username, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }

}
