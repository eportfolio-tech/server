package tech.eportfolio.server.utility;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import tech.eportfolio.server.model.UserPrincipal;
import tech.eportfolio.server.security.SecurityConstant;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JWTTokenProvider {
    @Value("jwt.secret")
    private String secret;

    public String generateJWTToken(UserPrincipal userPrincipal) {
        String[] claims = getClaimsFromUser(userPrincipal);
        return JWT.create().withIssuer(SecurityConstant.ISSUER).
                withAudience(SecurityConstant.AUDIENCE).
                withIssuedAt(new Date()).
                withSubject(userPrincipal.getUsername()).
                withArrayClaim(SecurityConstant.AUTHORITIES, claims).
                withExpiresAt(new Date(System.currentTimeMillis() + SecurityConstant.EXPIRATION_TIME)).
                sign(Algorithm.HMAC512(secret.getBytes()));

    }

    public String[] getClaimsFromUser(UserPrincipal userPrincipal) {
        List<String> authorities = userPrincipal.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
        return authorities.toArray(new String[0]);
    }

    public List<GrantedAuthority> getAuthorities(String token) {
        String[] claims = getClaimsFromToken(token);
        return Arrays.stream(claims).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    private String[] getClaimsFromToken(String token) {
        JWTVerifier jwtVerifier = getJWTVerifier();
        return jwtVerifier.verify(token).getClaim(SecurityConstant.AUTHORITIES).asArray(String.class);
    }

    public Authentication getAuthentication(String username, List<GrantedAuthority> authorities, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(username, null, authorities);
        usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        return usernamePasswordAuthenticationToken;
    }

    public boolean isTokenValid(String username, String token) {
        JWTVerifier jwtVerifier = getJWTVerifier();
        return StringUtils.isNotEmpty(username) && !isTokenExpired(jwtVerifier, token);
    }

    public String getSubject(String token) {
        JWTVerifier verifier = getJWTVerifier();
        return verifier.verify(token).getSubject();
    }


    public boolean isTokenExpired(JWTVerifier jwtVerifier, String token) {
        Date expiration = jwtVerifier.verify(token).getExpiresAt();
        return expiration.before(new Date());
    }

    public JWTVerifier getJWTVerifier() {
        JWTVerifier jwtVerifier;
        try {
            Algorithm algorithm = Algorithm.HMAC512(secret.getBytes());
            jwtVerifier = JWT.require(algorithm).withIssuer(SecurityConstant.ISSUER).build();
        } catch (JWTVerificationException exception) {
            throw new JWTVerificationException(SecurityConstant.TOKEN_CANNOT_BE_VERIFIED);
        }
        return jwtVerifier;
    }

}
