package com.unimarket.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Representa una venta completada en el sistema.
 */
public class Sale {

    private String id;
    private LocalDateTime date;
    private User user;
    private double totalPaid;
    private List<Product> products;

    public Sale() {
        this.id = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.date = LocalDateTime.now();
        this.products = new ArrayList<>();
    }

    public Sale(User user, double totalPaid, List<Product> products) {
        this.id = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.date = LocalDateTime.now();
        this.user = user;
        this.totalPaid = totalPaid;
        this.products = new ArrayList<>(products);
    }

    public Sale(String id, LocalDateTime date, User user, double totalPaid, List<Product> products) {
        this.id = id;
        this.date = date;
        this.user = user;
        this.totalPaid = totalPaid;
        this.products = new ArrayList<>(products);
    }

    // --- Getters y Setters ---

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public double getTotalPaid() {
        return totalPaid;
    }

    public void setTotalPaid(double totalPaid) {
        this.totalPaid = totalPaid;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public String getFormattedDate() {
        return date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    @Override
    public String toString() {
        return String.format("Venta[%s] %s - Usuario: %s - Total: $%.2f",
                id, getFormattedDate(), user.getUsername(), totalPaid);
    }
}
