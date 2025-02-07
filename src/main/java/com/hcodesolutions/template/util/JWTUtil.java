package com.hcodesolutions.template.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import java.util.Map;
import java.util.function.Function;

/**
 * @author Dewmith Mihisara
 * @date 2025-02-07
 * @since
 */
@Configuration
public class JWTUtil {

    @Value("${security.jwt.expire}")
    private long jwtExpire;

    @Value("${security.jwt.refresh.expire}")
    private long jwtRefreshExpire;

    @Value("${security.jwt.secret}")
    private String SecretKey;


    public String generateJwtToken(String username, Map<String,Object> hashMap) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpire))
                .signWith(SignatureAlgorithm.HS512, SecretKey)
                .compact();

    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Object extractSpecificClaim(String token,String claim) {
        return Jwts.parser().setSigningKey(SecretKey).parseClaimsJws(token).getBody().get(claim);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(SecretKey).parseClaimsJws(token).getBody();
    }
}
