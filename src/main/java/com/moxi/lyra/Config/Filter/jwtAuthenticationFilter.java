package com.moxi.lyra.Config.Filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import com.moxi.lyra.Config.Utils.JwtUtil;
import com.moxi.lyra.User.CustomUserDetailsService;
import org.hibernate.annotations.Filter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@Slf4j
@Filter(name = "jwtAuthenticationFilter")
@Component
public class jwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private CustomUserDetailsService customUserDetailsService;



    public jwtAuthenticationFilter(JwtUtil jwtUtil , CustomUserDetailsService customUserDetailsService) {
        this.jwtUtil = jwtUtil;
        this.customUserDetailsService = customUserDetailsService;

    }


/**
* Token Filter
* Le Filter fait en sorte de recuperer le token dans la requete puis il extrait le nom d'utilisateur >
 * si le token est bon, on met le security context et ont stock ce token jusqu'à expiration (1h)
* @Filter
 * @Token
 */

@Override
protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    String token = jwtUtil.extractTokenFromRequest(request);
    String newToken = jwtUtil.checkToken(request);

    if (newToken != null) {
        response.setHeader("Authorization", "Bearer " + newToken);
        token = newToken;
    }

    if (token != null && jwtUtil.validateToken(token)) {
        String username = jwtUtil.extractUsername(token);
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
    if(token == null || !jwtUtil.validateToken(token)) {
        String moxiToken = jwtUtil.createMoxiToken();
        request = new CustomWrapper(request , moxiToken);
    }

    filterChain.doFilter(request, response);
}

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return path.startsWith("/ws") || path.startsWith("/socket.io/");
    }

    private void updateSecurityContext(HttpServletRequest request, String username) {
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
    }



