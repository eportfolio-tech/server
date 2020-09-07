package tech.eportfolio.server.controller;

import com.auth0.jwt.JWTVerifier;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import tech.eportfolio.server.constant.SecurityConstant;
import tech.eportfolio.server.constant.VerificationConstant;
import tech.eportfolio.server.dto.PasswordResetRequestBody;
import tech.eportfolio.server.exception.UserNotFoundException;
import tech.eportfolio.server.model.User;
import tech.eportfolio.server.service.UserService;
import tech.eportfolio.server.utility.JWTTokenProvider;

import javax.validation.Valid;
import javax.validation.constraints.Null;
import java.nio.file.AccessDeniedException;

@RestController
@RequestMapping("/verification")
public class VerificationController {

    private final UserService userService;

    private final JWTTokenProvider verificationTokenProvider;

    private final Environment environment;

    @Autowired
    public VerificationController(UserService userService, Environment environment, JWTTokenProvider verificationTokenProvider) {
        this.userService = userService;
        this.environment = environment;
        this.verificationTokenProvider = verificationTokenProvider;
    }

    @GetMapping("/link")
    @ApiOperation(value = "", authorizations = {@Authorization(value = "JWT")})
    public String generateLink() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));
        // Generate a verification token for current user
        String verificationToken = userService.generateVerificationToken(user);
        // Generate URI to be embedded into email
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .scheme(VerificationConstant.SCHEME_HTTPS).
                        host(VerificationConstant.HOST).path(VerificationConstant.PATH).
                        queryParam("token", verificationToken).
                        queryParam("username", username).build();
        return uriComponents.toUriString();
    }

    @GetMapping("/token")
    @ApiOperation(value = "", authorizations = {@Authorization(value = "JWT")})
    public String generateToken() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));
        return userService.generateVerificationToken(user);
    }

    @PostMapping("/verify")
    public User verify(@RequestParam("token") String token, @RequestParam String username) {
        User user = userService.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));
        return userService.verify(user, token);
    }

    @PostMapping("/verify-reset-password")
    public ResponseEntity<Null> verifyPasswordReset(@RequestParam("token") String token, @RequestParam String username,
                                                    @RequestBody @Valid PasswordResetRequestBody passwordResetRequestBody) throws AccessDeniedException {
        // TODO: Check whether the token the password has been changed
        User user = userService.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));
        String secret = user.getUsername() + user.getCreatedAt();
        JWTVerifier jwtVerifier = verificationTokenProvider.getJWTVerifier(secret);
        if (verificationTokenProvider.isTokenValid(user.getUsername(), token, secret)
                && StringUtils.equals(jwtVerifier.verify(token).getSubject(), user.getUsername())
                && userService.verifyPassword(user, passwordResetRequestBody.getOldPassword())) {
            userService.changePassword(user, passwordResetRequestBody.getNewPassword());
        } else {
            throw new AccessDeniedException(SecurityConstant.ACCESS_DENIED_MESSAGE);
        }
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

}
