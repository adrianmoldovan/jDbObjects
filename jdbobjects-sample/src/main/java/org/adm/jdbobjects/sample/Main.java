package org.adm.jdbobjects.sample;

import java.util.List;

import org.adm.jdbobjects.DatabaseConnection;
import org.adm.jdbobjects.dao.BasicDAO;

public class Main {// TODO to transform in unit tests.
    public static void main(String[] args) {
	try {
	    User usr = new User();
	    usr.setEmail("john.doe@example.com");
	    usr.setName("John Doe111111111");
	    usr.setId(112);
	    BasicDAO<User> dao = new BasicDAO<User>(DatabaseConnection.getInstance().getConnection(), User.class);
	    dao.update(usr);
	    System.err.println(usr.getId());
	    
	    List<User> all = dao.listAll();
	    for (int i = 0; i < all.size(); i++) {
		User user = all.get(i);
		System.err.println(user.getId() + " " + user.getEmail()+ " " + user.getName());
	    }
	    
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }
}
