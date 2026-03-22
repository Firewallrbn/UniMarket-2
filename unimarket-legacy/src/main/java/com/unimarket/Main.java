package com.unimarket;

import com.unimarket.auth.AuthService;
import com.unimarket.auth.IAuthService;
import com.unimarket.data.DataSeeder;
import com.unimarket.factory.IMarketFactory;
import com.unimarket.factory.TechFactory;
import com.unimarket.market.IMarketService;
import com.unimarket.market.MarketService;
import com.unimarket.model.Product;
import com.unimarket.model.Sale;
import com.unimarket.model.User;
import com.unimarket.repository.*;
import com.unimarket.strategy.CommissionContext;
import com.unimarket.ui.ConsoleUI;

/**
 * Punto de entrada de la aplicación UniMarket.
 * Configura las dependencias (DI manual) y arranca la UI de consola.
 */
public class Main {

    public static void main(String[] args) {
        // 1. Instanciar repositorios
        CsvUserRepository userRepository = new CsvUserRepository();
        CsvProductRepository productRepository = new CsvProductRepository();
        CsvSaleRepository saleRepository = new CsvSaleRepository();

        // 2. Seed de datos iniciales (si los CSV están vacíos)
        DataSeeder seeder = new DataSeeder(userRepository, productRepository);
        seeder.seedUsers();
        seeder.seedProducts();

        // 3. Instanciar servicios con inyección de dependencias
        IAuthService authService = new AuthService(userRepository);

        CommissionContext commissionContext = new CommissionContext();
        IMarketFactory factory = new TechFactory(); // Factory por defecto

        IMarketService marketService = new MarketService(
                productRepository,
                saleRepository,
                commissionContext,
                factory
        );

        // 4. Instanciar y arrancar la UI
        ConsoleUI ui = new ConsoleUI(authService, marketService);
        ui.start();
    }
}
