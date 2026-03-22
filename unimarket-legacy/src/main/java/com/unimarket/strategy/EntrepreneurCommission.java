package com.unimarket.strategy;

/**
 * Comisión reducida del 5% para emprendedores.
 */
public class EntrepreneurCommission implements ICommissionStrategy {

    private static final double COMMISSION_RATE = 0.05;

    @Override
    public double calculateCommission(double amount) {
        return amount * COMMISSION_RATE;
    }
}
