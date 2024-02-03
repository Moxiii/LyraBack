package org.georges.georges.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;


@Configuration
@EnableWebSecurity

public class SecurityConfig  {
    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.inMemoryAuthentication().withUser("user").password("password").roles("ADMIN");
        return authenticationManagerBuilder.build();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();}

    @Bean
    public SecurityFilterChain securityfilterChain(HttpSecurity http) throws Exception {
        http
                .csrf()
                .ignoringRequestMatchers("/api/**")
                    .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()) // Utilisation de CookieCsrfTokenRepository
                .and()
                .authorizeRequests()
                .requestMatchers("/auth/login" , "/auth/register").permitAll()
                .requestMatchers("/favicon.ico").permitAll()
                .requestMatchers("/api/user/**").permitAll()
                //.requestMatchers("/","/index").authenticated()
                .requestMatchers("/admin/**").hasAnyRole("admin")
                .anyRequest().authenticated()
                .and()
                .formLogin()
                    .loginPage("/auth/login") // Spécifiez l'URL de votre page de connexion personnalisée
                    .loginProcessingUrl("/process-login")
                    .defaultSuccessUrl("/index")
                    .failureUrl("/custom-login?error=true")
                    .permitAll()
                .and()
                    .logout()
                    .logoutSuccessUrl("/auth/logout")
                    .invalidateHttpSession(true)
                    .deleteCookies("JSESSIONID")
                    .permitAll();

        return http.build();
    }
}


