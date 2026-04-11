package com.unimarket.unimarket_ventas;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.Duration;

/**
 * Cliente que comunica con el microservicio unimarket-usuarios mediante RestClient (SOA).
 *
 * Patrones de resiliencia aplicados:
 * - Circuit Breaker: Si el servicio de usuarios falla repetidamente, el circuito se abre
 *   y las llamadas se redirigen automáticamente al fallback sin intentar conectar.
 * - Retry: Antes de declarar una falla, reintenta la operación hasta 3 veces.
 * - Fallback: Si todo falla, retorna un valor por defecto en lugar de propagar la excepción.
 *
 * Orden de ejecución: Retry → CircuitBreaker → Fallback
 * (Retry reintenta dentro del CircuitBreaker; si todos los reintentos fallan,
 *  el CircuitBreaker registra la falla y eventualmente se abre)
 */
@Service
public class UsuarioClientService {

    private static final Logger log = LoggerFactory.getLogger(UsuarioClientService.class);

    private final RestClient restClient;

    public UsuarioClientService(
            RestClient.Builder restClientBuilder,
            @Value("${app.usuarios-service.url}") String usuariosServiceUrl) {
        // RestClient.Builder is auto-configured by spring-boot-starter-restclient
        // and auto-instrumented by spring-boot-starter-opentelemetry for trace propagation
        this.restClient = restClientBuilder
                .baseUrl(usuariosServiceUrl)
                .build();
        log.info("[USUARIOS-CLIENT] Configurado para conectar a: {}", usuariosServiceUrl);
    }

    /**
     * Verifica si un usuario existe en el microservicio de usuarios.
     *
     * @param usuarioId ID del usuario a verificar
     * @return true si el usuario existe, false si no
     */
    @CircuitBreaker(name = "usuariosService", fallbackMethod = "fallbackVerificarExistencia")
    @Retry(name = "usuariosService")
    public boolean verificarExistencia(String usuarioId) {
        String url = "/api/usuarios/" + usuarioId + "/existe";
        log.info("[VENTAS-SERVICE] ---> Enviando GET SOA a: {}", url);

        Boolean existe = restClient.get()
                .uri(url)
                .retrieve()
                .body(Boolean.class);

        log.info("[VENTAS-SERVICE] <--- Respuesta de existencia para {}: {}", usuarioId, existe);
        return Boolean.TRUE.equals(existe);
    }

    /**
     * Fallback para verificarExistencia.
     * Se activa cuando el Circuit Breaker está abierto o cuando los reintentos se agotan.
     * Retorna true por defecto para no bloquear el flujo de ventas.
     */
    public boolean fallbackVerificarExistencia(String usuarioId, Throwable t) {
        log.warn("[VENTAS-SERVICE] <--- FALLBACK verificarExistencia: No se pudo verificar usuario {}. Causa: {}",
                usuarioId, t.getMessage());
        log.warn("[VENTAS-SERVICE] <--- Circuit Breaker activado: asumiendo que el usuario EXISTE por defecto");
        return true; // Asumimos que existe por defecto en caso de falla
    }

    /**
     * Obtiene el perfil de un usuario desde el microservicio de usuarios.
     *
     * @param usuarioId ID del usuario
     * @return DTO con el perfil del usuario
     */
    @CircuitBreaker(name = "usuariosService", fallbackMethod = "fallbackObtenerPerfil")
    @Retry(name = "usuariosService")
    public UsuarioPerfilDTO obtenerPerfil(String usuarioId) {
        String url = "/api/usuarios/" + usuarioId + "/perfil";
        log.info("[VENTAS-SERVICE] ---> Enviando GET SOA a: {}", url);

        UsuarioPerfilDTO perfil = restClient.get()
                .uri(url)
                .retrieve()
                .body(UsuarioPerfilDTO.class);

        String tipo = (perfil != null) ? perfil.getTipo() : "null";
        log.info("[VENTAS-SERVICE] <--- Perfil recibido para {}: tipo={}", usuarioId, tipo);
        return perfil;
    }

    /**
     * Fallback para obtenerPerfil.
     * Se activa cuando el Circuit Breaker está abierto o cuando los reintentos se agotan.
     * Retorna un perfil STANDARD por defecto.
     */
    public UsuarioPerfilDTO fallbackObtenerPerfil(String usuarioId, Throwable t) {
        log.warn("[VENTAS-SERVICE] <--- FALLBACK obtenerPerfil: No se pudo obtener perfil de {}. Causa: {}",
                usuarioId, t.getMessage());
        log.warn("[VENTAS-SERVICE] <--- Circuit Breaker activado: retornando perfil STANDARD por defecto");
        UsuarioPerfilDTO defaultProfile = new UsuarioPerfilDTO();
        defaultProfile.setTipo("STANDARD");
        return defaultProfile;
    }
}
