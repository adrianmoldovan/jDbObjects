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
import java.io.FileInputStream;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConnectionDetails {

    private static final Logger LOGGER = LogManager.getLogger();
    private String className;
    private String host;
    private int port;
    private String user;
    private String password;
    private String database;
    private int transactionIsolation;
    private boolean autoReconnect;
    private int maxReconnects;

    public ConnectionDetails(File file) {
	try {

	    Properties prop = new Properties();
	    prop.load(new FileInputStream(file));
	    loadProperties(prop);
	} catch (Exception e) {
	    LOGGER.fatal(e.getMessage(), e);
	}
    }

    public ConnectionDetails(Properties properties) {
	loadProperties(properties);
    }

    private void loadProperties(Properties properties) {
	try {
	    className = properties.getProperty("className");
	    host = properties.getProperty("host");
	    port = Integer.parseInt(properties.getProperty("port"));
	    user = properties.getProperty("user");
	    password = properties.getProperty("password");
	    database = properties.getProperty("database");
	    transactionIsolation = Integer.parseInt(properties
		    .getProperty("transactionIsolation"));
	    autoReconnect = Boolean.parseBoolean(properties
		    .getProperty("autoReconnect"));
	    maxReconnects = Integer.parseInt(properties
		    .getProperty("maxReconnects"));
	} catch (Exception e) {
	    LOGGER.fatal(e.getMessage(), e);
	}
    }

    public String getURL() {
	return host + ":" + port + "/" + database;
    }

    public boolean loadDriver() {
	try {
	    Class.forName(className);
	    return true;
	} catch (ClassNotFoundException e) {// TODO to introduce error handling.
	    return false;
	}
    }

    public String getClassName() {
	return className;
    }

    public void setClassName(String className) {
	this.className = className;
    }

    public String getHost() {
	return host;
    }

    public void setHost(String host) {
	this.host = host;
    }

    public int getPort() {
	return port;
    }

    public void setPort(int port) {
	this.port = port;
    }

    public String getUser() {
	return user;
    }

    public void setUser(String user) {
	this.user = user;
    }

    public String getPassword() {
	return password;
    }

    public void setPassword(String password) {
	this.password = password;
    }

    public String getDatabase() {
	return database;
    }

    public void setDatabase(String database) {
	this.database = database;
    }

    public int getTransactionIsolation() {
	return transactionIsolation;
    }

    public void setTransactionIsolation(int transactionIsolation) {
	this.transactionIsolation = transactionIsolation;
    }

    public boolean isAutoReconnect() {
	return autoReconnect;
    }

    public void setAutoReconnect(boolean autoReconnect) {
	this.autoReconnect = autoReconnect;
    }

    public int getMaxReconnects() {
	return maxReconnects;
    }

    public void setMaxReconnects(int maxReconnects) {
	this.maxReconnects = maxReconnects;
    }

}
