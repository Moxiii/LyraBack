package com.moxi.lyra.Config.Utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import com.moxi.lyra.User.User;
import com.moxi.lyra.User.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class JwtUtil {
    private final Map<String , String> tokenMap = new ConcurrentHashMap<>();
    private final SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private final long ACCESS_TOKEN_VALIDITY = 60*60*1000;
    private final long REFRESH_TOKEN_VALIDITY = 24 * 60 * 60 * 1000;
    private final String TOKEN_HEADER = "Authorization";
    private final String TOKEN_PREFIX = "Bearer ";
    private final UserService userService;
    public JwtUtil(UserService userService) {
        this.jwtParser = Jwts.parser().setSigningKey(SECRET_KEY).build();
        this.userService = userService;
    }
    private final JwtParser jwtParser;

@Bean
    public JwtDecoder jwtDecoder() {
    return NimbusJwtDecoder.withSecretKey(SECRET_KEY).build();
}

    public String createMoxiToken(){
        User moxi = userService.findByUsername("moxi");
        if (moxi == null) {
            throw new RuntimeException("Utilisateur moxi non trouvé");
        }
        return createAccessToken(moxi);
    }
    public String createAccessToken(User user){
        Claims claims = Jwts.claims().setSubject(user.getUsername()).build();
        Date tokenCreateTime = new Date();
        Date tokenValidity = new Date(tokenCreateTime.getTime() + ACCESS_TOKEN_VALIDITY);
        String accesToken =  Jwts.builder()
                .setClaims(claims)
                .setExpiration(tokenValidity)
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
        return accesToken;
}
public String createRefreshToken(User user){
    Claims claims = Jwts.claims().setSubject(user.getUsername()).build();
    Date tokenCreateTime = new Date();
    Date tokenValidity = new Date(tokenCreateTime.getTime() + REFRESH_TOKEN_VALIDITY);
    String refreshToken =  Jwts.builder()
            .setClaims(claims)
            .setExpiration(tokenValidity)
            .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
            .compact();
    return refreshToken;
}
    private Claims parseJwtClaims(String token) {
        return jwtParser.parseClaimsJws(token).getBody();
    }


public boolean isTokenExpired(String token) {
    Claims claims = Jwts.parser()
            .setSigningKey(SECRET_KEY).
            build().parseClaimsJws(token)
            .getPayload();
    Date expiration = claims.getExpiration();
    return expiration.before(new Date());
}

    public boolean validateToken(String token) {
    try{
        Jwts.parser().setSigningKey(SECRET_KEY).build().parseClaimsJws(token).getPayload();
        return true;
    }catch (JwtException e) {
        return false;
    } catch (IllegalArgumentException e) {
        return false;
    } catch (Exception e) {
        return false;
    }
    }
    public String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(TOKEN_HEADER);
        if(bearerToken != null && bearerToken.startsWith(TOKEN_PREFIX)){
            return bearerToken.substring(TOKEN_PREFIX.length());
        }return null;
    }

    public String extractUsername(String token) {
    Claims claims = parseJwtClaims(token);
    return claims.getSubject();
    }


    public void addToken(String username , String token){
        tokenMap.put(username , token);
    }

    public String checkToken(HttpServletRequest request){
       String token = extractTokenFromRequest(request);
       if(validateToken(token) && token != null){
           if(isTokenExpired(token)){
               String username = extractUsername(token);
               User currentUser = userService.findByUsername(username);
               if(currentUser != null){
                   String refreshToken = tokenMap.get(currentUser.getUsername()+ "_refresh");
                   if(refreshToken != null && validateToken(refreshToken)){
                       String newAccessToken =  createAccessToken(currentUser);
                       addToken(currentUser.getUsername(), newAccessToken);
                       return newAccessToken;
                   }
               }
           }
           return token;

           }
          return null;
    }

}
