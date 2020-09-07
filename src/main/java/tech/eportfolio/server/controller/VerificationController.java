package tech.eportfolio.server.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import tech.eportfolio.server.exception.UserNotFoundException;
import tech.eportfolio.server.model.User;
import tech.eportfolio.server.service.UserService;

import java.net.InetAddress;

@RestController
@RequestMapping("/verification")
public class VerificationController {

    private final UserService userService;

    private final Environment environment;

    @Autowired
    public VerificationController(UserService userService, Environment environment) {
        this.userService = userService;
        this.environment = environment;
    }

    @GetMapping("/link")
    @ApiOperation(value = "", authorizations = {@Authorization(value = "JWT")})
    public String generateLink() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));
        String verificationToken = userService.generateVerificationToken(user);
        // TODO: Use Spring application properties to use https on staging and production
        String port = environment.getProperty("server.port");
        String hostname = InetAddress.getLoopbackAddress().getHostName();
        // TODO: Use Something like Path Variable to replace string formatter
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .scheme("http").host(hostname).path(String.format("/users/%s/verify", username)).port(port).
                        queryParam("token", verificationToken).build();
        return uriComponents.toUriString();
    }

    @GetMapping("/token")
    @ApiOperation(value = "", authorizations = {@Authorization(value = "JWT")})
    public String generateToken() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));
        return userService.generateVerificationToken(user);
    }
}
