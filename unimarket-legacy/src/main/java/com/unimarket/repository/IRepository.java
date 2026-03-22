package com.unimarket.repository;

import java.util.List;

/**
 * Interfaz genérica de repositorio para operaciones CRUD básicas.
 *
 * @param <T> el tipo de entidad gestionada
 */
public interface IRepository<T> {

    void save(T entity);

    List<T> findAll();

    T findById(String id);
}
