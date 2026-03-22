package com.unimarket.unimarket_ventas.factory;

import com.unimarket.unimarket_ventas.model.CoursePDF;
import com.unimarket.unimarket_ventas.model.Notebook;
import com.unimarket.unimarket_ventas.model.Product;

/**
 * Fábrica concreta que crea productos de útiles escolares.
 */
public class SchoolSuppliesFactory implements IMarketFactory {

    @Override
    public Product createPhysicalProduct(String name, double price) {
        return new Notebook(name, price);
    }

    @Override
    public Product createDigitalProduct(String name, double price) {
        return new CoursePDF(name, price);
    }
}
