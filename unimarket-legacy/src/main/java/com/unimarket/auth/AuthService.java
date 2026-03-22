package com.unimarket.auth;

import com.unimarket.model.User;
import com.unimarket.repository.IRepository;

/**
 * Implementación del servicio de autenticación.
 * Usa el repositorio de usuarios inyectado para buscar por username.
 */
public class AuthService implements IAuthService {

    private final IRepository<User> userRepo;

    public AuthService(IRepository<User> userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public User login(String username) {
        if (username == null || username.trim().isEmpty()) {
            return null;
        }
        return userRepo.findById(username.trim());
    }
}
