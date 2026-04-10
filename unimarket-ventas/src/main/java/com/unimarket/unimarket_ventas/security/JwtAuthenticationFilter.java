package com.unimarket.unimarket_ventas.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Filtro que intercepta cada petición HTTP para validar el token JWT.
 *
 * Si la petición incluye un header "Authorization: Bearer <token>" válido,
 * establece la autenticación en el SecurityContext de Spring Security.
 * Si no hay header o el token es inválido, la petición continúa sin autenticación
 * (y será rechazada por Spring Security si el endpoint requiere autenticación).
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        // Comprobar si existe el header de Authorization con el formato "Bearer <token>"
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(7); // Extraer token (sin "Bearer ")

        try {
            String username = jwtUtil.extractUsername(jwt);

            // Si hay un usuario en el token y no está autenticado aún en el SecurityContext
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                if (jwtUtil.validateToken(jwt)) {
                    // El token es válido, configuramos la autenticación
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            username, null, new ArrayList<>() // Aquí irían los roles/Authorities
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // Inyectar en el contexto de seguridad actual
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    log.debug("[SECURITY] Usuario autenticado via JWT: {}", username);
                }
            }
        } catch (Exception e) {
            log.warn("[SECURITY] Error al procesar el filtro JWT: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}
