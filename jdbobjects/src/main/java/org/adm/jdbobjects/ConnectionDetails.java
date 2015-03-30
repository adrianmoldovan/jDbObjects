package org.adm.jdbobjects;

public class ConnectionDetails {
    private String className;
    private String host;
    private int port;
    private String user;
    private String password;
    private String database;

    public ConnectionDetails() {

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

}
