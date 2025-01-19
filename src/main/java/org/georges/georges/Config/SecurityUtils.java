package org.georges.georges.Config;

import jakarta.servlet.http.HttpServletRequest;
import org.georges.georges.User.CustomUserDetails;
import org.georges.georges.User.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class SecurityUtils {
    public static User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            if (userDetails instanceof CustomUserDetails) {
                CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
                return customUserDetails.getUser();
            }
        }
        return null;
    }

    public static boolean isAuthorized(HttpServletRequest request , JwtUtil jwtUtil){
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = jwtUtil.extractTokenFromRequest(request);
            if (jwtUtil != null && jwtUtil.validateToken(token)) {
                return true;
            }
        }
        return false;
    }
}
