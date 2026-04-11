package com.unimarket.unimarket_ventas.controller;

import com.unimarket.unimarket_ventas.security.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Controlador de autenticación para generar tokens JWT.
 *
 * En un sistema real, aquí se validarían credenciales contra una base de datos.
 * Para esta demo/prueba, se genera un token para cualquier usuario solicitado.
 */
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final JwtUtil jwtUtil;

    public AuthController(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    /**
     * Endpoint GET para facilitar la generación de tokens en demos y pruebas.
     * Uso: GET /api/auth/token?username=testUser
     */
    @GetMapping("/token")
    public ResponseEntity<Map<String, String>> obtenerToken(
            @RequestParam(defaultValue = "testUser") String username) {
        return generarTokenResponse(username);
    }

    /**
     * Endpoint POST para generar tokens (más apropiado para producción).
     * Body: { "username": "testUser" }
     */
    @PostMapping("/token")
    public ResponseEntity<Map<String, String>> crearToken(@RequestBody Map<String, String> body) {
        String username = body.getOrDefault("username", "testUser");
        return generarTokenResponse(username);
    }

    private ResponseEntity<Map<String, String>> generarTokenResponse(String username) {
        String token = jwtUtil.generateToken(username);
        Map<String, String> response = new HashMap<>();
        response.put("jwt", token);
        response.put("usuario", username);
        response.put("mensaje", "Token generado exitosamente. Usa el header: Authorization: Bearer <jwt>");

        log.info("[AUTH] Token JWT generado para usuario: {}", username);
        return ResponseEntity.ok(response);
    }
}
