package tech.eportfolio.server.service.impl;

import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import tech.eportfolio.server.common.constant.RecoveryConstant;
import tech.eportfolio.server.common.utility.JWTTokenProvider;
import tech.eportfolio.server.model.UserStorageContainer.User;
import tech.eportfolio.server.model.UserStorageContainer.UserPrincipal;
import tech.eportfolio.server.service.EmailService;
import tech.eportfolio.server.service.RecoveryService;
import tech.eportfolio.server.service.UserService;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Service
public class RecoveryServiceImpl implements RecoveryService {

    private EmailService emailService;

    private JWTTokenProvider recoveryTokenProvider;

    private UserService userService;

    @Autowired
    public void setEmailService(EmailService emailService) {
        this.emailService = emailService;
    }

    @Autowired
    public void setRecoveryTokenProvider(JWTTokenProvider recoveryTokenProvider) {
        this.recoveryTokenProvider = recoveryTokenProvider;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public User passwordRecovery(@NotNull User user, @NotEmpty String token, String newPassword) {
        String secret = getPasswordRecoverySecret(user);
        JWTVerifier jwtVerifier = recoveryTokenProvider.getJWTVerifier(secret);
        try {
            if (recoveryTokenProvider.isTokenValid(user.getUsername(), token, secret)
                    && StringUtils.equals(jwtVerifier.verify(token).getSubject(), user.getUsername())) {
                return userService.changePassword(user, newPassword);
            }
        } catch (JWTVerificationException e) {
            throw new JWTVerificationException("Password recovery token is invalid or expired");
        }
        return user;
    }

    @Override
    public String buildRecoveryLink(@NotNull User user, String token) {
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .scheme(RecoveryConstant.SCHEME_HTTPS).
                        host(RecoveryConstant.HOST).path(RecoveryConstant.PATH).
                        queryParam(RecoveryConstant.TOKEN, token).
                        queryParam(RecoveryConstant.USERNAME, user.getUsername()).build();
        return uriComponents.toUriString();
    }

    @Override
    public void sendRecoveryEmail(@NotNull User user) {
        emailService.sendSimpleMessage(user.getEmail(),
                RecoveryConstant.EMAIL_TITLE,
                buildRecoveryEmailContent(user, buildRecoveryLink(user, generatePasswordRecoveryToken(user)))
        );
    }

    public String buildRecoveryEmailContent(User user, String link) {
        return String.format(RecoveryConstant.EMAIL_CONTENT, user.getUsername(), link);
    }

    @Override
    public String generatePasswordRecoveryToken(@NotNull User user) {
        return recoveryTokenProvider.generateJWTToken(new UserPrincipal(user), getPasswordRecoverySecret(user));
    }

    @Override
    public String getPasswordRecoverySecret(@NotNull User user) {
        return user.getPassword();
    }
}
