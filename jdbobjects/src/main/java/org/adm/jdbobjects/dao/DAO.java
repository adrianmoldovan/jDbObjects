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
    T update(T entity);

    /**
     * Deletes the entity.
     */
    boolean delete(T entity);

    
    /**
     * returns all entities
     */
    List<T> find();

    /**
     * returns the entity matching criteria {field:value}
     */
    T findOne(String field, Object value);
    
    /**
     * returns the entity matching criteria
     */
    T findOne(String query);

    /**
     * returns the entities matching the criteria
     */
    List<T> listByField(String field, Object value);
    
    /**
     * returns the entities matching the criteria
     */
    List<T> listByFields(List<String> fields, List<Object> values);
    
    /**
     * returns the total count
     */
    long count();

}
