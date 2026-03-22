package com.unimarket.factory;

import com.unimarket.model.CoursePDF;
import com.unimarket.model.Notebook;
import com.unimarket.model.Product;

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
