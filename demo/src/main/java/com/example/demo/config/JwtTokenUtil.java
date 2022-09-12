package com.example.demo.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtTokenUtil implements Serializable {

    private static final long serialVersionUID = -2550185165626007488L;

    public static final long JWT_TOKEN_VALIDITY = 5 * 60 * 60;

     @Value("${jwt.secret}")
    private String secret;

    // jwt 토큰에서 사용자 이름 검색
    public String getUsernameFromToken (String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    // jwt 토큰에서 만료 날짜 검색
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    private <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    // 토큰에서 정보를 검색하려면 비밀 키가 필요
    private Claims getAllClaimsFromToken(String token) {
         return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    // 토큰 만료 여부 확인
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    // 사용자에 대한 토큰 생성
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return doGenerateToken(claims, userDetails.getUsername());
    }

    // 톤큰 생성하는 동안
    // 1. 발급자, 만료일, 제목 및 ID와 같은 토큰의 클레임을 정의
    // 2. HS512 알고리즘과 비밀 키를 사용하여 JWT에 서명
    // 3. JWS 컴팩트 직렬화(https://tools.ietf.org/html/draft-ietf-jose-json-web-signature-41#section-3.1)에
    //    따라 JTW를 URL 안전 문자열로 압축
    private String doGenerateToken(Map<String, Object> claims, String subject) {
        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
                .signWith(SignatureAlgorithm.HS512, secret).compact();
    }

    // validate token
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

}
