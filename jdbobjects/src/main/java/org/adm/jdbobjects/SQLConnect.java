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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SQLConnect {

    private final static Logger LOGGER = LogManager.getLogger();

    protected ConnectionDetails connectionDetails;
    protected Connection connection;
    protected Statement statement;

    public SQLConnect(ConnectionDetails cDetails) {
	this.connectionDetails = cDetails;
    }

    public void connect() throws Exception {
	try {
	    LOGGER.debug("Loading JDBC driver");
	    Class.forName(this.connectionDetails.getClassName());
	    LOGGER.debug("JDBC driver loaded");
	    final Properties connectionProps = new Properties();
	    connectionProps.put("user", this.connectionDetails.getUser());
	    if (this.connectionDetails.getPassword() != null) {
		connectionProps.put("password",
			this.connectionDetails.getPassword());
	    }
	    connectionProps.put("autoReconnect", String.valueOf(connectionDetails.isAutoReconnect()));
	    connectionProps.put("maxReconnects", connectionDetails.getMaxReconnects());
	    connection = DriverManager.getConnection(
		    this.connectionDetails.getURL(), connectionProps);
	    connection.setAutoCommit(Boolean.FALSE);
	    connection.setTransactionIsolation(this.connectionDetails
		    .getTransactionIsolation());
	    statement = connection.createStatement();
	} catch (Exception ex) {
	    LOGGER.fatal(ex.getMessage(), ex);
	    try {
		if ((connection != null) && !connection.isClosed()) {
		    connection.close();
		}
	    } catch (SQLException se) {
		LOGGER.fatal(se.getMessage(), se);
		throw se;
	    }
	    throw ex;
	}
    }

    public Connection getConnection() {
	return connection;
    }

    public void setConnection(Connection connection) {
	this.connection = connection;
    }

    public Statement getStatement() {
	return statement;
    }

    public void setStatement(Statement statement) {
	this.statement = statement;
    }

}