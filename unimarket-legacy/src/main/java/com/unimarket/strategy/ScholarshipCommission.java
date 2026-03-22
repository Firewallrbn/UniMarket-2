package com.unimarket.strategy;

/**
 * Comisión del 0% para becados (sin cargo).
 */
public class ScholarshipCommission implements ICommissionStrategy {

    private static final double COMMISSION_RATE = 0.0;

    @Override
    public double calculateCommission(double amount) {
        return amount * COMMISSION_RATE;
    }
}
