package org.adm.jdbobjects.dao;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.adm.jdbobjects.annotation.DbEntity;
import org.adm.jdbobjects.annotation.DbField;
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
	    long id = getLastInsertedID(connection);

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
    public List<T> listAll() {
	try {
	    if (type.isAnnotationPresent(DbEntity.class)) {
		DbEntity entityA = type.getAnnotation(DbEntity.class);
		StringBuilder query = new StringBuilder();
		query.append("SELECT * FROM ");
		query.append(" `" + entityA.name() + "`;");
		System.err.println(query);
		PreparedStatement statement = this.connection
			.prepareStatement(query.toString());
		ResultSet rs = statement.executeQuery(query.toString());
		List<T> result = new ArrayList<T>();
		while (rs.next()) {
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
			} else if (dbField.type().equals(Date.class)) {
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
		    result.add(object);
		}
		return result;
	    }
	    return null;
	} catch (Exception e) {
	    LOGGER.fatal(e.getMessage(), e);
	    return null;
	}
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

    private PreparedStatement createInsertStatement(T obj) throws Exception {
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
	    System.err.println(statement.toString());
	    return statement;
	}
	return null;
    }

    private PreparedStatement createUpdateStatement(T obj) throws Exception {
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
	    DbEntity entityA = obj.getClass().getAnnotation(DbEntity.class);
	    String table = entityA.name();
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

    public static long getLastInsertedID(Connection connection)
	    throws Exception {
	PreparedStatement statement = null;
	ResultSet rs = null;

	statement = connection
		.prepareStatement("SELECT LAST_INSERT_ID() AS last_id");
	rs = statement.executeQuery();
	if (rs.next()) {
	    return rs.getLong("last_id");
	}
	return 0;
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

}
