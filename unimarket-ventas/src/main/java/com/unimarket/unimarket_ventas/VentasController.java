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

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/ventas")
public class VentasController {

    private final UsuarioClientService usuarioClientService;

    public VentasController(UsuarioClientService usuarioClientService) {
        this.usuarioClientService = usuarioClientService;
    }

    @PostMapping("/crear")
    public ResponseEntity<Map<String, Object>> crearVenta(@RequestBody VentaRequestDTO request) {
        Map<String, Object> response = new HashMap<>();

        // 1. Validar existencia del usuario (Funcionalidad 1 - SOA)
        boolean existe = usuarioClientService.verificarExistencia(request.getUsuarioId());
        if (!existe) {
            response.put("error", "El usuario especificado no existe o no es válido");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        // 2. Obtener tipo de usuario (Funcionalidad 2 - SOA)
        UsuarioPerfilDTO perfil = usuarioClientService.obtenerPerfil(request.getUsuarioId());
        String tipoUsuario = (perfil != null && perfil.getTipo() != null)
                ? perfil.getTipo().toUpperCase()
                : "STANDARD";

        // 3. Instanciar el contexto del patrón Strategy
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

        response.put("mensaje", "Venta creada exitosamente");
        response.put("usuarioId", request.getUsuarioId());
        response.put("tipoUsuario", tipoUsuario);
        response.put("montoBase", request.getMontoBase());
        response.put("comision", comision);
        response.put("montoFinal", montoFinal);

        return ResponseEntity.ok(response);
    }
}
