package com.unimarket.model;

/**
 * Representa un usuario del sistema UniMarket.
 */
public class User {

    private String username;
    private UserType type;

    public User() {
    }

    public User(String username, UserType type) {
        this.username = username;
        this.type = type;
    }

    // --- Getters y Setters ---

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public UserType getType() {
        return type;
    }

    public void setType(UserType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return username + " (" + type.getDisplayName() + ")";
    }
}
