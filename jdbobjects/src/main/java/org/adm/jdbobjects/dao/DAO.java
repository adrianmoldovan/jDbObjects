package org.adm.jdbobjects.dao;

import java.util.List;

public interface DAO<T> {

    /**
     * Insert the entity; either inserting or overriding the existing entity.
     */
    T insert(T entity);

    /**
     * Updates the entity.
     */
    long update(T entity);

    /**
     * Deletes the entity.
     */
    boolean delete(T entity);

    List<T> listAll();

    T findByID(long id);

    List<T> listByField(String field, Object value);
    
    List<T> listByFields(String[] field, Object[] value);

}
