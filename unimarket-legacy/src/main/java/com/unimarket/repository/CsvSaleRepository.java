package com.unimarket.repository;

import com.unimarket.model.Product;
import com.unimarket.model.Sale;
import com.unimarket.model.User;
import com.unimarket.model.UserType;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Repositorio CSV para ventas.
 * Formato: id,date,username,userType,totalPaid,productIds
 */
public class CsvSaleRepository implements IRepository<Sale> {

    private static final String FILE_PATH = "data/sales.csv";
    private static final String HEADER = "id,date,username,userType,totalPaid,productIds";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public CsvSaleRepository() {
        ensureFileExists();
    }

    @Override
    public void save(Sale sale) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            String productIds = sale.getProducts().stream()
                    .map(p -> String.valueOf(p.getId()))
                    .collect(Collectors.joining(";"));

            writer.write(sale.getId() + ","
                    + sale.getDate().format(FORMATTER) + ","
                    + sale.getUser().getUsername() + ","
                    + sale.getUser().getType().name() + ","
                    + String.format("%.2f", sale.getTotalPaid()) + ","
                    + productIds);
            writer.newLine();
        } catch (IOException e) {
            throw new RuntimeException("Error al guardar venta: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Sale> findAll() {
        List<Sale> sales = new ArrayList<>();
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
                    String id = parts[0].trim();
                    LocalDateTime date = LocalDateTime.parse(parts[1].trim(), FORMATTER);
                    String username = parts[2].trim();
                    UserType userType = UserType.valueOf(parts[3].trim());
                    double totalPaid = Double.parseDouble(parts[4].trim());

                    User user = new User(username, userType);
                    Sale sale = new Sale(id, date, user, totalPaid, new ArrayList<>());
                    sales.add(sale);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error al leer ventas: " + e.getMessage(), e);
        }
        return sales;
    }

    @Override
    public Sale findById(String id) {
        return findAll().stream()
                .filter(s -> s.getId().equalsIgnoreCase(id))
                .findFirst()
                .orElse(null);
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
            throw new RuntimeException("Error al crear archivo CSV de ventas: " + e.getMessage(), e);
        }
    }
}
