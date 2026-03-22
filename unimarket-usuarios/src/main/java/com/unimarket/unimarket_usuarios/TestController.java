package main.java.com.unimarket.unimarket_usuarios; // Ajusta esto si tu paquete se llama diferente

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/api/status")
    public String probarServicio() {
        return "¡El microservicio de Usuarios está vivo, funcionando y listo para el Corte 2!";
    }
}