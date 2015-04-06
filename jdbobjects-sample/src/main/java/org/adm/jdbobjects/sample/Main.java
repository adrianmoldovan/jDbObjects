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
package org.adm.jdbobjects.sample;

import java.util.Date;
import java.util.List;

import org.adm.jdbobjects.DatabaseConnection;
import org.adm.jdbobjects.dao.BasicDAO;

public class Main {// TODO to transform in unit tests.
    public static void main(String[] args) {
	try {
	    User usr = new User();
	    usr.setEmail("john.doe@example.com");
	    usr.setName("John Doe");
	    usr.setId(112);
	    usr.setCreated(new Date());
	    BasicDAO<User> dao = new BasicDAO<User>(DatabaseConnection.getInstance().getConnection(), User.class);
	    //dao.insert(usr);
	    System.err.println(usr.getId());
	    
	    List<User> all = dao.find();
	    for (int i = 0; i < all.size(); i++) {
		User user = all.get(i);
		System.err.println(user.getId() + " " + user.getEmail()+ " " + user.getName() + " " + user.getCreated());
	    }
	    
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }
}
