package com.unimarket.unimarket_ventas.strategy;

/**
 * Strategy Interface: define el cálculo de comisión.
 */
public interface ICommissionStrategy {

    double calculateCommission(double amount);
}
