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
     *
     * Retorna FALSE intencionalmente: si no se puede verificar la existencia del usuario
     * (porque el servicio está caído), la venta debe rechazarse. No tiene sentido aprobar
     * una venta para un usuario que no se puede confirmar que existe.
     */
    public boolean fallbackVerificarExistencia(String usuarioId, Throwable t) {
        log.warn("[VENTAS-SERVICE] <--- FALLBACK verificarExistencia: servicio de usuarios no disponible. Causa: {}",
                t.getMessage());
        log.warn("[VENTAS-SERVICE] <--- Circuit Breaker ACTIVO: bloqueando venta, no se puede verificar usuario {}", usuarioId);
        return false; // BLOQUEAMOS la venta: sin verificacion, no hay venta
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
     *
     * En la práctica, este fallback solo se alcanzaría si verificarExistencia devolvió true
     * pero obtenerPerfil falla (improbable pero posible en condiciones de red intermitente).
     * Se retorna null para que el controlador decida cómo manejarlo.
     */
    public UsuarioPerfilDTO fallbackObtenerPerfil(String usuarioId, Throwable t) {
        log.warn("[VENTAS-SERVICE] <--- FALLBACK obtenerPerfil: servicio de usuarios no disponible para {}. Causa: {}",
                usuarioId, t.getMessage());
        log.warn("[VENTAS-SERVICE] <--- Circuit Breaker ACTIVO: retornando perfil nulo, venta usara STANDARD");
        return null; // El controlador lo manejará como STANDARD
    }
}
