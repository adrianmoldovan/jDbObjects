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

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DatabaseConnection {

    private final static Logger LOGGER = LogManager.getLogger();

    public static DatabaseConnection instance;
    private Connection connection;

    public DatabaseConnection() {
	try {
	    ConnectionDetails con = new ConnectionDetails(new File("D:\\adm\\Repositories\\jDbObjects\\jdbobjects-sample\\src\\main\\resources\\mysql.conf"));//TODO just for tests
	    connection = DriverManager.getConnection(con.getURL(), con.getUser(), con.getPassword());
	} catch (Exception e) {
	    LOGGER.error(e.getMessage(), e);
	}
    }

    public static DatabaseConnection getInstance() {
	if (instance != null)
	    return instance;
	instance = new DatabaseConnection();
	return instance;
    }

    public Connection getConnection() throws Exception {
	instance = DatabaseConnection.getInstance();
	return instance.connection;
    }

    public static void closeResultSet(ResultSet rs) {
	if (rs != null) {
	    try {
		rs.close();
	    } catch (SQLException e) {
		LOGGER.fatal(e.getMessage(), e);
	    }
	    rs = null;
	}
    }

    public static void closeStatement(Statement stmt) {
	if (stmt != null) {
	    try {
		stmt.close();
	    } catch (SQLException e) {
		LOGGER.fatal(e.getMessage(), e);
	    }
	    stmt = null;
	}
    }

    public static void closePreparedStatement(PreparedStatement stmt) {
	if (stmt != null) {
	    try {
		stmt.close();
	    } catch (SQLException e) {
		LOGGER.fatal(e.getMessage(), e);
	    }
	    stmt = null;
	}
    }

    public static void closeConnection(Connection conn) {
	if (conn != null) {
	    try {
		conn.close();
	    } catch (SQLException e) {
		LOGGER.fatal(e.getMessage(), e);
	    }
	    conn = null;
	}
    }

    public static void close(ResultSet rs, Statement stmt, Connection conn) {
	closeResultSet(rs);
	closeStatement(stmt);
	closeConnection(conn);
    }
}