package org.georges.georges.Auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.georges.georges.Calendar.Calendar;
import org.georges.georges.Calendar.CalendarService;
import org.georges.georges.Config.Utils.JwtUtil;
import org.georges.georges.Config.TokenManager;
import org.georges.georges.DTO.LoginRes;
import org.georges.georges.User.User;
import org.georges.georges.User.UserRole.UserRole;
import org.georges.georges.User.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;


@RestController
@RequestMapping("api/auth")
public class AuthApiController {
    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private TokenManager tokenManager;
    @Autowired
    private CalendarService calendarService;

@PostMapping("/register")
    public ResponseEntity<String> createUser(@RequestBody User user) {
        try {
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String encodedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(encodedPassword);
            UserRole defaultRole = new UserRole("user", "user", 1l);
            user.setUserRole(defaultRole);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            String formattedDate = dateFormat.format(new Date());
            user.setDateInscription(formattedDate);
            Calendar calendar = new Calendar();
            calendar.setUser(user);
            user.setCalendar(calendar);
            calendarService.saveCalendar(calendar);
            userService.saveUser(user);
            return new ResponseEntity<>("User creer avec sucess", HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Erreur dans la creation de l'user ", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(path = {"/login"})
    @ResponseBody
    public ResponseEntity<?> login(@RequestBody User user, HttpServletRequest req) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        User existingUser = null;
        if (user.getEmail() != null) {
            existingUser = userService.findByEmail(user.getEmail().toLowerCase());
        } else if (user.getUsername() != null) {
            existingUser = userService.findByUsername(user.getUsername().toLowerCase());
        }

        if (existingUser != null) {
            if (passwordEncoder.matches(user.getPassword(), existingUser.getPassword())) {
                String validToken = tokenManager.getValidToken(existingUser.getUsername());
                if (validToken != null) {
                    return ResponseEntity.ok(new LoginRes(existingUser.getUsername(), validToken));
                } else {

                    UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(existingUser.getUsername(), user.getPassword());

                    try {
                        Authentication authentication = authenticationManager.authenticate(token);
                        if (authentication != null && authentication.isAuthenticated()) {
                            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                            SecurityContext sc = SecurityContextHolder.getContext();
                            sc.setAuthentication(authentication);
                            HttpSession session = req.getSession(true);
                            String accessToken = jwtUtil.createAccessToken(user);
                            String refreshToken = jwtUtil.createRefreshToken(user);
                            tokenManager.addToken(user.getUsername() + "_refresh",refreshToken);
                            tokenManager.addToken(user.getUsername(), accessToken);
                            return ResponseEntity.ok(new LoginRes(existingUser.getUsername(), accessToken));
                        }
                    } catch (AuthenticationException e) {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed");
                    }
                }
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Incorrect password");
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
    }

@DeleteMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest req) {
        String token = jwtUtil.extractTokenFromRequest(req);
        if (token != null) {
            String username = jwtUtil.extractUsername(token);
            if (username != null) {
                tokenManager.removeToken(username);
                SecurityContextHolder.getContext().setAuthentication(null);
                return ResponseEntity.status(HttpStatus.OK).body("User logged out successfully");
            }
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
}
@PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(HttpServletRequest req) {
        String token = jwtUtil.extractTokenFromRequest(req);
        if(token != null) {
            String newAccessToken = jwtUtil.checkToken(req);
            if (newAccessToken != null) {
                String username = jwtUtil.extractUsername(newAccessToken);
                return ResponseEntity.ok(new LoginRes(username, newAccessToken));
            }else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
            }
        }

    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing token");
}

@GetMapping("/check-token")
    public ResponseEntity<?> checkToken(HttpServletRequest req) {
        String token = jwtUtil.extractTokenFromRequest(req);
        if (token != null && jwtUtil.validateToken(token)) {
            return ResponseEntity.ok(new LoginRes(jwtUtil.extractUsername(token), token));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
}
}


