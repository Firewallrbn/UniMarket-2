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
        Boolean existe = restTemplate.getForObject(url, Boolean.class);
        return Boolean.TRUE.equals(existe);
    }

    public UsuarioPerfilDTO obtenerPerfil(String usuarioId) {
        String url = usuariosServiceUrl + "/api/usuarios/" + usuarioId + "/perfil";
        return restTemplate.getForObject(url, UsuarioPerfilDTO.class);
    }
}
