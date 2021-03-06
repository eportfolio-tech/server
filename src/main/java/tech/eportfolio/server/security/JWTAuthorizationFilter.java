package tech.eportfolio.server.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import tech.eportfolio.server.common.constant.SecurityConstant;
import tech.eportfolio.server.common.utility.JWTTokenProvider;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Component
public class JWTAuthorizationFilter extends OncePerRequestFilter {

    private final JWTTokenProvider jwtTokenProvider;

    @Value("$security.jwt.token.sign")
    private String signKey;

    @Value("$security.jwt.token.refresh")
    private String refreshKey;

    public JWTAuthorizationFilter(JWTTokenProvider tokenProvider) {
        this.jwtTokenProvider = tokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Allow OPTIONS request
        if (request.getMethod().equalsIgnoreCase(HttpMethod.OPTIONS.name())) {
            response.setStatus(HttpStatus.OK.value());
        } else {
            String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            // Pass to next filter if authorization header is missing or if malformed
            if (authorizationHeader == null || !authorizationHeader.startsWith(SecurityConstant.TOKEN_HEADER)) {
                filterChain.doFilter(request, response);
                return;
            }
            // Remove bearer from authentication header
            String token = authorizationHeader.substring(SecurityConstant.TOKEN_HEADER.length());
            // Retrieve username from JWT
            String username = jwtTokenProvider.getSubject(token, signKey);
            // Set SecurityContext if JWT token is valid and there is no authentication in SecurityContext
            if (jwtTokenProvider.isTokenValid(username, token, signKey) && SecurityContextHolder.getContext().getAuthentication() == null) {
                List<GrantedAuthority> authorityList = jwtTokenProvider.getAuthorities(token, signKey);
                Authentication authentication = jwtTokenProvider.getAuthentication(username, authorityList, request);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                logger.error(String.format("JWT verification failed %s %s", username, token));
                // otherwise clean up SecurityContext
                SecurityContextHolder.clearContext();
            }

        }
        // Pass the request to the next filter
        filterChain.doFilter(request, response);
    }
}
