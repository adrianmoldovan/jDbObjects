package org.adm.jdbobjects.sample;

import org.adm.jdbobjects.annotation.DbEntity;
import org.adm.jdbobjects.annotation.DbField;

@DbEntity(name = "user")
public class User {

    @DbField(name = "id", isPrimaryKey = true, type = Long.class)
    private long id;

    @DbField(name = "name", type = String.class)
    private String name;

    @DbField(name = "email", type = String.class)
    private String email;

    public long getId() {
	return id;
    }

    public void setId(long id) {
	this.id = id;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public String getEmail() {
	return email;
    }

    public void setEmail(String email) {
	this.email = email;
    }
}
