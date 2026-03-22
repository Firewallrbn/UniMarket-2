package com.unimarket.model;

/**
 * Producto físico de útiles escolares: un cuaderno.
 */
public class Notebook extends Product implements IPhysicalProduct {

    private double weight;
    private double shippingCost;

    public Notebook() {
        super();
    }

    public Notebook(String name, double price) {
        super(name, price, "Útiles Escolares");
        this.weight = 0.5;
        this.shippingCost = 5.00;
    }

    public Notebook(int id, String name, double price) {
        super(id, name, price, "Útiles Escolares");
        this.weight = 0.5;
        this.shippingCost = 5.00;
    }

    @Override
    public String getProductType() {
        return "Notebook";
    }

    @Override
    public double getWeight() {
        return weight;
    }

    @Override
    public double getShippingCost() {
        return shippingCost;
    }
}
