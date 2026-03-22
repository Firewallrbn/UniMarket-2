package com.unimarket.unimarket_ventas;

public class VentaRequestDTO {
    private String usuarioId;
    private double montoBase;

    public VentaRequestDTO() {}

    public VentaRequestDTO(String usuarioId, double montoBase) {
        this.usuarioId = usuarioId;
        this.montoBase = montoBase;
    }

    public String getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(String usuarioId) {
        this.usuarioId = usuarioId;
    }

    public double getMontoBase() {
        return montoBase;
    }

    public void setMontoBase(double montoBase) {
        this.montoBase = montoBase;
    }
}
