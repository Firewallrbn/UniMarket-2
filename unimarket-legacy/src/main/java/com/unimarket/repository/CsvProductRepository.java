package com.unimarket.repository;

import com.unimarket.model.*;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Repositorio CSV para productos.
 * Formato: id,name,price,category,productType
 */
public class CsvProductRepository implements IRepository<Product> {

    private static final String FILE_PATH = "data/products.csv";
    private static final String HEADER = "id,name,price,category,productType";

    public CsvProductRepository() {
        ensureFileExists();
    }

    @Override
    public void save(Product product) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(product.getId() + ","
                    + product.getName() + ","
                    + product.getPrice() + ","
                    + product.getCategory() + ","
                    + product.getProductType());
            writer.newLine();
        } catch (IOException e) {
            throw new RuntimeException("Error al guardar producto: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Product> findAll() {
        List<Product> products = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            boolean isHeader = true;
            while ((line = reader.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }
                String trimmed = line.trim();
                if (trimmed.isEmpty()) continue;

                String[] parts = trimmed.split(",");
                if (parts.length >= 5) {
                    int id = Integer.parseInt(parts[0].trim());
                    String name = parts[1].trim();
                    double price = Double.parseDouble(parts[2].trim());
                    String category = parts[3].trim();
                    String productType = parts[4].trim();

                    Product product = createProductFromType(id, name, price, productType);
                    if (product != null) {
                        products.add(product);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error al leer productos: " + e.getMessage(), e);
        }
        return products;
    }

    @Override
    public Product findById(String id) {
        try {
            int productId = Integer.parseInt(id);
            return findAll().stream()
                    .filter(p -> p.getId() == productId)
                    .findFirst()
                    .orElse(null);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public boolean isEmpty() {
        return findAll().isEmpty();
    }

    /**
     * Reconstruye el tipo concreto de producto a partir del CSV.
     */
    private Product createProductFromType(int id, String name, double price, String productType) {
        return switch (productType) {
            case "Laptop" -> new Laptop(id, name, price);
            case "EBook" -> new EBook(id, name, price);
            case "Notebook" -> new Notebook(id, name, price);
            case "CoursePDF" -> new CoursePDF(id, name, price);
            default -> null;
        };
    }

    private void ensureFileExists() {
        try {
            Path path = Paths.get(FILE_PATH);
            if (!Files.exists(path.getParent())) {
                Files.createDirectories(path.getParent());
            }
            if (!Files.exists(path)) {
                Files.createFile(path);
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
                    writer.write(HEADER);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error al crear archivo CSV de productos: " + e.getMessage(), e);
        }
    }
}
