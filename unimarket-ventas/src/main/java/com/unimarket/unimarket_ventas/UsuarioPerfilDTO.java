package com.unimarket.unimarket_ventas;

public class UsuarioPerfilDTO {
    private String tipo;

    public UsuarioPerfilDTO() {}

    public UsuarioPerfilDTO(String tipo) {
        this.tipo = tipo;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
