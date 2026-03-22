package com.unimarket.strategy;

/**
 * Context del patrón Strategy: delega el cálculo de comisión a la estrategia configurada.
 */
public class CommissionContext {

    private ICommissionStrategy strategy;

    public CommissionContext() {
    }

    public CommissionContext(ICommissionStrategy strategy) {
        this.strategy = strategy;
    }

    public void setStrategy(ICommissionStrategy strategy) {
        this.strategy = strategy;
    }

    public ICommissionStrategy getStrategy() {
        return strategy;
    }

    /**
     * Ejecuta la estrategia de comisión sobre el monto dado.
     *
     * @param amount el subtotal de la compra
     * @return el monto de la comisión
     */
    public double executeStrategy(double amount) {
        if (strategy == null) {
            throw new IllegalStateException("No se ha configurado una estrategia de comisión.");
        }
        return strategy.calculateCommission(amount);
    }
}
