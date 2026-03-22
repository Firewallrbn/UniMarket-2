package com.unimarket.auth;

import com.unimarket.model.User;

/**
 * Interfaz del servicio de autenticación.
 */
public interface IAuthService {

    /**
     * Busca un usuario por su username.
     *
     * @param username nombre de usuario a buscar
     * @return el objeto User si existe, null si no se encuentra
     */
    User login(String username);
}
