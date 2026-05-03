package com.ctbc.assignment2.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    @Order(1)
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/api/**")
                .cors(cors -> {
                })
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(exceptions -> exceptions
                // 未登入或 Token 無效時回 401
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
            )
                .authorizeHttpRequests(auth -> auth
                        // preflight
                        .requestMatchers(HttpMethod.OPTIONS, "/api/**").permitAll()
                        .requestMatchers("/api/auth/login").permitAll()
                        .requestMatchers("/api/auth/register").permitAll()

                        // Public read: anyone can browse category/course lists
                        .requestMatchers(HttpMethod.GET, "/api/category/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/course/**").permitAll()

                        // Other GET APIs: require login
                        .requestMatchers(HttpMethod.GET, "/api/**").hasAnyRole("USER", "ADMIN", "INSTRUCTOR")

                        // Write APIs: allow any logged-in role (lab/demo purpose)
                        .requestMatchers(HttpMethod.POST, "/api/category/**").hasAnyRole("USER", "ADMIN", "INSTRUCTOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/category/**").hasAnyRole("USER", "ADMIN", "INSTRUCTOR")
                        .requestMatchers(HttpMethod.POST, "/api/course/**").hasAnyRole("USER", "ADMIN", "INSTRUCTOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/course/**").hasAnyRole("USER", "ADMIN", "INSTRUCTOR")

                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(
                "http://localhost:4200",
                "http://127.0.0.1:4200"
        ));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);
        return source;
    }

    @Bean
    @Order(2)
    public SecurityFilterChain webSecurityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/home", "/error", "/webjars/**", "/login", "/register").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/instructor/**").hasRole("INSTRUCTOR")
                        .requestMatchers("/profile/**").authenticated()
                        .requestMatchers("/my-courses/**").authenticated()
                        .requestMatchers("/orders/**").authenticated()
                        .requestMatchers("/courses/**").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                    .loginPage("/login")
                        .successHandler((request, response, authentication) -> {
                            boolean isAdmin = authentication.getAuthorities().stream()
                                    .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
                            boolean isInstructor = authentication.getAuthorities().stream()
                                    .anyMatch(authority -> authority.getAuthority().equals("ROLE_INSTRUCTOR"));
                            if (isAdmin) {
                                response.sendRedirect("/admin/dashboard");
                            } else if (isInstructor) {
                                response.sendRedirect("/instructor/dashboard");
                            } else {
                                response.sendRedirect("/courses");
                            }
                        })
                        .permitAll()
                )
                .logout(logout -> logout.logoutSuccessUrl("/login?logout"));
        return http.build();
    }
}
