package org.georges.georges.Config;

import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletRequest;
import org.georges.georges.User.User;

import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import java.util.Date;
import java.util.concurrent.TimeUnit;
@Component
public class JwtUtil {
    private final String SECRET_KEY = "test";
    private final long TOKEN_VALIDITY = 60*60*1000;
    private final String TOKEN_HEADER = "Authorization";
    private final String TOKEN_PREFIX = "Bearer ";

    private final JwtParser jwtParser;

    public JwtUtil(){
        this.jwtParser = (JwtParser) Jwts.parser().setSigningKey(SECRET_KEY);
    }
public String createToken(User user){
        Claims claims = (Claims) Jwts.claims().setSubject(user.getEmail());
        claims.put("Username", user.getUsername());
        Date tokenCreateTime = new Date();
        Date tokenValidity = new Date(tokenCreateTime.getTime() + TimeUnit.MINUTES.toMillis(TOKEN_VALIDITY));
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(tokenValidity)
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
}

    private Claims parseJwtClaims(String token) {
        return jwtParser.parseClaimsJws(token).getBody();
    }
public String resoleToken(HttpServletRequest req){
        String bearerToken = req.getHeader(TOKEN_HEADER);
        if(bearerToken != null && bearerToken.startsWith(TOKEN_PREFIX)){
        return bearerToken.substring(TOKEN_PREFIX.length());

        }
        return null;
}

public Claims resolveToken(HttpServletRequest req){
        try{
            String token = resoleToken(req);
            if(token != null){
                return parseJwtClaims(token);
            }
                return null;
        }
        catch (ExpiredJwtException ex){
            req.setAttribute("expired" , ex.getMessage());
            throw ex;
        }
        catch (Exception ex){
            req.setAttribute("expired" , ex.getMessage());
            throw ex;
        }
    }
    public Boolean validateClaims(Claims claims) throws AuthenticationException{
    try{
        return claims.getExpiration().after(new Date());
}catch (Exception e) {
    throw e;
        }
    }

    public String getEmail(Claims claims){
        return claims.getSubject();
    }

}
