package tech.eportfolio.server.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import tech.eportfolio.server.exception.UserNotFoundException;
import tech.eportfolio.server.model.User;
import tech.eportfolio.server.service.UserService;
import tech.eportfolio.server.service.VerificationService;

import javax.validation.constraints.Null;

@RestController
@RequestMapping("/verification")
public class VerificationController {

    private UserService userService;

    private VerificationService verificationService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }


    @GetMapping("/link")
    @ApiOperation(value = "", authorizations = {@Authorization(value = "JWT")})
    public String generateLink() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));
        // Generate a verification token for current user
        String verificationToken = verificationService.generateVerificationToken(user);
        // Generate URI to be embedded into email
        return verificationService.buildLink(user, verificationToken);
    }

    @GetMapping("/token")
    @ApiOperation(value = "", authorizations = {@Authorization(value = "JWT")})
    public String generateToken() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));
        return verificationService.generateVerificationToken(user);
    }

    @PostMapping("/verify")
    public User verify(@RequestParam("token") String token, @RequestParam String username) {
        User user = userService.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));
        return verificationService.verify(user, token);
    }

    @PostMapping("/resend")
    @ApiOperation(value = "", authorizations = {@Authorization(value = "JWT")})
    public ResponseEntity<Null> resend(@RequestParam String username) {
        User user = userService.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));
        verificationService.sendVerificationEmail(user);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }


}
