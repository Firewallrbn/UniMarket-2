package com.unimarket.unimarket_ventas;

import com.unimarket.unimarket_ventas.strategy.CommissionContext;
import com.unimarket.unimarket_ventas.strategy.EntrepreneurCommission;
import com.unimarket.unimarket_ventas.strategy.ScholarshipCommission;
import com.unimarket.unimarket_ventas.strategy.StandardCommission;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/ventas")
public class VentasController {

    private final UsuarioClientService usuarioClientService;

    public VentasController(UsuarioClientService usuarioClientService) {
        this.usuarioClientService = usuarioClientService;
    }

    @PostMapping("/crear")
    public ResponseEntity<Map<String, Object>> crearVenta(@RequestBody VentaRequestDTO request) {
        System.out.println("\n=======================================================");
        System.out.println("[VENTAS-SERVICE] ---> HTTP POST /api/ventas/crear (desde el Frontend)");
        System.out.println("[VENTAS-SERVICE] Iniciando simulación de venta. Usuario: " + request.getUsuarioId() + " | Monto: $" + request.getMontoBase());

        Map<String, Object> response = new HashMap<>();
        List<String> debugTrace = new ArrayList<>();
        debugTrace.add("[FRONT-TO-VENTAS] Inicio de petición POST para " + request.getUsuarioId());

        // 1. Validar existencia del usuario (Funcionalidad 1 - SOA)
        System.out.println("[VENTAS-SERVICE] Paso 1: Verificando si el usuario existe...");
        boolean existe = usuarioClientService.verificarExistencia(request.getUsuarioId());
        if (!existe) {
            System.out.println("[VENTAS-SERVICE] <--- ERROR: El usuario no existe. Finalizando proceso.");
            debugTrace.add("[SOA ERROR] El usuario no existe en el microservicio de Usuarios.");
            response.put("error", "El usuario especificado no existe o no es válido");
            response.put("debugTrace", debugTrace);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        debugTrace.add("[SOA EXITO] Usuario " + request.getUsuarioId() + " validado.");

        // 2. Obtener tipo de usuario (Funcionalidad 2 - SOA)
        System.out.println("[VENTAS-SERVICE] Paso 2: Usuario validado. Solicitando perfil...");
        UsuarioPerfilDTO perfil = usuarioClientService.obtenerPerfil(request.getUsuarioId());
        String tipoUsuario = (perfil != null && perfil.getTipo() != null)
                ? perfil.getTipo().toUpperCase()
                : "STANDARD";
        debugTrace.add("[SOA EXITO] Perfil obtenido: " + tipoUsuario);

        // 3. Instanciar el contexto del patrón Strategy
        System.out.println("[VENTAS-SERVICE] Paso 3: Aplicando 'Strategy Pattern' para perfil " + tipoUsuario);
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
        System.out.println("[VENTAS-SERVICE] Paso 4: Venta completada. Comisión: $" + comision + " | Monto Final: $" + montoFinal);
        System.out.println("=======================================================\n");
        debugTrace.add("[PATRON STRATEGY] Comisión calculada: $" + comision);

        response.put("mensaje", "Venta creada exitosamente");
        response.put("usuarioId", request.getUsuarioId());
        response.put("tipoUsuario", tipoUsuario);
        response.put("montoBase", request.getMontoBase());
        response.put("comision", comision);
        response.put("montoFinal", montoFinal);
        response.put("debugTrace", debugTrace);

        return ResponseEntity.ok(response);
    }
}
