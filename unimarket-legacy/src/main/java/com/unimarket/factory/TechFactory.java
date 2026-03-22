package com.unimarket.factory;

import com.unimarket.model.EBook;
import com.unimarket.model.Laptop;
import com.unimarket.model.Product;

/**
 * Fábrica concreta que crea productos de tecnología.
 */
public class TechFactory implements IMarketFactory {

    @Override
    public Product createPhysicalProduct(String name, double price) {
        return new Laptop(name, price);
    }

    @Override
    public Product createDigitalProduct(String name, double price) {
        return new EBook(name, price);
    }
}
