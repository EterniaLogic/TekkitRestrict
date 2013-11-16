package nl.taico.tekkitrestrict.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

public abstract class Database {
	protected Connection connection;
	protected boolean working = false, initialized = false;
	
	protected abstract boolean initialize();
	public abstract boolean open();
	public abstract boolean close();
	
	public abstract Connection getConnection();
	public abstract boolean isOpen();
	
	@Nullable public abstract ResultSet query(@NonNull String query) throws SQLException;
	@Nullable public abstract ResultSet query(@NonNull PreparedStatement ps) throws SQLException;
	
	@Nullable public abstract PreparedStatement prepare(@NonNull String query);
}
