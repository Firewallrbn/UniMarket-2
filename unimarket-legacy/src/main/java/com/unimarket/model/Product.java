package com.unimarket.model;

/**
 * Clase base abstracta para todos los productos del marketplace.
 */
public abstract class Product {

    private static int idCounter = 0;

    private int id;
    private String name;
    private double price;
    private String category;

    public Product() {
        this.id = ++idCounter;
    }

    public Product(String name, double price, String category) {
        this.id = ++idCounter;
        this.name = name;
        this.price = price;
        this.category = category;
    }

    public Product(int id, String name, double price, String category) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
        if (id >= idCounter) {
            idCounter = id;
        }
    }

    /**
     * Devuelve el tipo concreto del producto (e.g. "Laptop", "EBook").
     */
    public abstract String getProductType();

    // --- Getters y Setters ---

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return String.format("[%d] %-25s $%8.2f  (%s)", id, name, price, category);
    }
}
