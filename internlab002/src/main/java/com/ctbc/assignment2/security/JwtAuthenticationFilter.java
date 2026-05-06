package com.ctbc.assignment2.security;

// ======================================
// Servlet / Spring Security 相關 import
// ======================================
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

// ---------------------------------------------------------
// JwtAuthenticationFilter
// 「大樓門口的警衛」
// - 每一個進入 /api/** 的請求進 Controller 之前一定會先經過這裡
// - 檢查你有沒有帶證件，把證件轉成 Spring Security 看得懂的身分
// ---------------------------------------------------------

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // JwtService：負責解析、驗證 JWT 的工具
    private final JwtService jwtService;

    @Autowired
    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    // -----------------------------------------------------
    // doFilterInternal
    // 每一個 HTTP request 進來時，Spring Security 都會呼叫這個方法一次
    // -----------------------------------------------------

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        // 1. 從 HTTP Header 拿 Authorization
        // 等同於：Authorization: Bearer xxxxxx.yyyyy.zzzzz
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        // 如果：
        // - Header 是空的
        // - 或不是以 "Bearer " 開頭
        // 代表：使用者「沒有帶 JWT」
        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
            // 不是錯誤，只是「我無法確認你是登入狀態」
            // 交給後面的流程繼續判斷（可能是公開 API）
            filterChain.doFilter(request, response);
            return;
        }

        // 2. 從 Header 中取出真正的 JWT
        // "Bearer " 長度是 7，所以從第 7 個字元後開始切
        String token = authHeader.substring(7);

        // 3. 驗證 JWT 是否有效
        if (!jwtService.isTokenValid(token)) {
            // 如果 token：簽章被亂改、已過期、格式錯誤，當作沒帶 token
            filterChain.doFilter(request, response);
            return;
        }

        // 4. 從 JWT 裡取出 username
        String username = jwtService.extractUsername(token);

        // 5. 建立 Spring Security 的「登入狀態」
        // 如果：JWT 裡真的有使用者名字，而且現在還沒有人被標記為「已登入」
        // 才需要建立登入狀態
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            
            // 從 JWT 中取出角色資訊，例如：ROLE_USER, ROLE_ADMIN
            List<SimpleGrantedAuthority> authorities = jwtService.extractRoles(token).stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            // 建立 Spring Security 認得的 Authentication 物件
            // 👉 把一張 JWT 轉換成「系統內部的登入身分」
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    username,    // principal：你是誰
                    null,        // credentials：不需要密碼
                    authorities  // authorities：你有哪些角色
            );

            // 補上 request 的額外資訊（IP、Session 等）
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // 放進 SecurityContext
            // 從這一刻開始：hasRole("USER"), authenticated(), @PreAuthorize 全部都會生效
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // 6. 繼續往後走（交給 Controller）
        // 一定要呼叫，不然 request 會「卡死在這裡」
        filterChain.doFilter(request, response);
    }
}