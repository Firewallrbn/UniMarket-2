package com.unimarket.unimarket_ventas.model;

/**
 * Interfaz para productos físicos que requieren envío.
 */
public interface IPhysicalProduct {

    double getWeight();

    double getShippingCost();
}
