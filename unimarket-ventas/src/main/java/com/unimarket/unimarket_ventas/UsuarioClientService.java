package com.unimarket.unimarket_ventas;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class UsuarioClientService {

    private final RestClient restClient;

    public UsuarioClientService(RestClient.Builder restClientBuilder, @Value("${app.usuarios-service.url}") String usuariosServiceUrl) {
        // RestClient.Builder is auto-configured by spring-boot-starter-restclient
        // and auto-instrumented by spring-boot-starter-opentelemetry for trace propagation
        this.restClient = restClientBuilder
                .baseUrl(usuariosServiceUrl)
                .build();
    }

    public boolean verificarExistencia(String usuarioId) {
        String url = "/api/usuarios/" + usuarioId + "/existe";
        System.out.println("[VENTAS-SERVICE] ---> Enviando GET SOA a: " + url);
        Boolean existe = restClient.get()
                .uri(url)
                .retrieve()
                .body(Boolean.class);
        System.out.println("[VENTAS-SERVICE] <--- Recibida respuesta de existencia: " + existe);
        return Boolean.TRUE.equals(existe);
    }

    public UsuarioPerfilDTO obtenerPerfil(String usuarioId) {
        String url = "/api/usuarios/" + usuarioId + "/perfil";
        System.out.println("[VENTAS-SERVICE] ---> Enviando GET SOA a: " + url);
        UsuarioPerfilDTO perfil = restClient.get()
                .uri(url)
                .retrieve()
                .body(UsuarioPerfilDTO.class);
        String tipo = (perfil != null) ? perfil.getTipo() : "null";
        System.out.println("[VENTAS-SERVICE] <--- Recibida respuesta de tipo: " + tipo);
        return perfil;
    }
}
