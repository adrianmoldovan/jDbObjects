# jDbObjects
Java lightweight library for storing and retrieving java objects in and from relational databases based on JDBC. Currently support only MySQL. It's a work in progress...

# Settings
A configuration file is needed in main/resources folder, named mysql.conf
e.g. file:

      className=com.mysql.jdbc.Driver
      host=jdbc:mysql://127.0.0.1
      port=3306
      user=root
      password=
      database=db1
      transactionIsolation=1
      autoReconnect=true
      maxReconnects=3

# Usage

Define an Entity:

      @DbEntity(name = "user")
      public class User {
      .....
      }

Define a Property:

      @DbField(name = "id", isPrimaryKey = true, type = Long.class)
      private long id;

Save the Entity:

	    User usr = new User();
	    usr.setEmail("john.doe@example.com");
	    usr.setName("John Doe");
	    usr.setCreated(new Date());
	    BasicDAO<User> dao = new BasicDAO<User>(DatabaseConnection.getInstance().getConnection(), User.class);
	    dao.insert(usr);
	    
Read all Entities:

      BasicDAO<User> dao = new BasicDAO<User>(DatabaseConnection.getInstance().getConnection(), User.class);
	    List<User> all = dao.find();
	    for (int i = 0; i < all.size(); i++) {
		      User user = all.get(i);
		      System.err.println(user.getId() + " " + user.getEmail()+ " " + user.getName() + " " + user.getCreated());
	    }
	    
#TODOs
- add connection pool;
- make configurable how settings are loaded;
- add foreign key details and data load;
- port to different database systems: PostgreSQL, MSSQL, Oracle;
- add unit testing;
- update documentation;
