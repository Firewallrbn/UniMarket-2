package com.unimarket.model;

/**
 * Producto físico de tecnología: una laptop.
 */
public class Laptop extends Product implements IPhysicalProduct {

    private double weight;
    private double shippingCost;

    public Laptop() {
        super();
    }

    public Laptop(String name, double price) {
        super(name, price, "Tecnología");
        this.weight = 2.5;
        this.shippingCost = 15.00;
    }

    public Laptop(int id, String name, double price) {
        super(id, name, price, "Tecnología");
        this.weight = 2.5;
        this.shippingCost = 15.00;
    }

    @Override
    public String getProductType() {
        return "Laptop";
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
