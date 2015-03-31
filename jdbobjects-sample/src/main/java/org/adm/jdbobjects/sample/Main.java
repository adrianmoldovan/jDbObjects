package org.adm.jdbobjects.sample;

import org.adm.jdbobjects.ConnectionDetails;
import org.adm.jdbobjects.DatabaseConnection;
import org.adm.jdbobjects.DatabaseManager;
import org.adm.jdbobjects.SQLConnect;
import org.adm.jdbobjects.dao.BasicDAO;

public class Main {// TODO to transform in unit tests.
    public static void main(String[] args) {
	try {
	    User usr = new User();
	    usr.setEmail("john.doe@example.com");
	    usr.setName("John Doe");
	    DatabaseManager.insert(usr);
	    usr.setId(100);

	    DatabaseManager.insert(usr);
	    DatabaseManager.update(usr);
	    DatabaseManager.delete(usr);
	    
	    DatabaseConnection db = new DatabaseConnection();

	    BasicDAO<User> dao = new BasicDAO<>(DatabaseConnection.getInstance().getConnection());
	    dao.createInsertStatement(usr);
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }
}
