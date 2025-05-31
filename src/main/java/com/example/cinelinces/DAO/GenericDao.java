package com.example.cinelinces.DAO;

import java.io.Serializable;
import java.util.List;


public interface GenericDao<T, ID extends Serializable> {

    T findById(ID id);

    List<T> findAll();

    void save(T entity);

    T update(T entity);

    void delete(T entity);

    void deleteById(ID id);
}
