package com.unimarket.unimarket_ventas.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Utilidad para generar y validar tokens JWT.
 *
 * Usa HMAC-SHA256 para firmar los tokens. La clave secreta se inyecta desde
 * la propiedad {@code jwt.secret} y debe tener al menos 32 caracteres (256 bits)
 * para cumplir con el estándar RFC 7518 (JWA).
 */
@Component
public class JwtUtil {

    private static final Logger log = LoggerFactory.getLogger(JwtUtil.class);

    private final SecretKey secretKey;
    private final long EXPIRATION_TIME = 86400000; // 1 día en milisegundos

    public JwtUtil(@Value("${jwt.secret}") String secretString) {
        // Validar longitud mínima de la clave (256 bits = 32 bytes para HS256)
        byte[] keyBytes = secretString.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            throw new IllegalArgumentException(
                "[SECURITY] La clave JWT debe tener al menos 32 caracteres (256 bits). " +
                "Longitud actual: " + keyBytes.length + " bytes.");
        }
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
        log.info("[SECURITY] JwtUtil inicializado correctamente con clave de {} bits", keyBytes.length * 8);
    }

    /**
     * Genera un token JWT para el usuario especificado.
     *
     * @param username nombre de usuario que será el subject del token
     * @return token JWT firmado
     */
    public String generateToken(String username) {
        String token = Jwts.builder()
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(secretKey)
                .compact();
        log.info("[SECURITY] Token JWT generado para usuario: {}", username);
        return token;
    }

    /**
     * Extrae el nombre de usuario (subject) de un token JWT.
     */
    public String extractUsername(String token) {
        return getClaims(token).getSubject();
    }

    /**
     * Valida que un token JWT sea correcto y no haya expirado.
     *
     * @param token el token JWT a validar
     * @return true si el token es válido, false en caso contrario
     */
    public boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (Exception e) {
            log.warn("[SECURITY] Token JWT invalido: {}", e.getMessage());
            return false;
        }
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
