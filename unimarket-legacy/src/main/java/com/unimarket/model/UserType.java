package com.unimarket.model;

/**
 * Enum que define los tipos de usuario en UniMarket.
 */
public enum UserType {
    VENDEDOR_CASUAL("Vendedor Casual"),
    EMPRENDEDOR("Emprendedor"),
    BECADO("Becado");

    private final String displayName;

    UserType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
