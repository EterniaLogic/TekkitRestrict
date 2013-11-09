package nl.taico.tekkitrestrict.database;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

import com.github.dreadslicer.tekkitrestrict.tekkitrestrict;

@SuppressWarnings("resource")
public class SQLite extends Database {
	public String location;
	public String name;
	private File sqlFile;

	public SQLite(String name, String location) {
		this.connection = null;
		this.name = name;
		this.location = location;
		File folder = new File(this.location);
		if (this.name.contains("/") || this.name.contains("\\") || this.name.endsWith(".db")) {
			throw new DBException("The database name can not contain: /, \\, or .db");
		}
		if (!folder.exists()) folder.mkdir();

		sqlFile = new File(folder.getAbsolutePath() + File.separator + name + ".db");
	}
	
	protected boolean initialize() {
		if (initialized) return working;
		
		initialized = true;
		try {
			Class.forName("org.sqlite.JDBC");
			working = true;
			return true;
		} catch (ClassNotFoundException e) {
			write("Unable to find the SQLite library!", Level.SEVERE);
			return false;
		}
	}

	public boolean open() {
		if (!initialize()) return false;
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:" + sqlFile.getAbsolutePath());
			return true;
		} catch (SQLException e) {
			write("Error when trying to open the database. " + e, Level.SEVERE);
			return false;
		}
	}

	public boolean close() {
		if (connection == null) return true;
		try {
			connection.close();
			return true;
		} catch (SQLException ex) {
			write("Error on Connection close: " + ex, Level.SEVERE);
			return false;
		}
	}

	/** Opens the connection if it is null. */
	public Connection getConnection() {
		if (connection == null) open();
		return connection;
	}

	public boolean isOpen() {
		if (connection == null) return false;
		try {
			return !connection.isClosed();
		} catch (SQLException e) {
			return false;
		}
	}
	
	/**
	 * Executes a query.
	 * @return A resultSet if the query returned one.<br>
	 * If the query was an update, delete or insert query it will return null.<br>
	 * If the query Failed, it will throw an SQLExcpetion.
	 */
	public ResultSet query(String query) throws SQLException {
		try {
			Statement statement = getConnection().createStatement();
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
	 * If the query was an update, delete or insert query it will return null.<br>
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
			PreparedStatement ps = getConnection().prepareStatement(query);
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
		tekkitrestrict.log.log(level, "[SQLite] " + toWrite);
	}
}