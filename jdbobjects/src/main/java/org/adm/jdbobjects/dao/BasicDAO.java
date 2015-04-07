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

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.adm.jdbobjects.DatabaseConnection;
import org.adm.jdbobjects.annotation.DbEntity;
import org.adm.jdbobjects.annotation.DbField;
import org.adm.jdbobjects.exception.DatabaseQueryException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BasicDAO<T> implements DAO<T> {

    private static final Logger LOGGER = LogManager.getLogger();

    private Connection connection;

    private Class<T> type;

    public BasicDAO(Connection connection, Class<T> type) {
	this.connection = connection;
	this.type = type;
    }

    @Override
    public T insert(T entity) {
	try {
	    PreparedStatement stmt = createInsertStatement(entity);
	    int result = stmt.executeUpdate();
	    if (result != 1)
		return null;
	    long id = getLastInsertedID();

	    for (Field field : entity.getClass().getDeclaredFields()) {
		DbField dbField = field.getAnnotation(DbField.class);
		field.setAccessible(true);
		if (dbField.isPrimaryKey()) {
		    field.set(entity, id);
		    break;
		}
	    }
	    return entity;
	} catch (Exception e) {
	    LOGGER.fatal(e.getMessage(), e);
	    return null;
	}
    }

    @Override
    public T update(T entity) {
	try {
	    PreparedStatement stmt = createUpdateStatement(entity);
	    int result = stmt.executeUpdate();
	    if (result != 1)
		return null;
	    return entity;
	} catch (Exception e) {
	    LOGGER.fatal(e.getMessage(), e);
	    return null;
	}
    }

    @Override
    public boolean delete(T entity) {
	try {
	    PreparedStatement stmt = createDeleteStatement(entity);
	    int result = stmt.executeUpdate();
	    if (result == 0)
		return false;
	    return true;
	} catch (Exception e) {
	    LOGGER.fatal(e.getMessage(), e);
	    return false;
	}
    }

    @Override
    public List<T> find() {
	try {
	    if (type.isAnnotationPresent(DbEntity.class)) {
		DbEntity entityA = type.getAnnotation(DbEntity.class);
		StringBuilder query = new StringBuilder();
		query.append("SELECT * FROM ");
		query.append(" `" + entityA.name() + "`;");
		return doQuery(query.toString());
	    }
	    return null;
	} catch (Exception e) {
	    LOGGER.fatal(e.getMessage(), e);
	    return null;
	}
    }

    @Override
    public T findOne(String field, Object value) {
	try {
	    if (type.isAnnotationPresent(DbEntity.class)) {
		DbEntity entityA = type.getAnnotation(DbEntity.class);
		StringBuilder query = new StringBuilder();
		query.append("SELECT * FROM ");
		query.append(" `" + entityA.name() + "`");
		query.append(" WHERE ");
		query.append(field + "='" + value + "';");
		return findOne(query.toString());
	    }
	    return null;
	} catch (Exception e) {
	    LOGGER.fatal(e.getMessage(), e);
	    return null;
	}
    }

    @Override
    public T findOne(String query) {
	try {
	    List<T> result = doQuery(query);
	    if (result.size() > 0)
		return result.get(0);
	    return null;
	} catch (Exception e) {
	    LOGGER.fatal(e.getMessage(), e);
	    return null;
	}
    }

    @Override
    public List<T> listByField(String field, Object value) {
	List<String> fields = new ArrayList<>();
	fields.add(field);
	List<Object> values = new ArrayList<>();
	values.add(value);
	return listByFields(fields, values);
    }

    @Override
    public List<T> listByFields(List<String> fields, List<Object> values) {
	if (fields.size() != values.size())
	    return null;
	try {
	    if (type.isAnnotationPresent(DbEntity.class)) {
		DbEntity entityA = type.getAnnotation(DbEntity.class);
		StringBuilder query = new StringBuilder();
		query.append("SELECT * FROM ");
		query.append(" `" + entityA.name() + "`");

		String where = "";
		for (int i = 0; i < fields.size(); i++) {
		    if (where.length() > 0)
			where += " AND ";
		    where += fields.get(i) + "='" + values.get(i) + "'";
		}
		if (where.length() > 0) {
		    query.append(" WHERE ").append(where);
		}
		return doQuery(query.toString());
	    }
	    return null;
	} catch (Exception e) {
	    LOGGER.fatal(e.getMessage(), e);
	    return null;
	}
    }

    @Override
    public long count() {
	try {
	    if (type.isAnnotationPresent(DbEntity.class)) {
		String query = "SELECT COUNT(*) as `count` FROM `"
			+ getTableName() + "`;";
		Object obj = doQueryValue(query);
		try {
		    return (Long) obj;
		} catch (Exception e) {
		    return 0;
		}
	    }
	    return 0;
	} catch (Exception e) {
	    LOGGER.fatal(e.getMessage(), e);
	    return 0;
	}
    }

    private PreparedStatement createInsertStatement(T obj) throws Exception {
	if (obj.getClass().isAnnotationPresent(DbEntity.class)) {
	    StringBuilder query = new StringBuilder();
	    query.append("INSERT INTO");
	    query.append(" `" + getTableName() + "` ");
	    query.append(" (");

	    String fields = "";
	    String values = "";
	    for (Field field : obj.getClass().getDeclaredFields()) {
		DbField dbField = field.getAnnotation(DbField.class);
		fields += "`" + dbField.name() + "`, ";
		values += "?, ";
	    }
	    query.append(fields.substring(0, fields.length() - 2));
	    query.append(")");
	    query.append(" VALUES ");
	    query.append("( ");
	    query.append(values.substring(0, values.length() - 2));
	    query.append(");");
	    PreparedStatement statement = this.connection
		    .prepareStatement(query.toString());
	    int i = 1;
	    for (Field field : obj.getClass().getDeclaredFields()) {
		field.setAccessible(true);
		statement.setObject(i++, field.get(obj));
	    }
	    return statement;
	}
	return null;
    }

    private PreparedStatement createUpdateStatement(T obj) throws Exception {
	if (obj.getClass().isAnnotationPresent(DbEntity.class)) {
	    String table = getTableName();
	    StringBuilder query = new StringBuilder();
	    query.append("UPDATE");
	    query.append(" `" + table + "` ");
	    String fields = "";
	    String primaryKey = "";
	    Object primaryValue = "";
	    for (Field field : obj.getClass().getDeclaredFields()) {
		DbField dbField = field.getAnnotation(DbField.class);
		field.setAccessible(true);
		if (dbField.isPrimaryKey()) {
		    primaryKey = dbField.name();
		    primaryValue = field.get(obj);
		}
		if (fields.length() == 0)
		    fields += "SET ";
		fields += dbField.name() + "= ?, ";
	    }
	    query.append(fields.substring(0, fields.length() - 2));
	    query.append(" WHERE ");
	    query.append(primaryKey).append("=")
		    .append("'" + primaryValue + "';");

	    PreparedStatement statement = this.connection
		    .prepareStatement(query.toString());
	    int i = 1;
	    for (Field field : obj.getClass().getDeclaredFields()) {
		field.setAccessible(true);
		statement.setObject(i++, field.get(obj));
	    }

	    return statement;
	}
	return null;
    }

    private PreparedStatement createDeleteStatement(T obj) throws Exception {
	if (obj.getClass().isAnnotationPresent(DbEntity.class)) {
	    String table = getTableName();
	    StringBuilder query = new StringBuilder();
	    query.append("DELETE FROM");
	    query.append(" `" + table + "`");
	    Field field = getPrimaryKey(obj);
	    field.setAccessible(true);
	    DbField dbField = field.getAnnotation(DbField.class);
	    query.append(" WHERE ");
	    query.append(dbField.name()).append("=");
	    query.append("?;");
	    PreparedStatement statement = this.connection
		    .prepareStatement(query.toString());
	    statement.setObject(1, field.get(obj));
	    return statement;
	}
	return null;
    }

    private Field getPrimaryKey(T entity) throws Exception {
	if (entity.getClass().isAnnotationPresent(DbEntity.class)) {
	    DbEntity entityA = entity.getClass().getAnnotation(DbEntity.class);
	    if (entityA.primaryKey().length() > 0) {
		return entity.getClass().getField(entityA.primaryKey());
	    }
	    for (Field field : entity.getClass().getDeclaredFields()) {
		DbField dbField = field.getAnnotation(DbField.class);
		field.setAccessible(true);
		if (dbField.isPrimaryKey()) {
		    return field;
		}
	    }
	}
	return null;
    }

    private String getTableName() {
	if (type.isAnnotationPresent(DbEntity.class)) {
	    DbEntity entityA = type.getAnnotation(DbEntity.class);
	    if (entityA != null)
		return entityA.name();
	}
	return null;
    }

    public long getLastInsertedID() throws Exception {
	PreparedStatement statement = null;
	ResultSet rs = null;

	statement = this.connection
		.prepareStatement("SELECT LAST_INSERT_ID() AS last_id");
	rs = statement.executeQuery();
	if (rs.next()) {
	    return rs.getLong("last_id");
	}
	return 0;
    }

    public List<T> doQuery(String query) throws DatabaseQueryException {
	List<T> result = new ArrayList<T>();
	ResultSet resultSet = null;
	PreparedStatement statement = null;
	try {
	    statement = connection.prepareStatement(query);
	    // Executing the query.
	    resultSet = statement.executeQuery();
	    while (resultSet.next()) {
		T obj = setFields(resultSet);
		result.add(obj);
	    }
	} catch (Exception e) {
	    LOGGER.error(e.toString(), e);
	    throw new DatabaseQueryException();
	} finally {
	    DatabaseConnection.close(resultSet, statement, connection);
	}
	return result;
    }

    public Object doQueryValue(String query) throws DatabaseQueryException {
	ResultSet resultSet = null;
	PreparedStatement statement = null;
	try {
	    statement = connection.prepareStatement(query);
	    // Executing the query.
	    resultSet = statement.executeQuery();
	    while (resultSet.next()) {
		return resultSet.getObject(1);
	    }
	} catch (Exception e) {
	    LOGGER.error(e.toString(), e);
	    throw new DatabaseQueryException();
	} finally {
	    DatabaseConnection.close(resultSet, statement, connection);
	}
	return null;
    }

    private T setFields(ResultSet rs) throws Exception {
	T object = type.newInstance();
	for (Field field : object.getClass().getDeclaredFields()) {
	    DbField dbField = field.getAnnotation(DbField.class);
	    if (dbField == null)
		continue;
	    field.setAccessible(true);
	    if (dbField.type().equals(String.class)) {
		field.set(object, rs.getString(dbField.name()));
	    } else if (dbField.type().equals(Long.class)) {
		field.set(object, rs.getLong(dbField.name()));
	    } else if (dbField.type().equals(java.util.Date.class)) {
		field.set(object, rs.getDate(dbField.name()));
	    } else if (dbField.type().equals(Integer.class)) {
		field.set(object, rs.getInt(dbField.name()));
	    } else if (dbField.type().equals(Boolean.class)) {
		field.set(object, rs.getBoolean(dbField.name()));
	    } else if (dbField.type().equals(Double.class)) {
		field.set(object, rs.getDouble(dbField.name()));
	    } else if (dbField.type().equals(Float.class)) {
		field.set(object, rs.getFloat(dbField.name()));
	    }
	}
	return object;
    }
}
