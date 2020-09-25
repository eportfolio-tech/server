package tech.eportfolio.server.common.constant;

import org.joda.time.Period;
import org.springframework.beans.factory.annotation.Value;

public class SecurityConstant {


    @Value("jwt.expire")
    public static final long EXPIRATION_TIME = 1_800_000; // stands for 30 minutes
    //    public static final long EXPIRATION_TIME = 30000; // stands for 30 seconds
    public static final String AUTHENTICATION = "/authentication/**";
    public static final String TOKEN_HEADER = "Bearer ";
    public static final String JWT_TOKEN_HEADER = "X-JWT-Token";
    public static final String TOKEN_CANNOT_BE_VERIFIED = "Token can't be verified";
    public static final String ISSUER = "eportfolio.tech";
    public static final String AUDIENCE = "eportfolio user";
    public static final String AUTHORITIES = "Authorities";
    public static final String ACCESS_TOKEN = "access-token";
    public static final String REFRESH_TOKEN = "refresh-token";
    public static final String FORBIDDEN_MESSAGE = "You need to log in to access this resource";
    public static final String ACCESS_DENIED_MESSAGE = "You do not have permission to access this resource";
    // Swagger resources
    public static final String[] API_DOC = {"/swagger-ui.html", "/v2/api-docs", "/configuration/ui", "/swagger-resources/**", "/configuration/**", "/webjars/**", "/h2-console/**"};

    // TODO: Move JWT secret to properties file
    public static final String AUTHENTICATION_SECRET = "This is a JWT secret";
    public static final String REFRESH_SECRET = "This is a refresh token secret";

    public static final String[] POST_ONLY = {"/blobs/image/**", "/verification/verify/**"};
    public static final String[] GET_ONLY = {"/tags/", "/portfolios/search/**", "/portfolios/{username}"};
    public static final Period REFRESH_TOKEN_VALIDITY = Period.weeks(2);
    public static final Period ACCESS_TOKEN_VALIDITY = Period.seconds(5);
    public static final Period PASSWORD_RECOVERY_TOKEN_VALIDITY = Period.minutes(15);
    public static final Period EMAIL_VERIFICATION_TOKEN_VALIDITY = Period.hours(12);


    private SecurityConstant() {
        throw new IllegalStateException("SecurityConstant is an Utility class");
    }

}
