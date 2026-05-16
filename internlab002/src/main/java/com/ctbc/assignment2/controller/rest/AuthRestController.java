package com.ctbc.assignment2.controller.rest;

import com.ctbc.assignment2.controller.rest.dto.AuthRequest;
import com.ctbc.assignment2.controller.rest.dto.AuthResponse;
import com.ctbc.assignment2.controller.rest.dto.RegisterRequest;
import com.ctbc.assignment2.security.JwtService;
import com.ctbc.assignment2.service.AppUserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

// AuthRestController 是「櫃檯人員」

@RestController
@RequestMapping("/api/auth")
public class AuthRestController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final AppUserService appUserService;

    @Autowired
    public AuthRestController(AuthenticationManager authenticationManager,
                              UserDetailsService userDetailsService,
                              JwtService jwtService,
                              AppUserService appUserService) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
        this.appUserService = appUserService;
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest request) {

        // 櫃檯不自己驗證，交給「身分查驗系統」AuthenticationManager
        // UsernamePasswordAuthenticationToken 是寫著帳號密碼的「申請單」
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()));
        } catch (AuthenticationException ex) {
            // 帳密錯誤時回傳 401
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        // 開始製作通行證
        UserDetails userDetails = userDetailsService.loadUserByUsername(authentication.getName());
        String token = jwtService.generateToken(userDetails);
        return new AuthResponse(token, "Bearer", jwtService.getExpirationMs());
    }

    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {

        // 1 密碼與確認密碼檢查
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Passwords do not match");
        }

        // 2 帳號是否已存在
        if (appUserService.existsByUsername(request.getUsername())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
        }

        // 3 建立使用者（存 DB，密碼已加密）
        appUserService.registerUser(request.getUsername(), request.getPassword());

        // 4 載入 UserDetails（JWT 需要）
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());

        // 5 產生 JWT（與 login 完全一致）
        String token = jwtService.generateToken(userDetails);

        // 6 直接回傳 JWT
        return new AuthResponse(token, "Bearer", jwtService.getExpirationMs());
    }
}