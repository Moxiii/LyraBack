package org.georges.georges.Config;

import org.georges.georges.User.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.firewall.StrictHttpFirewall;


@Configuration
@EnableWebSecurity

public class SecurityConfig  {
    @Autowired
    private CustomUserDetailsService customUserDetailsService;
    @Autowired
    private JwtUtil jwtUtil;




    @Bean
    public AuthenticationManager authManager(HttpSecurity http ) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(customUserDetailsService);
        return authenticationManagerBuilder.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();}
    @Bean
    public StrictHttpFirewall httpFirewall() {
        StrictHttpFirewall firewall = new StrictHttpFirewall();
        firewall.setAllowSemicolon(true); // Autoriser le point-virgule dans les URLs
        return firewall;
    }
    @SuppressWarnings("deprecated")
    @Bean
    public SecurityFilterChain securityfilterChain(HttpSecurity http) throws Exception {
        http
                .csrf()
                    .ignoringRequestMatchers("/api/**")
                    //.ignoringRequestMatchers(request -> request.getServletPath().contains("jsessionid"))
                    //.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()) // Utilisation de CookieCsrfTokenRepository
                .ignoringRequestMatchers("/chat/**")
                .and()
                .authorizeRequests()
                .requestMatchers("/private/**").permitAll()
                .requestMatchers("favicon.ico").denyAll()
                .requestMatchers("/api/**").permitAll()
                .requestMatchers("/admin/**").hasAnyRole("admin")
                .anyRequest().authenticated()
                .and()
                .addFilterBefore(new jwtAuthenticationFilter( jwtUtil , customUserDetailsService ), UsernamePasswordAuthenticationFilter.class)
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
                .and()
                .formLogin()
                    .loginPage("/api/auth/login" ) // Spécifiez l'URL de votre page de connexion personnalisée
                    .loginProcessingUrl("/process-login")
                    .defaultSuccessUrl("/index")
                    .failureUrl("/custom-login?error=true")
                    .permitAll()
                .successHandler(new SavedRequestAwareAuthenticationSuccessHandler())
                .and()
                    .logout()
                    .logoutSuccessUrl("/private/auth/logout")
                    .invalidateHttpSession(true)
                    .deleteCookies("JSESSIONID")
                    .permitAll();

        return http.build();
    }


}


