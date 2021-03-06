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
package org.adm.jdbobjects;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.adm.jdbobjects.annotation.DbEntity;
import org.adm.jdbobjects.annotation.DbField;

public class DatabaseManager {

    public static long getLastInsertedID(Connection connection,
	    PreparedStatement statement) throws Exception {
	ResultSet rs = null;

	statement = connection
		.prepareStatement("SELECT LAST_INSERT_ID() AS last_id");
	rs = statement.executeQuery();
	if (rs.next()) {
	    return rs.getLong("last_id");
	}
	return 0;
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

    public static void insert(Object user) throws Exception {
	if (user.getClass().isAnnotationPresent(DbEntity.class)) {
	    DbEntity entityA = user.getClass().getAnnotation(DbEntity.class);

	    StringBuilder query = new StringBuilder();
	    query.append("INSERT INTO");
	    query.append(" `" + entityA.name() + "` ");
	    query.append(" (");
	    String fields = "";
	    String values = "";
	    for (Field field : user.getClass().getDeclaredFields()) {
		DbField dbField = field.getAnnotation(DbField.class);
		fields += "`" + dbField.name() + "`, ";
		field.setAccessible(true);
		values += "'" + field.get(user) + "', ";
	    }
	    query.append(fields.substring(0, fields.length() - 2));
	    query.append(") ");
	    query.append(" VALUES ");
	    query.append("( ");
	    query.append(values.substring(0, values.length() - 2));
	    query.append(");");
	    System.err.println(query);
	}
    }

    public static void update(Object obj) throws Exception {
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
	    System.err.println(query);
	}
    }

    public static void delete(Object obj) throws Exception {
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
	    System.err.println(query);
	}
    }

}
