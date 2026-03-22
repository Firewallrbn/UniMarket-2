package com.unimarket.strategy;

/**
 * Comisión estándar del 10% para vendedores casuales.
 */
public class StandardCommission implements ICommissionStrategy {

    private static final double COMMISSION_RATE = 0.10;

    @Override
    public double calculateCommission(double amount) {
        return amount * COMMISSION_RATE;
    }
}
