package com.unimarket.unimarket_ventas;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class UsuarioClientService {

    private final RestTemplate restTemplate;
    private final String usuariosServiceUrl;

    public UsuarioClientService(RestTemplate restTemplate, @Value("${app.usuarios-service.url}") String usuariosServiceUrl) {
        this.restTemplate = restTemplate;
        this.usuariosServiceUrl = usuariosServiceUrl;
    }

    public boolean verificarExistencia(String usuarioId) {
        String url = usuariosServiceUrl + "/api/usuarios/" + usuarioId + "/existe";
        System.out.println("[VENTAS-SERVICE] ---> Enviando GET SOA a: " + url);
        Boolean existe = restTemplate.getForObject(url, Boolean.class);
        System.out.println("[VENTAS-SERVICE] <--- Recibida respuesta de existencia: " + existe);
        return Boolean.TRUE.equals(existe);
    }

    public UsuarioPerfilDTO obtenerPerfil(String usuarioId) {
        String url = usuariosServiceUrl + "/api/usuarios/" + usuarioId + "/perfil";
        System.out.println("[VENTAS-SERVICE] ---> Enviando GET SOA a: " + url);
        UsuarioPerfilDTO perfil = restTemplate.getForObject(url, UsuarioPerfilDTO.class);
        String tipo = (perfil != null) ? perfil.getTipo() : "null";
        System.out.println("[VENTAS-SERVICE] <--- Recibida respuesta de tipo: " + tipo);
        return perfil;
    }
}
