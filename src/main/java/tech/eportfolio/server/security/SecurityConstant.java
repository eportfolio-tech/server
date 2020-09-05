package tech.eportfolio.server.security;

import org.springframework.beans.factory.annotation.Value;

public class SecurityConstant {
    @Value("jwt.expire")
    public static final long EXPIRATION_TIME = 432_000_000;
    public static final String TOKEN_HEADER = "Bearer ";
    public static final String JWT_TOKEN_HEADER = "X-JWT-Token";
    public static final String TOKEN_CANNOT_BE_VERIFIED = "Token can't be verified";
    public static final String ISSUER = "eportfolio.tech";
    public static final String AUDIENCE = "eportfolio user";
    public static final String AUTHORITIES = "Authorities";
    public static final String FORBIDDEN_MESSAGE = "You need to log in to access this resource";
    public static final String ACCESS_DENIED_MESSAGE = "You do not have permission to access this resource";
    // Swagger resources
    public static final String[] API_DOC = {"/swagger-ui.html", "/v2/api-docs", "/configuration/ui", "/swagger-resources/**", "/configuration/**", "/webjars/**"};
    // Authentication endpoints
    public static final String AUTHENTICATION = "/authentication/**";

    public static final String[] GET_ONLY = {"/tags/"};

    private SecurityConstant() {
        throw new IllegalStateException("SecurityConstant is an Utility class");
    }

}
