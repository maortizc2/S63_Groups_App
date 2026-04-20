package com.groupsapp.monolito.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    // Clave secreta desde application.properties
    @Value("${app.jwt.secret}")
    private String jwtSecret;

    // Tiempo de expiración desde application.properties (en ms)
    @Value("${app.jwt.expiration}")
    private long jwtExpiration;

    // ── Generar token ─────────────────────────────────
    // Se llama cuando el usuario hace login exitosamente
    public String generateToken(Long userId, String email) {
        return Jwts.builder()
                .setSubject(email)                          // identidad principal
                .claim("userId", userId)                    // dato extra en el payload
                .setIssuedAt(new Date())                    // cuándo se creó
                .setExpiration(new Date(                    // cuándo expira
                        System.currentTimeMillis() + jwtExpiration
                ))
                .signWith(getSigningKey())                  // firma con clave secreta
                .compact();
    }

    // ── Extraer email del token ───────────────────────
    public String getEmailFromToken(String token) {
        return parseClaims(token).getSubject();
    }

    // ── Extraer userId del token ──────────────────────
    public Long getUserIdFromToken(String token) {
        return parseClaims(token).get("userId", Long.class);
    }

    // ── Validar que el token sea correcto ─────────────
    public boolean validateToken(String token) {
        try {
            parseClaims(token);     // Si no lanza excepción, el token es válido
            return true;
        } catch (ExpiredJwtException e) {
            System.out.println("Token expirado: " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            System.out.println("Token no soportado: " + e.getMessage());
        } catch (MalformedJwtException e) {
            System.out.println("Token malformado: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Token vacío o nulo: " + e.getMessage());
        }
        return false;
    }

    // ── Métodos privados de utilidad ──────────────────

    // Parsear el token y extraer todos los claims (datos del payload)
    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Convertir el String secreto en una clave criptográfica real
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }
}