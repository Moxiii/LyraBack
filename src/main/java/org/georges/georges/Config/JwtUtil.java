package org.georges.georges.Config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.georges.georges.User.User;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
@Slf4j
@Component
public class JwtUtil {
    private final Map<String , String> tokenMap = new ConcurrentHashMap<>();
    private final SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);


    private final long TOKEN_VALIDITY = 60*60*1000;
    private final String TOKEN_HEADER = "Authorization";
    private final String TOKEN_PREFIX = "Bearer ";

    public JwtUtil() {
        this.jwtParser = Jwts.parser().setSigningKey(SECRET_KEY).build();
    }

    private final JwtParser jwtParser;


public String createToken(User user){
        Claims claims = Jwts.claims().setSubject(user.getUsername()).build();
        Date tokenCreateTime = new Date();
        Date tokenValidity = new Date(tokenCreateTime.getTime() + TimeUnit.MINUTES.toMillis(TOKEN_VALIDITY));
        String token =  Jwts.builder()
                .setClaims(claims)
                .setExpiration(tokenValidity)
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
        return token;
}

    private Claims parseJwtClaims(String token) {
        return jwtParser.parseClaimsJws(token).getBody();
    }



    public Boolean validateClaims(Claims claims) throws AuthenticationException{
    try{
        return claims.getExpiration().after(new Date());
}catch (Exception e) {
    throw e;
        }
    }



    public boolean validateToken(String token) {
    try{
        Claims claims = Jwts.parser().setSigningKey(SECRET_KEY).build().parseSignedClaims(token).getPayload();
        // Vérifiez si le token est expiré
        Date expirationDate = claims.getExpiration();
        Date currentDate = new Date();
        if (expirationDate.before(currentDate)) {
            return false;
        }
        return true;
    }catch (JwtException e) {
        log.warn("Le token a une erreur : {}", e.getMessage());
        // Journaliser la trace complète de l'exception pour un débogage plus approfondi si nécessaire
        log.debug("Trace complète de l'exception : ", e);
        return false;
    } catch (IllegalArgumentException e) {
        log.warn("L'argument du token est invalide : {}", e.getMessage());
        // Journaliser la trace complète de l'exception pour un débogage plus approfondi si nécessaire
        log.debug("Trace complète de l'exception : ", e);
        return false;
    }
    }
    public String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(TOKEN_HEADER);
        if(bearerToken != null && bearerToken.startsWith(TOKEN_PREFIX)){
            return bearerToken.substring(TOKEN_PREFIX.length());
        }return null;
    }
    public String extractTokenFromServerRequest(ServerHttpRequest request) {
        String bearerToken = request.getHeaders().getFirst(TOKEN_HEADER);
        if (bearerToken != null && bearerToken.startsWith(TOKEN_PREFIX)) {
            return bearerToken.substring(TOKEN_PREFIX.length());
        }
        return null;
    }
    public String extractUsername(String token) {
    Claims claims = parseJwtClaims(token);
    return claims.getSubject();
    }

    public String getValidToken(String username){
        String storedToken = tokenMap.get(username);
        if(storedToken != null && validateToken(storedToken)){
            return storedToken;
        }
        return null;
    }
    public void addToken(String username , String token){
        tokenMap.put(username , token);
    }
}
