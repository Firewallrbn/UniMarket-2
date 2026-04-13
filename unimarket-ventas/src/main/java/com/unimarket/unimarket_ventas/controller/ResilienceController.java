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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Controlador para visualizar el estado del Circuit Breaker en tiempo real.
 *
 * Mantiene contadores acumulados propios porque las métricas nativas de
 * Resilience4j solo reportan la ventana deslizante actual (últimas N llamadas),
 * y se resetean al cambiar de estado (CLOSED→OPEN→HALF_OPEN).
 *
 * Este endpoint es PÚBLICO (no requiere JWT) para facilitar la demostración.
 */
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/resilience")
public class ResilienceController {

    private static final Logger log = LoggerFactory.getLogger(ResilienceController.class);

    private final CircuitBreakerRegistry circuitBreakerRegistry;

    // Contadores acumulados por nombre de circuit breaker (no se resetean)
    private final Map<String, AtomicLong> totalExitosas      = new ConcurrentHashMap<>();
    private final Map<String, AtomicLong> totalFallidas      = new ConcurrentHashMap<>();
    private final Map<String, AtomicLong> totalNoPermitidas  = new ConcurrentHashMap<>();

    public ResilienceController(CircuitBreakerRegistry circuitBreakerRegistry) {
        this.circuitBreakerRegistry = circuitBreakerRegistry;

        // Registrar listeners en cada Circuit Breaker para llevar contadores acumulados
        circuitBreakerRegistry.getAllCircuitBreakers().forEach(this::registrarListeners);

        // También registrar los que se creen después (por si acaso)
        circuitBreakerRegistry.getEventPublisher()
                .onEntryAdded(event -> registrarListeners(event.getAddedEntry()));
    }

    /**
     * Registra listeners en un Circuit Breaker para mantener contadores acumulados.
     * Resilience4j publica eventos por cada llamada individual, lo que nos permite
     * contabilizar el total independientemente de la ventana deslizante.
     */
    private void registrarListeners(CircuitBreaker cb) {
        String name = cb.getName();
        totalExitosas.putIfAbsent(name, new AtomicLong(0));
        totalFallidas.putIfAbsent(name, new AtomicLong(0));
        totalNoPermitidas.putIfAbsent(name, new AtomicLong(0));

        cb.getEventPublisher()
            .onSuccess(e    -> totalExitosas.get(name).incrementAndGet())
            .onError(e      -> totalFallidas.get(name).incrementAndGet())
            .onIgnoredError(e -> totalFallidas.get(name).incrementAndGet())
            .onCallNotPermitted(e -> totalNoPermitidas.get(name).incrementAndGet());
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> obtenerEstadoResiliencia() {
        log.info("[RESILIENCE] Consultando estado de Circuit Breakers...");

        Map<String, Object> response     = new LinkedHashMap<>();
        Map<String, Object> circuitBreakers = new LinkedHashMap<>();

        circuitBreakerRegistry.getAllCircuitBreakers().forEach(cb -> {
            String name = cb.getName();
            CircuitBreaker.Metrics metrics = cb.getMetrics();

            Map<String, Object> cbInfo = new LinkedHashMap<>();

            // ── Estado ──────────────────────────────────────────────────────────
            // Normalizamos el estado: FORCED_OPEN lo tratamos igual que OPEN
            // para que el frontend tenga siempre CLOSED | OPEN | HALF_OPEN
            String estadoRaw = cb.getState().name();
            String estado = switch (estadoRaw) {
                case "CLOSED"      -> "CLOSED";
                case "OPEN",
                     "FORCED_OPEN",
                     "DISABLED"    -> "OPEN";
                case "HALF_OPEN"   -> "HALF_OPEN";
                default            -> "OPEN"; // seguro por defecto
            };
            cbInfo.put("estado", estado);
            cbInfo.put("estadoRaw", estadoRaw); // opcional, para debug

            // ── Tasa de fallos ─────────────────────────────────────────────────
            // getFailureRate() retorna -1.0 cuando la ventana no está llena todavía.
            // En ese caso calculamos la tasa real desde nuestros contadores acumulados.
            float failureRateNativo = metrics.getFailureRate();
            String tasaFallos;
            long acumExitosas    = totalExitosas.getOrDefault(name, new AtomicLong(0)).get();
            long acumFallidas    = totalFallidas.getOrDefault(name, new AtomicLong(0)).get();
            long acumNoPermit    = totalNoPermitidas.getOrDefault(name, new AtomicLong(0)).get();

            if (failureRateNativo < 0) {
                // Ventana aún no llena: calculamos manualmente con el total acumulado
                long totalLlamadas = acumExitosas + acumFallidas;
                if (totalLlamadas == 0) {
                    tasaFallos = "0.0%";
                } else {
                    double tasa = (acumFallidas * 100.0) / totalLlamadas;
                    tasaFallos = String.format("%.1f%%", tasa);
                }
            } else {
                tasaFallos = String.format("%.1f%%", failureRateNativo);
            }
            cbInfo.put("tasaFallos", tasaFallos);

            // ── Contadores acumulados (no se resetean con cambios de estado) ───
            cbInfo.put("llamadasExitosas",    acumExitosas);
            cbInfo.put("llamadasFallidas",    acumFallidas);
            cbInfo.put("llamadasNoPermitidas", acumNoPermit);

            // ── Ventana actual (últimas N llamadas, para referencia) ───────────
            cbInfo.put("ventanaActualBuffered", metrics.getNumberOfBufferedCalls());
            cbInfo.put("ventanaActualExitosas", metrics.getNumberOfSuccessfulCalls());
            cbInfo.put("ventanaActualFallidas", metrics.getNumberOfFailedCalls());

            // ── Configuración del Circuit Breaker ─────────────────────────────
            Map<String, Object> config = new LinkedHashMap<>();
            config.put("slidingWindowSize",
                    cb.getCircuitBreakerConfig().getSlidingWindowSize());
            config.put("failureRateThreshold",
                    String.format("%.0f%%", cb.getCircuitBreakerConfig().getFailureRateThreshold()));
            config.put("waitDurationInOpenState", "10s"); // valor fijo de application.properties
            config.put("permittedCallsInHalfOpen",
                    cb.getCircuitBreakerConfig().getPermittedNumberOfCallsInHalfOpenState());
            cbInfo.put("configuracion", config);

            circuitBreakers.put(name, cbInfo);
        });

        response.put("circuitBreakers", circuitBreakers);

        // Explicación de estados para la demo
        Map<String, String> explicacion = new LinkedHashMap<>();
        explicacion.put("CLOSED",    "Circuito cerrado: todo funciona normal, las llamadas pasan al servicio real.");
        explicacion.put("OPEN",      "Circuito abierto: demasiadas fallas detectadas, las llamadas van directo al FALLBACK.");
        explicacion.put("HALF_OPEN", "Circuito semi-abierto: probando si el servicio se recupero con llamadas limitadas.");
        response.put("estadosPosibles", explicacion);

        return ResponseEntity.ok(response);
    }
}
