package com.unimarket.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Carrito de compras temporal del usuario.
 */
public class Cart {

    private final List<Product> items;

    public Cart() {
        this.items = new ArrayList<>();
    }

    public void add(Product product) {
        items.add(product);
    }

    public void clear() {
        items.clear();
    }

    public List<Product> getItems() {
        return new ArrayList<>(items);
    }

    public double calculateSubtotal() {
        return items.stream()
                .mapToDouble(Product::getPrice)
                .sum();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public int size() {
        return items.size();
    }
}
