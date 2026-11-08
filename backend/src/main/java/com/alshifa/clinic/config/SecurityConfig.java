package com.alshifa.clinic.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

/**
 * Spring Security setup:
 *  - every /api/** call needs a logged-in user (HTTP Basic auth)
 *  - only ADMIN users may delete appointments
 *  - stateless: no server session, credentials sent on each request
 * Users are kept in memory to keep the demo simple; a real system would
 * load them from the database.
 */
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CSRF protection is for browser sessions with cookies; this API is stateless.
                .csrf(csrf -> csrf.disable())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(antMatcher("/h2-console/**")).permitAll()
                        .requestMatchers(antMatcher(HttpMethod.DELETE, "/api/**")).hasRole("ADMIN")
                        .requestMatchers(antMatcher("/api/**")).authenticated()
                        .anyRequest().permitAll())
                // lets the H2 web console render in a frame during development
                .headers(h -> h.frameOptions(f -> f.sameOrigin()))
                .httpBasic(Customizer.withDefaults());
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public UserDetailsService users(
            PasswordEncoder encoder,
            @Value("${app.security.staff-password}") String staffPassword,
            @Value("${app.security.admin-password}") String adminPassword) {
        return new InMemoryUserDetailsManager(
                User.withUsername("staff").password(encoder.encode(staffPassword)).roles("STAFF").build(),
                User.withUsername("admin").password(encoder.encode(adminPassword)).roles("STAFF", "ADMIN").build());
    }
}
