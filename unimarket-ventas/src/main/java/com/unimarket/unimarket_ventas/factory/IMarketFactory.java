package com.unimarket.unimarket_ventas.factory;

import com.unimarket.unimarket_ventas.model.Product;

/**
 * Abstract Factory: define la creación de familias de productos.
 */
public interface IMarketFactory {

    Product createPhysicalProduct(String name, double price);

    Product createDigitalProduct(String name, double price);
}
