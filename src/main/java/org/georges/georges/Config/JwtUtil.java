package org.georges.georges.Config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.georges.georges.User.User;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.concurrent.TimeUnit;
@Slf4j
@Component
public class JwtUtil {
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
        log.info("le token est {}" , token);
        log.info("La  clee secrete pour creer le token est :{}",SECRET_KEY);
        return token;
}

    private Claims parseJwtClaims(String token) {
    log.info("LES CLAIMS SONT :{}", jwtParser.parseSignedClaims(token).getBody());
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
    log.info("TOKEN A VALIDER :{}", token);
    log.info("La clee pour valider le token est :{}" , SECRET_KEY);
    try{
        Claims claims = Jwts.parser().setSigningKey(SECRET_KEY).build().parseSignedClaims(token).getPayload();
        log.info("Le PARSER : {}" , Jwts.parser().setSigningKey(SECRET_KEY).build().parseSignedClaims(token).getPayload());
        log.info("Informations extraites du token : {}", claims);

        // Vérifiez si le token est expiré
        Date expirationDate = claims.getExpiration();
        Date currentDate = new Date();
        log.info("Date d'expiration du token : {}", expirationDate);
        log.info("Date actuelle : {}", currentDate);
        if (expirationDate.before(currentDate)) {
            log.warn("Le token est expiré !");
            return false;
        }
        return true;
    }catch (JwtException | IllegalArgumentException e ){
        log.warn("Le token a une erreur :{}" , e.getMessage());
        return false;
    }
    }
    public String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(TOKEN_HEADER);
        if(bearerToken != null && bearerToken.startsWith(TOKEN_PREFIX)){
            log.warn("Le SAINT token est :{}" , bearerToken.substring(TOKEN_PREFIX.length()));
            return bearerToken.substring(TOKEN_PREFIX.length());
        }return null;
    }

    public String extractUsername(String token) {
    Claims claims = parseJwtClaims(token);
    log.info("Le nom extrait est :{}" , claims.getSubject());
    return claims.getSubject();
    }
}
