package com.unimarket.unimarket_ventas;

import com.unimarket.unimarket_ventas.strategy.CommissionContext;
import com.unimarket.unimarket_ventas.strategy.EntrepreneurCommission;
import com.unimarket.unimarket_ventas.strategy.ScholarshipCommission;
import com.unimarket.unimarket_ventas.strategy.StandardCommission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/ventas")
public class VentasController {

    private static final Logger log = LoggerFactory.getLogger(VentasController.class);

    private final UsuarioClientService usuarioClientService;

    public VentasController(UsuarioClientService usuarioClientService) {
        this.usuarioClientService = usuarioClientService;
    }

    @PostMapping("/crear")
    public ResponseEntity<Map<String, Object>> crearVenta(@RequestBody VentaRequestDTO request) {
        log.info("=======================================================");
        log.info("[VENTAS-SERVICE] ---> HTTP POST /api/ventas/crear (desde el Frontend)");
        log.info("[VENTAS-SERVICE] Iniciando venta. Usuario: {} | Monto: ${}",
                request.getUsuarioId(), request.getMontoBase());

        Map<String, Object> response = new HashMap<>();
        List<String> debugTrace = new ArrayList<>();
        debugTrace.add("[FRONT-TO-VENTAS] Inicio de peticion POST para " + request.getUsuarioId());

        // 1. Validar existencia del usuario (Funcionalidad 1 - SOA + Circuit Breaker)
        log.info("[VENTAS-SERVICE] Paso 1: Verificando si el usuario existe (con Circuit Breaker)...");
        boolean existe = usuarioClientService.verificarExistencia(request.getUsuarioId());
        if (!existe) {
            log.warn("[VENTAS-SERVICE] <--- VENTA RECHAZADA: usuario {} no verificado (puede ser CB activo o usuario inexistente).",
                    request.getUsuarioId());
            debugTrace.add("[CIRCUIT BREAKER / SOA] Venta bloqueada: servicio de usuarios no disponible o usuario no existe.");
            response.put("error", "Venta rechazada: no se pudo verificar el usuario. El servicio de usuarios puede estar caido (Circuit Breaker activo) o el usuario no existe.");
            response.put("circuitBreakerActivo", true);
            response.put("debugTrace", debugTrace);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
        }
        debugTrace.add("[SOA EXITO] Usuario " + request.getUsuarioId() + " validado.");


        // 2. Obtener tipo de usuario (Funcionalidad 2 - SOA + Circuit Breaker)
        log.info("[VENTAS-SERVICE] Paso 2: Usuario validado. Solicitando perfil (con Circuit Breaker)...");
        UsuarioPerfilDTO perfil = usuarioClientService.obtenerPerfil(request.getUsuarioId());
        String tipoUsuario = (perfil != null && perfil.getTipo() != null)
                ? perfil.getTipo().toUpperCase()
                : "STANDARD";
        debugTrace.add("[SOA EXITO] Perfil obtenido: " + tipoUsuario);

        // 3. Instanciar el contexto del patrón Strategy
        log.info("[VENTAS-SERVICE] Paso 3: Aplicando 'Strategy Pattern' para perfil {}", tipoUsuario);
        CommissionContext context = new CommissionContext();

        // 4. Asignar estrategia de comisión según el tipo de usuario
        switch (tipoUsuario) {
            case "ENTREPRENEUR":
                context.setStrategy(new EntrepreneurCommission());
                break;
            case "SCHOLARSHIP":
                context.setStrategy(new ScholarshipCommission());
                break;
            case "STANDARD":
            default:
                context.setStrategy(new StandardCommission());
                break;
        }

        // 5. Calcular la comisión usando executeStrategy (patrón Strategy legacy)
        double comision = context.executeStrategy(request.getMontoBase());
        double montoFinal = request.getMontoBase() + comision;
        log.info("[VENTAS-SERVICE] Paso 4: Venta completada. Comision: ${} | Monto Final: ${}", comision, montoFinal);
        log.info("=======================================================");
        debugTrace.add("[PATRON STRATEGY] Comision calculada: $" + comision);

        response.put("mensaje", "Venta creada exitosamente");
        response.put("usuarioId", request.getUsuarioId());
        response.put("tipoUsuario", tipoUsuario);
        response.put("montoBase", request.getMontoBase());
        response.put("comision", comision);
        response.put("montoFinal", montoFinal);
        response.put("debugTrace", debugTrace);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/historial/{usuarioId}")
    public ResponseEntity<Map<String, Object>> obtenerHistorial(@PathVariable String usuarioId) {
        log.info("[VENTAS-SERVICE] ---> HTTP GET /api/ventas/historial/{}", usuarioId);

        Map<String, Object> response = new HashMap<>();
        response.put("usuarioId", usuarioId);
        response.put("mensaje", "Historial recuperado exitosamente");

        List<String> compras = new ArrayList<>();
        compras.add("Compra 1: MacBook Pro M4 - $2499.99");
        compras.add("Compra 2: Clean Code eBook - $29.99");
        response.put("historial", compras);

        return ResponseEntity.ok(response);
    }
}
