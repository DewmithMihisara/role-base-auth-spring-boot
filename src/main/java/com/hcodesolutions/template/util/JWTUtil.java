package com.hcodesolutions.template.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetails;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * @author Dewmith Mihisara
 * @date 2025-02-07
 * @since 0.0.1
 */
@Configuration
public class JWTUtil {

    @Value("${security.jwt.expire}")
    private long jwtExpire;

    @Value("${security.jwt.refresh.expire}")
    private long jwtRefreshExpire;

    @Value("${security.jwt.secret}")
    private String SecretKey;

    private final CommonUtils commonUtil;

    public JWTUtil(CommonUtils commonUtil) {
        this.commonUtil = commonUtil;
    }


    public String generateJwtToken(String username, Map<String,Object> hashMap) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpire))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .claim("Role", hashMap.get("Role"))
                .compact();

    }

    private Key getSigningKey() {
        byte[] keyBytes = this.SecretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Object extractSpecificClaim(String token,String claim) {
        return Jwts.parser().setSigningKey(getSigningKey()).parseClaimsJws(token).getBody().get(claim);
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
        return Jwts.parser().setSigningKey(getSigningKey()).parseClaimsJws(token).getBody();
    }

    public List<String> extractRoles(String request) {
        Claims claims = Jwts.parser()
                .setSigningKey(getSigningKey())
                .parseClaimsJws(commonUtil.removeTokenPrefix(request))
                .getBody();

        return claims.get("Role", List.class);
    }
}
