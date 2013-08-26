package com.github.dreadslicer.tekkitrestrict.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class Database {
	protected Connection connection;
	protected boolean working = false, initialized = false;
	
	protected abstract boolean initialize();
	public abstract boolean open();
	public abstract boolean close();
	
	public abstract Connection getConnection();
	public abstract boolean isOpen();
	
	public abstract ResultSet query(String query) throws SQLException;
	public abstract ResultSet query(PreparedStatement ps) throws SQLException;
	
	public abstract PreparedStatement prepare(String query);
}
