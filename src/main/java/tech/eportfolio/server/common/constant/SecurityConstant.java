package tech.eportfolio.server.common.constant;

import org.springframework.beans.factory.annotation.Value;

public class SecurityConstant {


    @Value("jwt.expire")
    public static final long EXPIRATION_TIME = 1_800_000; // stands for 30 minutes

    public static final String AUTHENTICATION_BASE_PATH = "/authentication";
    // Authentication endpoints
    public static final String[] AUTHENTICATION = {
            AUTHENTICATION_BASE_PATH + "/login",
            AUTHENTICATION_BASE_PATH + "/loginAsTest",
            AUTHENTICATION_BASE_PATH + "/deleteTest",
            AUTHENTICATION_BASE_PATH + "/password-recovery",
            AUTHENTICATION_BASE_PATH + "/quick-test",
            AUTHENTICATION_BASE_PATH + "/recovery-link",
            AUTHENTICATION_BASE_PATH + "/recovery-token",
            AUTHENTICATION_BASE_PATH + "/send-recovery-link",
            AUTHENTICATION_BASE_PATH + "/signup"
    };

    public static final String TOKEN_HEADER = "Bearer ";
    public static final String JWT_TOKEN_HEADER = "X-JWT-Token";
    public static final String TOKEN_CANNOT_BE_VERIFIED = "Token can't be verified";
    public static final String ISSUER = "eportfolio.tech";
    public static final String AUDIENCE = "eportfolio user";
    public static final String AUTHORITIES = "Authorities";
    public static final String FORBIDDEN_MESSAGE = "You need to log in to access this resource";
    public static final String ACCESS_DENIED_MESSAGE = "You do not have permission to access this resource";
    // Swagger resources
    public static final String[] API_DOC = {"/swagger-ui.html", "/v2/api-docs", "/configuration/ui", "/swagger-resources/**", "/configuration/**", "/webjars/**", "/h2-console/**"};

    // TODO: Move JWT secret to properties file
    public static final String AUTHENTICATION_SECRET = "This is a JWT secret";

    public static final String[] POST_ONLY = {"/blobs/image/**", "/verification/verify/**"};
    public static final String[] GET_ONLY = {"/tags/", "/portfolios/search/**", "/portfolios/{username}"};


    private SecurityConstant() {
        throw new IllegalStateException("SecurityConstant is an Utility class");
    }

}
