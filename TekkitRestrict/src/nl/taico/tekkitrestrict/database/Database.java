package nl.taico.tekkitrestrict.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public abstract class Database {
	protected Connection connection;
	protected boolean working = false, initialized = false;

	public abstract boolean close();
	public abstract Connection getConnection();
	protected abstract boolean initialize();

	public abstract boolean isOpen();
	public abstract boolean open();

	@javax.annotation.Nullable public abstract PreparedStatement prepare(@lombok.NonNull String query);
	@javax.annotation.Nullable public abstract ResultSet query(@lombok.NonNull PreparedStatement ps) throws SQLException;

	@javax.annotation.Nullable public abstract ResultSet query(@lombok.NonNull String query) throws SQLException;
}
