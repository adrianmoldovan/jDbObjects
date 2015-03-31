package org.adm.jdbobjects.dao;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import javax.management.QueryExp;

import org.adm.jdbobjects.annotation.DbEntity;
import org.adm.jdbobjects.annotation.DbField;
import org.adm.jdbobjects.dao.DAO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BasicDAO<T> implements DAO<T> {

    private static final Logger LOGGER = LogManager.getLogger();

    private Connection connection;

    public BasicDAO(Connection connection) {
	this.connection = connection;
    }

    @Override
    public T insert(T entity) {
	try {
	    StringBuilder query = createInsertQuery(entity);
	    return entity;
	} catch (Exception e) {
	    LOGGER.fatal(e.getMessage(), e);
	    return null;
	}
    }

    @Override
    public long update(T entity) {
	try {
	    StringBuilder query = createUpdateQuery(entity);
	    return 1;
	} catch (Exception e) {
	    LOGGER.fatal(e.getMessage(), e);
	    return 0;
	}
    }

    @Override
    public boolean delete(T entity) {
	try {
	    StringBuilder query = createDeleteQuery(entity);
	    return true;
	} catch (Exception e) {
	    LOGGER.fatal(e.getMessage(), e);
	    return false;
	}
    }

    @Override
    public List<T> listAll() {
	return null;
    }

    @Override
    public T findByID(long id) {
	return null;
    }

    @Override
    public List<T> listByField(String field, Object value) {
	return null;
    }

    @Override
    public List<T> listByFields(String[] field, Object[] value) {
	// TODO Auto-generated method stub
	return null;
    }

    public PreparedStatement createInsertStatement(T obj) throws Exception {
	if (obj.getClass().isAnnotationPresent(DbEntity.class)) {
	    DbEntity entityA = obj.getClass().getAnnotation(DbEntity.class);
	    StringBuilder query = new StringBuilder();
	    query.append("INSERT INTO");
	    query.append(" `" + entityA.name() + "` ");
	    query.append(" (");

	    String fields = "";
	    String values = "";
	    for (Field field : obj.getClass().getDeclaredFields()) {
		DbField dbField = field.getAnnotation(DbField.class);
		fields += "`" + dbField.name() + "`, ";
		field.setAccessible(true);
		values += "?, ";
	    }
	    query.append(fields.substring(0, fields.length() - 2));
	    query.append(") ");
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
	    System.err.println(statement.toString());
	    return statement;
	}
	return null;

    }

    private StringBuilder createInsertQuery(T obj) throws Exception {
	if (obj.getClass().isAnnotationPresent(DbEntity.class)) {
	    DbEntity entityA = obj.getClass().getAnnotation(DbEntity.class);

	    StringBuilder query = new StringBuilder();
	    query.append("INSERT INTO");
	    query.append(" `" + entityA.name() + "` ");
	    query.append(" (");
	    String fields = "";
	    String values = "";
	    for (Field field : obj.getClass().getDeclaredFields()) {
		DbField dbField = field.getAnnotation(DbField.class);
		fields += "`" + dbField.name() + "`, ";
		field.setAccessible(true);
		values += "'" + field.get(obj) + "', ";
	    }
	    query.append(fields.substring(0, fields.length() - 2));
	    query.append(") ");
	    query.append(" VALUES ");
	    query.append("( ");
	    query.append(values.substring(0, values.length() - 2));
	    query.append(");");
	    return query;
	}
	return new StringBuilder();
    }

    private StringBuilder createUpdateQuery(T obj) throws Exception {
	if (obj.getClass().isAnnotationPresent(DbEntity.class)) {
	    DbEntity entityA = obj.getClass().getAnnotation(DbEntity.class);

	    String table = entityA.name();

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
		    continue;
		}
		if (fields.length() == 0)
		    fields += "SET ";
		fields += dbField.name() + "= '" + field.get(obj) + "', ";
	    }
	    query.append(fields.substring(0, fields.length() - 2));
	    query.append(" WHERE ");
	    query.append(primaryKey).append("=")
		    .append("'" + primaryValue + "';");
	    return query;
	}
	return new StringBuilder();
    }

    private StringBuilder createDeleteQuery(T obj) throws Exception {
	if (obj.getClass().isAnnotationPresent(DbEntity.class)) {
	    DbEntity entityA = obj.getClass().getAnnotation(DbEntity.class);

	    String table = entityA.name();

	    StringBuilder query = new StringBuilder();
	    query.append("DELETE FROM");
	    query.append(" `" + table + "`");
	    String primaryKey = "";
	    Object primaryValue = "";
	    for (Field field : obj.getClass().getDeclaredFields()) {
		DbField dbField = field.getAnnotation(DbField.class);
		field.setAccessible(true);
		if (dbField.isPrimaryKey()) {
		    primaryKey = dbField.name();
		    primaryValue = field.get(obj);
		    continue;
		}
	    }
	    query.append(" WHERE ");
	    query.append(primaryKey).append("=")
		    .append("'" + primaryValue + "';");
	    return query;
	}
	return new StringBuilder();
    }

}
