package tech.eportfolio.server.security;

import org.springframework.beans.factory.annotation.Value;

public class SecurityConstant {
    @Value("jwt.expire")
    public static final long EXPIRATION_TIME = 432_000_000;
    public static final String TOKEN_HEADER = "Bearer ";
    public static final String JWT_TOKEN_HEADER = "X_JWT_Token";
    public static final String TOKEN_CANNOT_BE_VERIFIED = "Token can't be verified";
    public static final String ISSUER = "eportfolio.tech";
    public static final String AUDIENCE = "eportfolio user";
    public static final String AUTHORITIES = "Authorities";
    public static final String FORBIDDEN_MESSAGE = "You need to log in to access this resource";
    public static final String ACCESS_DENIED_MESSAGE = "You do not have permission to access this resource";
    public static final String[] PUBLIC_PATH = {
            // Swagger resources
            "/swagger-ui.html", "/v2/api-docs", "/configuration/ui", "/swagger-resources/**", "/configuration/**", "/webjars/**",
            // Authentication endpoints
            "/authentication/**"};

    private SecurityConstant() {
        throw new IllegalStateException("SecurityConstant is an Utility class");
    }

}
