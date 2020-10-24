package tech.eportfolio.server.controller;

import com.auth0.jwt.exceptions.JWTVerificationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import tech.eportfolio.server.common.constant.SecurityConstant;
import tech.eportfolio.server.common.constraint.ValidPassword;
import tech.eportfolio.server.common.exception.EmailNotFoundException;
import tech.eportfolio.server.common.exception.UserNotFoundException;
import tech.eportfolio.server.common.exception.handler.AuthenticationExceptionHandler;
import tech.eportfolio.server.common.jsend.SuccessResponse;
import tech.eportfolio.server.common.unsplash.Client;
import tech.eportfolio.server.common.utility.JWTTokenProvider;
import tech.eportfolio.server.dto.LoginRequestBody;
import tech.eportfolio.server.dto.RenewRequestBody;
import tech.eportfolio.server.dto.UserDTO;
import tech.eportfolio.server.model.User;
import tech.eportfolio.server.model.UserPrincipal;
import tech.eportfolio.server.service.RecoveryService;
import tech.eportfolio.server.service.UserFollowService;
import tech.eportfolio.server.service.UserService;
import tech.eportfolio.server.service.VerificationService;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@Validated
@RestController
@RequestMapping("/authentication")
public class AuthenticationController extends AuthenticationExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final UserService userService;

    private final AuthenticationManager authenticationManager;

    private final VerificationService verificationService;

    private final JWTTokenProvider jwtTokenProvider;

    private final RecoveryService recoveryService;

    private final Client unsplashClient;

    @Value("$security.jwt.token.sign")
    private String signKey;

    @Value("$security.jwt.token.refresh")
    private String refreshKey;


    @Autowired
    public AuthenticationController(@Qualifier("UserServiceCacheImpl") UserService userService, AuthenticationManager authenticationManager, VerificationService verificationService, JWTTokenProvider jwtTokenProvider, RecoveryService recoveryService, UserFollowService userFollowService, Client unsplashClient) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.verificationService = verificationService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.recoveryService = recoveryService;
        this.unsplashClient = unsplashClient;
    }


    @PostMapping("/signup")
    public ResponseEntity<SuccessResponse<Object>> signUp(@RequestBody @Valid UserDTO userDTO) {
        User user = userService.register(userService.fromUserDTO(userDTO), true);
        UserPrincipal userPrincipal = new UserPrincipal(user);
        Map<String, Object> responseMap = generateTokens(userPrincipal);
        responseMap.put("user", user);
        verificationService.sendVerificationEmail(user);
        return new SuccessResponse<>(responseMap).toOk();
    }

    @PostMapping("/login")
    public ResponseEntity<SuccessResponse<Object>> login(@RequestBody @Valid LoginRequestBody loginRequestBody) {
        String username = loginRequestBody.getUsername();
        User loginUser = userService.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));
        authenticate(username, loginRequestBody.getPassword());
        UserPrincipal userPrincipal = new UserPrincipal(loginUser);
        Map<String, Object> tokens = generateTokens(userPrincipal);
        tokens.put("user", loginUser);
        return new SuccessResponse<>(tokens).toOk();
    }

    private Map<String, Object> generateTokens(UserPrincipal user) {
        Map<String, Object> tokens = new HashMap<>();
        tokens.put(SecurityConstant.ACCESS_TOKEN, jwtTokenProvider.generateAccessToken(user));
        tokens.put(SecurityConstant.REFRESH_TOKEN, jwtTokenProvider.generateRefreshToken(user));
        return tokens;
    }

    @PostMapping("/renew")
    public ResponseEntity<SuccessResponse<Object>> renewToken(@RequestBody @Valid RenewRequestBody renewRequestBody) {
        String username = jwtTokenProvider.getSubject(renewRequestBody.getRefreshToken(), refreshKey);
        User refreshUser = userService.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));
        if (!jwtTokenProvider.isTokenValid(username, renewRequestBody.getRefreshToken(), refreshKey)) {
            throw new JWTVerificationException("Refresh token has expired");
        }
        UserPrincipal userPrincipal = new UserPrincipal(refreshUser);
        logger.info("Access token renewed for user: {}", username);
        return new SuccessResponse<>(generateTokens(userPrincipal)).toCreated();
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

    private void authenticate(String username, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }

}
