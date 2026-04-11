package com.unimarket.unimarket_ventas.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Configuración de Spring Security para el microservicio unimarket-ventas.
 *
 * Endpoints públicos:
 *   - /api/auth/**         → Generación de tokens JWT
 *   - /api/productos/**    → Catálogo de productos (lectura pública)
 *   - /api/resilience/**   → Estado del Circuit Breaker (para demo/observabilidad)
 *   - /actuator/**         → Health, Prometheus, métricas
 *
 * Endpoints protegidos (requieren JWT):
 *   - /api/ventas/crear           → Crear una venta
 *   - /api/ventas/historial/**    → Consultar historial
 *   - Cualquier otro endpoint
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Habilitar CORS con nuestra configuración personalizada
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // Deshabilitar CSRF para APIs REST (stateless)
                .csrf(AbstractHttpConfigurer::disable)
                // Proteger rutas especificadas
                .authorizeHttpRequests(auth -> auth
                        // Endpoints Públicos:
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/productos/**").permitAll()
                        .requestMatchers("/api/resilience/**").permitAll()
                        .requestMatchers("/actuator/**").permitAll()

                        // Endpoints Protegidos (requieren token JWT):
                        .requestMatchers("/api/ventas/crear").authenticated()
                        .requestMatchers("/api/ventas/historial/**").authenticated()

                        // Cualquier otra request necesita autenticación
                        .anyRequest().authenticated()
                )
                // Política Stateless: No guardar sesión (cada request se valida por JWT)
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Añadir nuestro filtro JWT antes del filtro estándar de autenticación
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Configuración CORS para permitir que el frontend (que corre en otro puerto/origen)
     * pueda enviar peticiones con headers de Authorization (Bearer token).
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("*"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept"));
        config.setExposedHeaders(List.of("Authorization"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
