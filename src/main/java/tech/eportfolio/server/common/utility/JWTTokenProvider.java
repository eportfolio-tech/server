package tech.eportfolio.server.common.utility;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import tech.eportfolio.server.common.constant.SecurityConstant;
import tech.eportfolio.server.model.UserPrincipal;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JWTTokenProvider {
    public String generateAccessToken(UserPrincipal userPrincipal) {
        return generateJWTToken(userPrincipal,
                SecurityConstant.AUTHENTICATION_SECRET,
                SecurityConstant.ACCESS_TOKEN_VALIDITY);
    }

    public String generateRefreshToken(UserPrincipal userPrincipal) {
        return generateJWTToken(userPrincipal,
                SecurityConstant.REFRESH_SECRET,
                SecurityConstant.REFRESH_TOKEN_VALIDITY);
    }

    public String generateJWTToken(UserPrincipal userPrincipal, String secret, Period validity) {
        DateTime now = new DateTime();
        DateTime expire = now.plus(validity);

        String[] claims = getClaimsFromUser(userPrincipal);
        return JWT.create().withIssuer(SecurityConstant.ISSUER).
                withAudience(SecurityConstant.AUDIENCE).
                withIssuedAt(now.toDate()).
                withSubject(userPrincipal.getUsername()).
                withArrayClaim(SecurityConstant.AUTHORITIES, claims).
                withExpiresAt(expire.toDate()).
                sign(Algorithm.HMAC512(secret.getBytes()));
    }

    public String[] getClaimsFromUser(UserPrincipal userPrincipal) {
        return userPrincipal.getAuthorities().stream().map(GrantedAuthority::getAuthority).toArray(String[]::new);
    }

    public List<GrantedAuthority> getAuthorities(String token, String secret) {
        String[] claims = getClaimsFromToken(token, secret);
        return Arrays.stream(claims).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    private String[] getClaimsFromToken(String token, String secret) {
        JWTVerifier jwtVerifier = getJWTVerifier(secret);
        return jwtVerifier.verify(token).getClaim(SecurityConstant.AUTHORITIES).asArray(String.class);
    }

    public Authentication getAuthentication(String username, List<GrantedAuthority> authorities, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(username, null, authorities);
        usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        return usernamePasswordAuthenticationToken;
    }

    public boolean isTokenValid(String username, String token, String secret) {
        JWTVerifier jwtVerifier = getJWTVerifier(secret);
        return StringUtils.isNotEmpty(username) && !isTokenExpired(jwtVerifier, token);
    }

    public String getSubject(String token, String secret) {
        JWTVerifier verifier = getJWTVerifier(secret);
        return verifier.verify(token).getSubject();
    }


    public boolean isTokenExpired(JWTVerifier jwtVerifier, String token) {
        Date expiration = jwtVerifier.verify(token).getExpiresAt();
        return expiration.before(new Date());
    }

    public JWTVerifier getJWTVerifier(String secret) {
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
