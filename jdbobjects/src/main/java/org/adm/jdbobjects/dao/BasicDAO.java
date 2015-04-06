package org.adm.jdbobjects.dao;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.UUID;

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
    public List<T> find() {
	try {
	    if (type.isAnnotationPresent(DbEntity.class)) {
		DbEntity entityA = type.getAnnotation(DbEntity.class);
		StringBuilder query = new StringBuilder();
		query.append("SELECT * FROM ");
		query.append(" `"+entityA.name()+"`;");
		System.err.println(query);
		PreparedStatement statement = this.connection
			.prepareStatement(query.toString());
		ResultSet rs = statement.executeQuery(query.toString());
		List<T> result = new ArrayList<T>();
		while (rs.next()) {
		    T object = setFields(rs);
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

    @Override
    public T findOne(String field, Object value) {
	try {
	    if (type.isAnnotationPresent(DbEntity.class)) {
		DbEntity entityA = type.getAnnotation(DbEntity.class);
		StringBuilder query = new StringBuilder();
		query.append("SELECT * FROM ");
		query.append(" `"+entityA.name()+"`");
		query.append(" WHERE ");
		query.append(field + "='"+value+"';");
		System.err.println(query);
		PreparedStatement statement = this.connection
			.prepareStatement(query.toString());
		ResultSet rs = statement.executeQuery(query.toString());
		while (rs.next()) {
		    T object = setFields(rs);
		    return object;
		}
	    }
	    return null;
	} catch (Exception e) {
	    LOGGER.fatal(e.getMessage(), e);
	    return null;
	}
    }

    @Override
    public List<T> listByField(String field, Object value) {
	return null;
    }

    @Override
    public List<T> listByFields(List<String> fields,  List<Object> values) {
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
    
    private String getTableName(){
	if (type.isAnnotationPresent(DbEntity.class)) {
	    DbEntity entityA = type.getAnnotation(DbEntity.class);
	    if (entityA != null)
		return entityA.name();
	}
	return null;
    }
    
    
    ///////////////////////////////////////////
    public ArrayList<T> doQuery(String query)
	    throws DatabaseQueryException {
	ArrayList<T> result = new ArrayList<T>();
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

    private ArrayList<T> doQuery(
	    String queryString, Object[] parameters, Class<T> type)
	    throws Exception {
	ArrayList<T> res = new ArrayList<T>();
	ResultSet resultSet = null;
	PreparedStatement statement = null;
	try {
	    // Getting the prepared statement.
	    statement = prepareStatement(connection, queryString, parameters);

	    // Executing the query.
	    LOGGER.debug("DatabaseManager.doQuery: " + statement);
	    resultSet = statement.executeQuery();

	    // Creating new objects of given class by analyzing each field and
	    // invoking the
	    // corresponding set-methods.
	    while (resultSet.next()) {
		T obj = setFields(resultSet);
		res.add(obj);
	    }

	} catch (Exception e) {
	    LOGGER.error(e.toString(), e);
	    throw e;
	} finally {
	    DatabaseConnection.close(resultSet, statement, connection);
	}
	return res;
    }

    public ArrayList<T> getAll()
	    throws Exception {
	return getAll("");
    }

    public ArrayList<T> getAll(String appendedSqlString) throws Exception {
	String sqlString = "SELECT * FROM "
		+ getTableName() + " "
		+ appendedSqlString + "";
	return doQuery(sqlString, new Object[] {}, type);
    }

    public ArrayList<T> getByField(String fieldName, Object fieldContent) throws Exception {
	return getByField(fieldName, fieldContent, "");
    }

    public ArrayList<T> getByField(String fieldName, Object fieldContent, String appendedSqlString)
	    throws Exception {
	return getByFields(new String[] { fieldName },
		new Object[] { fieldContent }, appendedSqlString);
    }

    public  ArrayList<T> getByFields(String[] fieldsName, Object[] fieldsContent) throws Exception {
	return getByFields(fieldsName, fieldsContent, "");
    }

    public ArrayList<T> getByFields(String[] fieldsName, Object[] fieldsContent,
	    String appendedSqlString) throws Exception {
	T obj = type.newInstance();

	String sqlString = "SELECT * FROM " + getTableName()
		+ " WHERE ";
	for (int i = 0; i < fieldsName.length; i++) {
	    String columnName = fieldsName[i];
	    sqlString += "`" + columnName + "`=? ";
	    if (i < fieldsName.length - 1)
		sqlString += " AND ";
	}
	sqlString += appendedSqlString + "";
	return doQuery(sqlString, fieldsContent, type);
    }

    public ArrayList<T> getByWhereClause(
	    Class<T> type, String whereClause) throws Exception {
	String sqlString = "SELECT * FROM "
		+ getTableName() + " WHERE "
		+ whereClause + "";

	return doQuery(sqlString, new Object[] {}, type);
    }

    public T getObjectDBbyID(long id)
	    throws Exception {
	ArrayList<T> res = getByField("id", id + "");
	if (res != null && res.size() > 0)
	    return res.get(0);
	return null;
    }

    public T getObjectDBbyField(String field, String value) throws Exception {
	ArrayList<T> res = getByField(field, value);
	if (res != null && res.size() > 0)
	    return res.get(0);
	return null;
    }

    public T getObjectDBbyFields(String[] field, String[] value) throws Exception {
	ArrayList<T> res = getByFields(field, value);
	if (res != null && res.size() > 0)
	    return res.get(0);
	return null;
    }

      public static PreparedStatement prepareStatement(Connection connection,
	    String sqlString, Object[] parameters, int returnKey)
	    throws SQLException {

	PreparedStatement statement = null;
	if (returnKey == Statement.RETURN_GENERATED_KEYS)
	    statement = connection.prepareStatement(sqlString, returnKey);
	else
	    statement = connection.prepareStatement(sqlString, returnKey);
	statement.clearParameters();
	for (int i = 0; i < parameters.length; i++) {
	    if (parameters[i] instanceof String) {
		statement.setString(i + 1, (String) parameters[i]);
	    } else if (parameters[i] instanceof Integer) {
		statement.setInt(i + 1, ((Integer) parameters[i]).intValue());
	    } else if (parameters[i] instanceof Long) {
		statement.setLong(i + 1, ((Long) parameters[i]).longValue());
	    } else if (parameters[i] instanceof Float) {
		statement.setFloat(i + 1, ((Float) parameters[i]).floatValue());
	    } else if (parameters[i] instanceof Double) {
		statement.setDouble(i + 1,
			((Double) parameters[i]).doubleValue());
	    } else if (parameters[i] instanceof Date) {
		statement.setDate(i + 1, ((Date) parameters[i]));
	    } else if (parameters[i] instanceof Time) {
		statement.setTime(i + 1, ((Time) parameters[i]));
	    } else if (parameters[i] instanceof Timestamp) {
		statement.setTimestamp(i + 1, ((Timestamp) parameters[i]));
	    } else {
		statement.setObject(i + 1, parameters[i]);
	    }
	}
	return statement;
    }

    public PreparedStatement prepareStatement(Connection connection,
	    String sqlString, Object[] parameters) throws SQLException {
	return prepareStatement(connection, sqlString, parameters, -1);
    }

    @Override
    public T findOne(String query) {
	return null;
    }

    @Override
    public long count() {
	return 0;
    }
}
