//package Holify.holify.security;
//
//
//import io.jsonwebtoken.*;
//import io.jsonwebtoken.security.Keys;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//
//import java.util.Date;
//
//@Component
//public class JwtUtil {
//
//    @Value("${jwt.secret}")
//    private String jwtSecret;
//
//    @Value("${jwt.expiration-ms:3600000}")
//    private long jwtExpirationMs;
//
//    public String generateToken(String username) {
//        Date now = new Date();
//        Date expiry = new Date(now.getTime() + jwtExpirationMs);
//        return Jwts.builder()
//                .setSubject(username)
//                .setIssuedAt(now)
//                .setExpiration(expiry)
//                .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
//                .compact();
//    }
//
//    public String getUsernameFromToken(String token) {
//        return Jwts.parserBuilder()
//                .setSigningKey(jwtSecret.getBytes())
//                .build()
//                .parseClaimsJws(token)
//                .getBody()
//                .getSubject();
//    }
//
//    public boolean validate(String token) {
//        try {
//            Jwts.parserBuilder().setSigningKey(jwtSecret.getBytes()).build().parseClaimsJws(token);
//            return true;
//        } catch (JwtException ex) {
//            return false;
//        }
//    }
//}
//
