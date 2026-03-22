package com.unimarket.unimarket_ventas;

import com.unimarket.unimarket_ventas.factory.IMarketFactory;
import com.unimarket.unimarket_ventas.factory.SchoolSuppliesFactory;
import com.unimarket.unimarket_ventas.factory.TechFactory;
import com.unimarket.unimarket_ventas.model.Product;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    @GetMapping("/catalogo")
    public List<Product> obtenerCatalogo() {
        List<Product> catalogo = new ArrayList<>();

        // Fábrica de Tecnología → crea Laptop (físico) y EBook (digital)
        IMarketFactory techFactory = new TechFactory();
        catalogo.add(techFactory.createPhysicalProduct("MacBook Pro M4", 2499.99));
        catalogo.add(techFactory.createDigitalProduct("Clean Code eBook", 29.99));

        // Fábrica de Útiles Escolares → crea Notebook (físico) y CoursePDF (digital)
        IMarketFactory schoolFactory = new SchoolSuppliesFactory();
        catalogo.add(schoolFactory.createPhysicalProduct("Cuaderno Universitario 100 hojas", 8.50));
        catalogo.add(schoolFactory.createDigitalProduct("Guía de Cálculo Integral PDF", 12.99));

        return catalogo;
    }
}
