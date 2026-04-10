package com.unimarket.unimarket_ventas.controller;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Controlador para visualizar el estado del Circuit Breaker en tiempo real.
 *
 * Ideal para demos: permite ver cómo cambia el estado del circuito
 * (CLOSED -> OPEN -> HALF_OPEN) cuando el servicio de usuarios falla.
 *
 * Este endpoint es PÚBLICO (no requiere JWT) para facilitar la demostración.
 */
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/resilience")
public class ResilienceController {

    private static final Logger log = LoggerFactory.getLogger(ResilienceController.class);

    private final CircuitBreakerRegistry circuitBreakerRegistry;

    public ResilienceController(CircuitBreakerRegistry circuitBreakerRegistry) {
        this.circuitBreakerRegistry = circuitBreakerRegistry;
    }

    /**
     * Muestra el estado actual de todos los Circuit Breakers registrados.
     * 
     * Respuesta de ejemplo:
     * {
     *   "circuitBreakers": {
     *     "usuariosService": {
     *       "estado": "CLOSED",
     *       "tasaFallos": "0.0%",
     *       "llamadasExitosas": 5,
     *       "llamadasFallidas": 0,
     *       "llamadasNoPermitidas": 0,
     *       "configuracion": {
     *         "slidingWindowSize": 5,
     *         "failureRateThreshold": "50.0%",
     *         "waitDurationInOpenState": "10s"
     *       }
     *     }
     *   }
     * }
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> obtenerEstadoResiliencia() {
        log.info("[RESILIENCE] Consultando estado de Circuit Breakers...");

        Map<String, Object> response = new LinkedHashMap<>();
        Map<String, Object> circuitBreakers = new LinkedHashMap<>();

        circuitBreakerRegistry.getAllCircuitBreakers().forEach(cb -> {
            Map<String, Object> cbInfo = new LinkedHashMap<>();
            CircuitBreaker.Metrics metrics = cb.getMetrics();

            // Estado actual del circuito
            cbInfo.put("estado", cb.getState().name());
            cbInfo.put("tasaFallos", String.format("%.1f%%", metrics.getFailureRate()));

            // Contadores de llamadas
            cbInfo.put("llamadasExitosas", metrics.getNumberOfSuccessfulCalls());
            cbInfo.put("llamadasFallidas", metrics.getNumberOfFailedCalls());
            cbInfo.put("llamadasNoPermitidas", metrics.getNumberOfNotPermittedCalls());
            cbInfo.put("llamadasBuffered", metrics.getNumberOfBufferedCalls());
            cbInfo.put("llamadasLentas", metrics.getNumberOfSlowCalls());
            cbInfo.put("tasaLlamadasLentas", String.format("%.1f%%", metrics.getSlowCallRate()));

            // Configuración del Circuit Breaker
            Map<String, Object> config = new LinkedHashMap<>();
            config.put("slidingWindowSize", cb.getCircuitBreakerConfig().getSlidingWindowSize());
            config.put("slidingWindowType", cb.getCircuitBreakerConfig().getSlidingWindowType().name());
            config.put("failureRateThreshold", 
                    String.format("%.1f%%", cb.getCircuitBreakerConfig().getFailureRateThreshold()));
            config.put("waitDurationInOpenState", 
                    cb.getCircuitBreakerConfig().getWaitIntervalFunctionInOpenState().toString());
            config.put("permittedCallsInHalfOpen", 
                    cb.getCircuitBreakerConfig().getPermittedNumberOfCallsInHalfOpenState());
            cbInfo.put("configuracion", config);

            circuitBreakers.put(cb.getName(), cbInfo);
        });

        response.put("circuitBreakers", circuitBreakers);

        // Explicación para la demo
        Map<String, String> explicacion = new LinkedHashMap<>();
        explicacion.put("CLOSED", "Circuito cerrado: todo funciona normal, las llamadas pasan al servicio real.");
        explicacion.put("OPEN", "Circuito abierto: demasiadas fallas detectadas, las llamadas van directo al FALLBACK.");
        explicacion.put("HALF_OPEN", "Circuito semi-abierto: probando si el servicio se recupero con llamadas limitadas.");

        response.put("estadosPosibles", explicacion);

        return ResponseEntity.ok(response);
    }
}
