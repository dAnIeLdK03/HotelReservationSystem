package com.hotelsystemmanegment.Utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.function.Function;

@Service
public class JWTUtilis {

    private static final long EXPIRATION_TIME = 1000 * 60 * 24 * 7; //for 7 days

    private final SecretKey Key;

    public JWTUtilis() {
        String secreteString = "35468798878634222346879877456343356789685465";
        byte[] keyBytes = Base64.getDecoder().decode(secreteString.getBytes(StandardCharsets.UTF_8));
        this.Key = new SecretKeySpec(keyBytes, "HmacSHA256");

    }

    public String generateToken(UserDetails userDetails){
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration((new Date(System.currentTimeMillis() + EXPIRATION_TIME)))
                .signWith(Key)
                .compact();
    }

    public String extractUsername(String token) {
        return exctractClaims(token, Claims::getSubject);
    }

    private <T> T exctractClaims(String token, Function<Claims, T>claimsFunction) {
        return claimsFunction.apply(Jwts.parser().verifyWith(Key).build().parseSignedClaims(token).getPayload());
    }

    public boolean isValidToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
    private boolean isTokenExpired(String token) {
        return exctractClaims(token, Claims::getExpiration).before(new Date());
    }
}
