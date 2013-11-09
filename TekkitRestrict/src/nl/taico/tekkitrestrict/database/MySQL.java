/**
 * MySQL
 * Inherited subclass for making a connection to a MySQL server.
 * 
 * Date Created: 2011-08-26 19:08
 * @author PatPeter
 */
package nl.taico.tekkitrestrict.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

import nl.taico.tekkitrestrict.tekkitrestrict;

@SuppressWarnings("resource")
public class MySQL extends Database {
	private String hostname = "localhost";
	private String port = "3306";
	private String username = "minecraft";
	private String password = "";
	private String database = "minecraft";

	public MySQL(String hostname, String port, String database, String username, String password) {
		this.connection = null;
		this.hostname = hostname;
		if (!port.matches("\\d+")){
			throw new DBException("You did not set a valid port! Only numbers are valid.");
		}
		this.port = port;
		this.database = database;
		this.username = username;
		this.password = password;
	}

	protected boolean initialize() {
		if (initialized) return working;
		
		initialized = true;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			working = true;
			return true;
		} catch (ClassNotFoundException e) {
			write("The MySQL driver class is missing: " + e.getMessage() + ".", Level.SEVERE);
			return false;
		}
	}

	public boolean open() {
		if (!initialize()) return false;
		String url = "";
		try {
			url = "jdbc:mysql://" + hostname + ":" + port + "/" + database;
			connection = DriverManager.getConnection(url, username, password);
			return true;
		} catch (SQLException e) {
			write("Could not connect to database at \""+url+"\". Error: " + e.getMessage() + ".", Level.SEVERE);
			return false;
		}
	}

	/**
	 * Tries to close the database connection and returns its result.<br>
	 * If the connection is already closed this is a no-op and will return true.<br>
	 * Returns true if the connection is null.
	 */
	public boolean close() {
		if (connection == null) return true;
		try {
			connection.close();
			return true;
		} catch (Exception ex) {
			write("Unable to close database connection. Error: " + ex.getMessage(), Level.SEVERE);
			return false;
		}
	}

	public Connection getConnection() {
		return connection;
	}

	/**
	 * Checks if the connection is open (valid).
	 * @return If the connection is closed or null will return false. Otherwise will return true.
	 */
	public boolean isOpen() {
		if (connection == null) return false;
		
		try {
			return connection.isValid(1);
		} catch (SQLException e) {
			return false;
		}
	}

	/**
	 * Sends the given query to the database.
	 * @return A resultSet if the query returned one.<br>
	 * If the query was an update, delete or insert type query it will return null.<br>
	 * If the query Failed, it will throw an SQLExcpetion. 
	 */
	public ResultSet query(String query) throws SQLException {
		Statement statement = null;
		try {
			statement = connection.createStatement();
			if (statement.execute(query))
				return statement.getResultSet();
			else
				return null;
		} catch (SQLException ex) {
			write("Error when trying to execute query! Error: " + ex.getMessage(), Level.WARNING);
			throw ex;
		}
	}
	
	/**
	 * Executes a query in a PreparedStatement.
	 * @return A resultSet if the query returned one.<br>
	 * If the query was an update, delete or insert type query it will return null.<br>
	 * If the query Failed, it will throw an SQLExcpetion.
	 */
	public ResultSet query(PreparedStatement ps) throws SQLException {
		try {
			if (ps.execute())
				return ps.getResultSet();
			else
				return null;
		} catch (SQLException ex) {
			write("Error when trying to execute query! Error: " + ex.getMessage(), Level.WARNING);
			throw ex;
		}
	}

	public PreparedStatement prepare(String query) {
		try {
			PreparedStatement ps = connection.prepareStatement(query);
			return ps;
		} catch (SQLException e) {
			if (!e.toString().contains("not return ResultSet")) {
				write("Error in SQL prepare() query: " + e.getMessage(), Level.WARNING);
			}
		}
		return null;
	}
	
	protected void write(String toWrite, Level level) {
		if (toWrite == null || toWrite.equals("")) return;
		tekkitrestrict.log.log(level, "[MySQL] " + toWrite);
	}
}