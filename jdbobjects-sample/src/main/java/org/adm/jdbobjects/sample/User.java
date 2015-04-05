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
    
    @DbField(name= "created", type = Date.class)
    private Date created;

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

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }
}
