package com.example.cinelinces.DAO;

import java.io.Serializable;
import java.util.List;

/**
 * Interfaz genérica para operaciones CRUD.
 *
 * @param <T>  Tipo de entidad
 * @param <ID> Tipo de la clave primaria (Serializable)
 */
public interface GenericDao<T, ID extends Serializable> {

    T findById(ID id);

    List<T> findAll();

    void save(T entity);

    T update(T entity);

    void delete(T entity);

    void deleteById(ID id);
}
