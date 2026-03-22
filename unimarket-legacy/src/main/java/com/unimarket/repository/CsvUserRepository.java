package com.unimarket.repository;

import com.unimarket.model.User;
import com.unimarket.model.UserType;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Repositorio CSV para usuarios.
 * Formato: username,userType
 */
public class CsvUserRepository implements IRepository<User> {

    private static final String FILE_PATH = "data/users.csv";
    private static final String HEADER = "username,userType";

    public CsvUserRepository() {
        ensureFileExists();
    }

    @Override
    public void save(User user) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(user.getUsername() + "," + user.getType().name());
            writer.newLine();
        } catch (IOException e) {
            throw new RuntimeException("Error al guardar usuario: " + e.getMessage(), e);
        }
    }

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
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
                if (parts.length >= 2) {
                    User user = new User(parts[0].trim(), UserType.valueOf(parts[1].trim()));
                    users.add(user);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error al leer usuarios: " + e.getMessage(), e);
        }
        return users;
    }

    @Override
    public User findById(String username) {
        return findAll().stream()
                .filter(u -> u.getUsername().equalsIgnoreCase(username))
                .findFirst()
                .orElse(null);
    }

    public boolean isEmpty() {
        List<User> users = findAll();
        return users.isEmpty();
    }

    private void ensureFileExists() {
        try {
            Path path = Paths.get(FILE_PATH);
            if (!Files.exists(path.getParent())) {
                Files.createDirectories(path.getParent());
            }
            if (!Files.exists(path)) {
                Files.createFile(path);
                // Escribir header
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
                    writer.write(HEADER);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error al crear archivo CSV de usuarios: " + e.getMessage(), e);
        }
    }
}
