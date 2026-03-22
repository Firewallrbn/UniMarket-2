package com.unimarket.unimarket_usuarios;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    // Funcionalidad 1: Validación de Existencia de Usuario
    @GetMapping("/{id}/existe")
    public boolean existeUsuario(@PathVariable String id) {
        // IDs de prueba: 101, 102, 103 (o el antiguo 123)
        return "101".equals(id) || "102".equals(id) || "103".equals(id) || "123".equals(id);
    }

    // Funcionalidad 2: Obtener Perfil para Cálculo de Comisión
    @GetMapping("/{id}/perfil")
    public Map<String, String> obtenerPerfil(@PathVariable String id) {
        Map<String, String> perfil = new HashMap<>();
        
        switch (id) {
            case "101":
                perfil.put("tipo", "STANDARD");
                break;
            case "102":
                perfil.put("tipo", "ENTREPRENEUR");
                break;
            case "103":
                perfil.put("tipo", "SCHOLARSHIP");
                break;
            case "123":
                perfil.put("tipo", "CASUAL"); // Mapeará a STANDARD por defecto en el controlador de ventas
                break;
            default:
                perfil.put("tipo", "UNKNOWN");
                break;
        }
        return perfil;
    }
}
