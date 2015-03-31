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