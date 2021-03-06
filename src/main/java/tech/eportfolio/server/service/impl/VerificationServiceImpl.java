package tech.eportfolio.server.service.impl;

import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import tech.eportfolio.server.common.constant.Authority;
import tech.eportfolio.server.common.constant.Role;
import tech.eportfolio.server.common.constant.SecurityConstant;
import tech.eportfolio.server.common.constant.VerificationConstant;
import tech.eportfolio.server.common.exception.UserVerificationException;
import tech.eportfolio.server.common.utility.JWTTokenProvider;
import tech.eportfolio.server.model.User;
import tech.eportfolio.server.model.UserPrincipal;
import tech.eportfolio.server.service.EmailService;
import tech.eportfolio.server.service.UserService;
import tech.eportfolio.server.service.VerificationService;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Service
public class VerificationServiceImpl implements VerificationService {

    private EmailService emailService;

    private UserService userService;

    private JWTTokenProvider verificationTokenProvider;

    @Value("${server.host}")
    private String host;

    @Autowired
    public void setEmailService(EmailService emailService) {
        this.emailService = emailService;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setVerificationTokenProvider(JWTTokenProvider verificationTokenProvider) {
        this.verificationTokenProvider = verificationTokenProvider;
    }

    @Override
    public void sendVerificationEmail(User user) {
        emailService.sendSimpleMessage(user.getEmail(),
                VerificationConstant.EMAIL_TITLE,
                buildEmailContent(user, buildLink(user, generateVerificationToken(user)))
        );
    }

    public String buildEmailContent(User user, String link) {
        return String.format(VerificationConstant.EMAIL_CONTENT, user.getUsername(), link);
    }

    @Override
    public String buildLink(User user, String token) {
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .scheme(VerificationConstant.SCHEME_HTTPS).
                        host(host).path(VerificationConstant.PATH).
                        queryParam(VerificationConstant.TOKEN, token).
                        queryParam(VerificationConstant.USERNAME, user.getUsername()).build();
        return uriComponents.toUriString();
    }

    @Override
    public User verify(@NotNull User user, @NotEmpty String token) {
        if (StringUtils.equals(user.getRoles(), Role.ROLE_VERIFIED_USER.name())) {
            throw new UserVerificationException("User already verified");
        }
        String secret = getVerificationSecret(user);
        JWTVerifier jwtVerifier = verificationTokenProvider.getJWTVerifier(secret);
        try {
            if (verificationTokenProvider.isTokenValid(user.getUsername(), token, secret)
                    && StringUtils.equals(jwtVerifier.verify(token).getSubject(), user.getUsername())) {
                verify(user);
            }
        } catch (JWTVerificationException e) {
            throw new JWTVerificationException("Email verification token is invalid or expired");
        }
        return user;
    }

    @Override
    public User verify(@NotNull User user) {
        user.setRoles(Role.ROLE_VERIFIED_USER.name());
        user.setAuthorities(Authority.VERIFIED_USER_AUTHORITIES);
        return userService.save(user);
    }

    @Override
    public String generateVerificationToken(User user) {
        return verificationTokenProvider.generateJWTToken(
                new UserPrincipal(user),
                getVerificationSecret(user),
                SecurityConstant.EMAIL_VERIFICATION_TOKEN_VALIDITY);
    }

    public String getVerificationSecret(@NotNull User user) {
        return user.getEmail();
    }

}
