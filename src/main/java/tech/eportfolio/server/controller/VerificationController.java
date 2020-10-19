package tech.eportfolio.server.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import tech.eportfolio.server.common.exception.UserNotFoundException;
import tech.eportfolio.server.common.jsend.SuccessResponse;
import tech.eportfolio.server.model.User;
import tech.eportfolio.server.service.UserService;
import tech.eportfolio.server.service.VerificationService;

@RestController
@RequestMapping("/verification")
public class VerificationController {

    private final UserService userService;

    private final VerificationService verificationService;

    public VerificationController(@Qualifier("UserServiceCacheImpl") UserService userService, VerificationService verificationService) {
        this.userService = userService;
        this.verificationService = verificationService;
    }

    @GetMapping("/link")
    @ApiOperation(value = "", authorizations = {@Authorization(value = "JWT")})
    public ResponseEntity<SuccessResponse<String>> generateLink() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));
        // Generate a verification token for current user
        String verificationToken = verificationService.generateVerificationToken(user);
        // Generate URI to be embedded into email
        return new SuccessResponse<>("link", verificationService.buildLink(user, verificationToken)).toOk();
    }

    @GetMapping("/token")
    @ApiOperation(value = "", authorizations = {@Authorization(value = "JWT")})
    public ResponseEntity<SuccessResponse<String>> generateToken() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));
        return new SuccessResponse<>("token", verificationService.generateVerificationToken(user)).toOk();
    }

    @PostMapping("/verify")
    public ResponseEntity<SuccessResponse<Object>> verify(@RequestParam("token") String token, @RequestParam String username) {
        User user = userService.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));
        verificationService.verify(user, token);
        return new SuccessResponse<>().toOk();
    }

    @PostMapping("/resend")
    @ApiOperation(value = "", authorizations = {@Authorization(value = "JWT")})
    public ResponseEntity<SuccessResponse<Object>> resend(@RequestParam String username) {
        User user = userService.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));
        verificationService.sendVerificationEmail(user);
        return new SuccessResponse<>().toOk();
    }
}
