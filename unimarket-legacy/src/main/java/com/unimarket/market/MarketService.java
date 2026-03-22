package com.unimarket.market;

import com.unimarket.model.*;
import com.unimarket.repository.IRepository;
import com.unimarket.strategy.*;
import com.unimarket.factory.IMarketFactory;

import java.util.List;

/**
 * Implementación del servicio de mercado.
 * Orquesta carrito, comisiones, productos y ventas.
 */
public class MarketService implements IMarketService {

    private final IRepository<Product> productRepo;
    private final IRepository<Sale> saleRepo;
    private final CommissionContext commissionContext;
    private final IMarketFactory factory;
    private final Cart currentCart;

    public MarketService(IRepository<Product> productRepo,
                         IRepository<Sale> saleRepo,
                         CommissionContext commissionContext,
                         IMarketFactory factory) {
        this.productRepo = productRepo;
        this.saleRepo = saleRepo;
        this.commissionContext = commissionContext;
        this.factory = factory;
        this.currentCart = new Cart();
    }

    @Override
    public List<Product> getCatalog() {
        return productRepo.findAll();
    }

    @Override
    public void addToCart(int productId) {
        Product product = productRepo.findById(String.valueOf(productId));
        if (product == null) {
            throw new IllegalArgumentException("Producto con ID " + productId + " no encontrado.");
        }
        currentCart.add(product);
    }

    @Override
    public double getCartTotal() {
        return currentCart.calculateSubtotal();
    }

    @Override
    public void checkout(User user) {
        if (currentCart.isEmpty()) {
            throw new IllegalStateException("El carrito está vacío. Agregue productos antes de pagar.");
        }

        // Seleccionar estrategia de comisión según el tipo de usuario
        ICommissionStrategy strategy = selectStrategy(user.getType());
        commissionContext.setStrategy(strategy);

        double subtotal = currentCart.calculateSubtotal();
        double commission = commissionContext.executeStrategy(subtotal);
        double total = subtotal + commission;

        // Crear y guardar la venta
        Sale sale = new Sale(user, total, currentCart.getItems());
        saleRepo.save(sale);

        // Limpiar carrito
        currentCart.clear();
    }

    @Override
    public void clearCart() {
        currentCart.clear();
    }

    /**
     * Retorna la comisión calculada para un subtotal dado según el usuario.
     */
    public double getCommission(User user) {
        ICommissionStrategy strategy = selectStrategy(user.getType());
        commissionContext.setStrategy(strategy);
        return commissionContext.executeStrategy(currentCart.calculateSubtotal());
    }

    /**
     * Retorna el nombre de la estrategia actual según el tipo de usuario.
     */
    public String getCommissionName(UserType type) {
        return switch (type) {
            case VENDEDOR_CASUAL -> "Estándar (10%)";
            case EMPRENDEDOR -> "Emprendedor (5%)";
            case BECADO -> "Becado (0%)";
        };
    }

    public Cart getCurrentCart() {
        return currentCart;
    }

    private ICommissionStrategy selectStrategy(UserType type) {
        return switch (type) {
            case VENDEDOR_CASUAL -> new StandardCommission();
            case EMPRENDEDOR -> new EntrepreneurCommission();
            case BECADO -> new ScholarshipCommission();
        };
    }
}
