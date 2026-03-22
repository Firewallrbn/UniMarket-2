package com.unimarket.data;

import com.unimarket.factory.IMarketFactory;
import com.unimarket.factory.SchoolSuppliesFactory;
import com.unimarket.factory.TechFactory;
import com.unimarket.model.Product;
import com.unimarket.model.User;
import com.unimarket.model.UserType;
import com.unimarket.repository.CsvProductRepository;
import com.unimarket.repository.CsvUserRepository;

/**
 * Carga datos iniciales de prueba si los archivos CSV están vacíos.
 */
public class DataSeeder {

    private final CsvUserRepository userRepository;
    private final CsvProductRepository productRepository;

    public DataSeeder(CsvUserRepository userRepository, CsvProductRepository productRepository) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    /**
     * Seed de usuarios de prueba.
     */
    public void seedUsers() {
        if (!userRepository.isEmpty()) return;

        userRepository.save(new User("carlos", UserType.VENDEDOR_CASUAL));
        userRepository.save(new User("maria", UserType.EMPRENDEDOR));
        userRepository.save(new User("andres", UserType.BECADO));
        userRepository.save(new User("lucia", UserType.VENDEDOR_CASUAL));
        userRepository.save(new User("pedro", UserType.EMPRENDEDOR));
    }

    /**
     * Seed de productos de prueba usando ambas fábricas.
     */
    public void seedProducts() {
        if (!productRepository.isEmpty()) return;

        // Familia Tecnología
        IMarketFactory techFactory = new TechFactory();
        productRepository.save(techFactory.createPhysicalProduct("Laptop HP Pavilion", 899.99));
        productRepository.save(techFactory.createPhysicalProduct("Laptop Dell XPS 13", 1249.99));
        productRepository.save(techFactory.createPhysicalProduct("Laptop Lenovo ThinkPad", 1099.50));
        productRepository.save(techFactory.createDigitalProduct("Clean Code eBook", 29.99));
        productRepository.save(techFactory.createDigitalProduct("Design Patterns eBook", 34.50));
        productRepository.save(techFactory.createDigitalProduct("Java Concurrency eBook", 24.99));

        // Familia Útiles Escolares
        IMarketFactory schoolFactory = new SchoolSuppliesFactory();
        productRepository.save(schoolFactory.createPhysicalProduct("Cuaderno Profesional 100H", 5.50));
        productRepository.save(schoolFactory.createPhysicalProduct("Cuaderno Universitario 200H", 8.75));
        productRepository.save(schoolFactory.createPhysicalProduct("Cuaderno de Dibujo A4", 12.00));
        productRepository.save(schoolFactory.createDigitalProduct("Curso de Cálculo PDF", 15.00));
        productRepository.save(schoolFactory.createDigitalProduct("Curso de Física PDF", 18.50));
        productRepository.save(schoolFactory.createDigitalProduct("Curso de Programación PDF", 22.00));
    }
}
