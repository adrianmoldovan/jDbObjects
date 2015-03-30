package org.adm.jdbobjects;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

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

}
