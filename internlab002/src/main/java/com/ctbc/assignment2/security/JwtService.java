package com.ctbc.assignment2.security;

// JWT 相關套件 (jjwt)
// 用來「產生 / 解析 / 驗證」JWT
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

// Spring / Security
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

// 加密金鑰型別
import javax.crypto.SecretKey;

// Java 基本工具
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JwtService {

    // JWT 簽名用的秘密金鑰
    // 只要這把鑰匙不外洩：
    // - JWT 就不能被偽造
    // - JWT 就不能被亂改
    private final String secret;

    // JWT 有效期限 (毫秒)
    // 例如 1800000 = 30 分鐘
    private final long expirationMs;

    // 從 application.properties 注入設定
    public JwtService(
            @Value("${security.jwt.secret}") String secret,
            @Value("${security.jwt.expiration-ms}") long expirationMs) {
        this.secret = secret;
        this.expirationMs = expirationMs;
    }

    // 1. 產生 JWT (登入 / 註冊成功時)
    public String generateToken(UserDetails userDetails) {

        // 從 UserDetails 中取出角色資訊
        // Spring Security 內部是 GrantedAuthority
        // JWT 只存「字串」即可
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        // 現在時間 (發證時間)
        Date now = new Date();
        // 到期時間 = 現在 + 有效期限
        Date expiry = new Date(now.getTime() + expirationMs);

        // 正式建立 JWT
        return Jwts.builder()
                // subject：證件的「主體」，這裡通常放 username
                .setSubject(userDetails.getUsername())
                // 自訂欄位 (claim)：把角色資訊寫進 JWT
                .claim("roles", roles)
                // 發證時間
                .setIssuedAt(now)
                // 過期時間
                .setExpiration(expiry)
                // 用秘密金鑰 + HS256 演算法簽名
                // 確保 JWT 沒被竄改
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                // 壓縮成最終 JWT 字串
                .compact();
    }

    // 2. 驗證 JWT 是否有效
    public boolean isTokenValid(String token) {
        try {
            // 只要能成功解析 claims：
            // - 簽名正確
            // - 沒過期
            // - 格式正確
            getAllClaims(token);
            return true;
        } catch (Exception ex) {
            // 任何錯誤：被亂改、過期、非法格式
            return false;
        }
    }

    // 3. 從 JWT 取出 username
    public String extractUsername(String token) {
        // subject 就是當初 setSubject() 放進去的 username
        return getAllClaims(token).getSubject();
    }

    // 4. 從 JWT 取出角色資訊
    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        // 這裡取的是當初 claim("roles", roles) 放進去的資料
        return getAllClaims(token).get("roles", List.class);
    }

    // 5. 提供 JWT 有效期限 (給 Controller 用)
    public long getExpirationMs() {
        return expirationMs;
    }

    // 內部工具方法
    /**
     * 解析 JWT 並回傳所有 Claims
     * 
     * 這一步同時會：
     * - 驗證簽名
     * - 驗證是否過期
     * 
     * 若有任何問題，會直接丟 Exception
     */
    private Claims getAllClaims(String token) {
        return Jwts.parserBuilder()
                // 設定簽名用的金鑰
                .setSigningKey(getSigningKey())
                .build()
                // 解析 JWT
                .parseClaimsJws(token)
                // 取得 payload (claims)
                .getBody();
    }

    /**
     * 取得 JWT 簽名用的 SecretKey
     * 
     * 為什麼這麼寫？
     * - 有些 secret 是 base64 格式
     * - 有些是純字串
     * 這裡同時支援兩種
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes;
        try {
            // 嘗試當作 base64 解碼
            keyBytes = Decoders.BASE64.decode(secret);
        } catch (Exception ex) {
            // 如果不是 base64，就當作普通字串
            keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        }
        // 產生 HS256 可用的金鑰
        return Keys.hmacShaKeyFor(keyBytes);
    }
}