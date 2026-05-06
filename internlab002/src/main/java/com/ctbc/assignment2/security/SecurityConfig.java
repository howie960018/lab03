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

    // 門口警衛（JWT 驗證用 Filter）
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    // ============================================================
    // 1 密碼加密器（給 AppUserService 用）
    // ============================================================

    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCrypt：
        // - 每次加密結果都不同
        // - 無法反推原始密碼
        // 業界標準
        return new BCryptPasswordEncoder();
    }

    // ============================================================
    // 2 AuthenticationManager（登入時用）
    // ============================================================

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration) throws Exception {
        // 交給 Spring Security 自己組裝
        // login 時 authenticationManager.authenticate(...) 會用到
        return configuration.getAuthenticationManager();
    }

    // ============================================================
    // 3 API Security（JWT 世界）
    // ============================================================

    @Bean
    @Order(1) // 優先處理 /api/**
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http)
            throws Exception {

        http
            // 只套用在 /api/** 路徑
            .securityMatcher("/api/**")

            // 啟用 CORS（設定在下面的 corsConfigurationSource）
            .cors(cors -> {})

            // 關閉 CSRF（JWT 是 stateless，不用 CSRF）
            .csrf(csrf -> csrf.disable())

            // Session 政策：完全不使用 HttpSession
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // 未登入或 JWT 無效時，直接回 401
            .exceptionHandling(exceptions ->
                exceptions.authenticationEntryPoint(
                        new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)
                )
            )

            // ============================================================
            // API 存取規則
            // ============================================================
            .authorizeHttpRequests(auth -> auth

                // CORS preflight（一定要放行）
                .requestMatchers(HttpMethod.OPTIONS, "/api/**").permitAll()

                // 登入 / 註冊 API 永遠放行
                .requestMatchers("/api/auth/login").permitAll()
                .requestMatchers("/api/auth/register").permitAll()

                // 公開查詢 API（不用登入）
                .requestMatchers(HttpMethod.GET, "/api/category/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/course/**").permitAll()

                // 其他 GET API：一定要登入
                .requestMatchers(HttpMethod.GET, "/api/**")
                        .hasAnyRole("USER", "ADMIN", "INSTRUCTOR")

                // 寫入 API：只要是登入者即可（Lab 示範用途）
                .requestMatchers(HttpMethod.POST, "/api/category/**")
                        .hasAnyRole("USER", "ADMIN", "INSTRUCTOR")

                .requestMatchers(HttpMethod.DELETE, "/api/category/**")
                        .hasAnyRole("USER", "ADMIN", "INSTRUCTOR")

                .requestMatchers(HttpMethod.POST, "/api/course/**")
                        .hasAnyRole("USER", "ADMIN", "INSTRUCTOR")

                .requestMatchers(HttpMethod.DELETE, "/api/course/**")
                        .hasAnyRole("USER", "ADMIN", "INSTRUCTOR")

                // 其他沒列出的 API，都要登入
                .anyRequest().authenticated()
            )

            // 把 JWT 警衛插在「帳密登入 Filter」之前
            .addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }

    // ============================================================
    // 4 CORS 設定（前端 Angular 用）
    // ============================================================

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration config = new CorsConfiguration();

        // 允許前端來源
        config.setAllowedOrigins(List.of(
                "http://localhost:4200",
                "http://127.0.0.1:4200"
        ));

        // 允許的 HTTP 方法
        config.setAllowedMethods(List.of(
                "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"
        ));

        // 允許所有 Header
        config.setAllowedHeaders(List.of("*"));

        // 讓前端可以讀取 Authorization Header
        config.setExposedHeaders(List.of("Authorization"));

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/api/**", config);

        return source;
    }

    // ============================================================
    // 5 Web MVC Security（傳統表單登入世界）
    // ============================================================

    @Bean
    @Order(2) // 比 API Security 晚處理
    public SecurityFilterChain webSecurityFilterChain(HttpSecurity http)
            throws Exception {

        http
            // ============================================================
            // Web 頁面存取規則
            // ============================================================
            .authorizeHttpRequests(auth -> auth

                // 公開頁面
                .requestMatchers(
                    "/",
                    "/home",
                    "/error",
                    "/login",
                    "/register",
                    "/h2-console/**"  // H2 Console
                ).permitAll()

                // 其他頁面都要登入
                .anyRequest().authenticated()
            )

            // ============================================================
            // H2 Console 特殊設定
            // ============================================================
            .csrf(csrf ->
                csrf.ignoringRequestMatchers("/h2-console/**")
            )

            .headers(headers ->
                headers.frameOptions(frame -> frame.sameOrigin())
            )

            // ============================================================
            // 表單登入設定
            // ============================================================
            .formLogin(form -> form
                .loginPage("/login")
                .permitAll()
            )

            // ============================================================
            // 登出後導向
            // ============================================================
            .logout(logout ->
                logout.logoutSuccessUrl("/login?logout")
            );

        return http.build();
    }
}