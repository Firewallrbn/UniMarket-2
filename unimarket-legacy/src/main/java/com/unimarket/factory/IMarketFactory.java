package com.unimarket.factory;

import com.unimarket.model.IDigitalProduct;
import com.unimarket.model.IPhysicalProduct;
import com.unimarket.model.Product;

/**
 * Abstract Factory: define la creación de familias de productos.
 */
public interface IMarketFactory {

    Product createPhysicalProduct(String name, double price);

    Product createDigitalProduct(String name, double price);
}
