package org.adm.jdbobjects.sample;

import java.io.File;

import org.adm.jdbobjects.ConnectionDetails;

public class ConnectionDetailsTests {
    public static void main(String[] args) {
	ConnectionDetails con = new ConnectionDetails(new File("D:\\adm\\Repositories\\jDbObjects\\jdbobjects-sample\\src\\main\\resources\\mysql.conf"));
	System.err.println(con.getClassName());
	System.err.println(con.getURL());
    }
}
