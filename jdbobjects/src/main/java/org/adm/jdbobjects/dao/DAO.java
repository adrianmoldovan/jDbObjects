/*
 * (C) Copyright jDBObjects.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 3 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl-3.0.html
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
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
