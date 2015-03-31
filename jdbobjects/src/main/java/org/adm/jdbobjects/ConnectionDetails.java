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
