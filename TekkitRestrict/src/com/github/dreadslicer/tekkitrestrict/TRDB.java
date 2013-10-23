package com.github.dreadslicer.tekkitrestrict;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

import com.github.dreadslicer.tekkitrestrict.Log.Warning;
import com.github.dreadslicer.tekkitrestrict.database.DBException;
import com.github.dreadslicer.tekkitrestrict.database.MySQL;
import com.github.dreadslicer.tekkitrestrict.database.SQLite;
import com.github.dreadslicer.tekkitrestrict.objects.TREnums.ConfigFile;
import com.github.dreadslicer.tekkitrestrict.objects.TREnums.DBType;

public class TRDB {
	private static boolean newdb = false;
	/** @return If opening the database was successful. */
	static boolean loadDB() {
		String type = tekkitrestrict.config.getString(ConfigFile.Database, "DatabaseType", "sqlite").toLowerCase();
		if (type.equals("sqlite")){
			tekkitrestrict.dbtype = DBType.SQLite;
			File dbfile = new File(tekkitrestrict.getInstance().getDataFolder().getPath() + File.separator + "Data.db");
			if (!dbfile.exists()){
				newdb = true;
				tekkitrestrict.log.info("[DB] Creating database file...");
			}
			tekkitrestrict.db = new SQLite("Data", tekkitrestrict.getInstance().getDataFolder().getPath());
			
			return tekkitrestrict.db.open();
		} else if (type.equals("mysql")){
			tekkitrestrict.dbtype = DBType.MySQL;
			String host = tekkitrestrict.config.getString(ConfigFile.Database, "MySQL.Hostname", "localhost");
			String port = tekkitrestrict.config.getString(ConfigFile.Database, "MySQL.Port", "3306");
			String database = tekkitrestrict.config.getString(ConfigFile.Database, "MySQL.Database", "minecraft");
			String user = tekkitrestrict.config.getString(ConfigFile.Database, "MySQL.Username", "root");
			String password = tekkitrestrict.config.getString(ConfigFile.Database, "MySQL.Password", "");
			try {
				tekkitrestrict.db = new MySQL(host, port, database, user, password);
			} catch (DBException ex){
				String msg = "[MySQL] Error: " + ex.getMessage();
				Warning.otherWarnings.add("[SEVERE] "+msg);
				tekkitrestrict.log.severe(msg);
				
				return false;
			}
			
			return tekkitrestrict.db.open();
		} else {
			tekkitrestrict.dbtype = DBType.Unknown;
			String msg = "[DB] You set an unknown/unsupported database type! Supported: SQLite and MySQL.";
			Warning.otherWarnings.add("[SEVERE] "+msg);
			tekkitrestrict.log.severe(msg);
			return false;
		}
		
	}

	static boolean initSQLite() {
		if (!tekkitrestrict.db.isOpen()) {
			if (!tekkitrestrict.db.open()){
				tekkitrestrict.loadWarning("[SQLite] Cannot open the database!");
				tekkitrestrict.dbworking = 20;
				return false;
			}
		}
		
		ResultSet prev = null;

		try {
			double verX = -1d;
			boolean purged = true;
			prev = tekkitrestrict.db.query("SELECT version FROM tr_dbversion");
			if(prev.next()) verX = prev.getDouble("version");
			if(prev.next()) purged = false;
			
			prev.close();
			
			if (verX == tekkitrestrict.dbversion){
				tekkitrestrict.db.query("DROP TABLE IF EXISTS tr_limiter_old");
			}
			
			//Change version to 1.3 if it is lower
			if(verX != -1d && verX < tekkitrestrict.dbversion){
				tekkitrestrict.db.query("DELETE FROM tr_dbversion");//clear table
				tekkitrestrict.db.query("INSERT INTO tr_dbversion (version) VALUES(" + tekkitrestrict.dbversion + ");");//Insert new version
				transferSQLite12To13();//Transfer to version 1.3
			} else if (!purged) {
				tekkitrestrict.db.query("DELETE FROM tr_dbversion");//clear table
				tekkitrestrict.db.query("INSERT INTO tr_dbversion (version) VALUES(" + tekkitrestrict.dbversion + ");");//Insert new version
			}
			
		} catch(Exception ex1){
			if(prev != null)
				try {prev.close();} catch (SQLException ex2) {}
			
			if (newdb) initNewSQLiteDB();
			else transferOldSQLite();
		}
		if (tekkitrestrict.dbworking == 0) return true;
		return false;
	}
	static boolean initMySQL() {
		if (!tekkitrestrict.db.isOpen()) {
			if (!tekkitrestrict.db.open()){
				tekkitrestrict.loadWarning("[MySQL] Cannot open the database connection!");
				tekkitrestrict.dbworking = 20;
				return false;
			}
		}
		
		ResultSet prev = null;

		try {
			double verX = -1d;
			boolean purged = true;
			prev = tekkitrestrict.db.query("SELECT version FROM tr_dbversion");
			if(prev.next()) verX = prev.getDouble("version");
			if(prev.next()) purged = false;
			
			prev.close();
			
			//Change version to 1.3 if it is lower
			if(verX != -1d && verX < tekkitrestrict.dbversion){
				tekkitrestrict.db.query("DELETE FROM tr_dbversion;");//clear table
				tekkitrestrict.db.query("INSERT INTO tr_dbversion (version) VALUES(" + tekkitrestrict.dbversion + ");");//Insert new version
			} else if (!purged) {
				tekkitrestrict.db.query("DELETE FROM tr_dbversion;");//clear table
				tekkitrestrict.db.query("INSERT INTO tr_dbversion (version) VALUES(" + tekkitrestrict.dbversion + ");");//Insert new version
			}
			
		} catch(Exception ex1){
			if(prev != null)
				try {prev.close();} catch (SQLException ex2) {}
			
			initNewMySQLDB();
			
		}
		if (tekkitrestrict.dbworking == 0) return true;
		return false;
	}
	
	private static void initNewSQLiteDB(){
		tekkitrestrict.dbworking = 0;
		tekkitrestrict.log.info("[SQLite] Creating new database...");
		try {
			tekkitrestrict.db.query("CREATE TABLE IF NOT EXISTS tr_dbversion (version NUMERIC);");
			tekkitrestrict.db.query("INSERT OR REPLACE INTO tr_dbversion (version) VALUES("+tekkitrestrict.dbversion+");");
		} catch (Exception ex) {
			tekkitrestrict.loadWarning("[SQLite] Unable to write version to database!");
			for (StackTraceElement cur : ex.getStackTrace()){
				tekkitrestrict.loadWarning("[SQLite] " + cur.toString());
			}
			tekkitrestrict.dbworking += 1;
		}
	
		try {
			tekkitrestrict.db.query("CREATE TABLE IF NOT EXISTS tr_saferegion ( "
					+ "id INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ "name TEXT,"
					+ "mode INT,"
					+ "data TEXT,"
					+ "world TEXT);");
		} catch (Exception ex) {
			tekkitrestrict.loadWarning("[SQLite] Unable to create safezones table!");
			for (StackTraceElement cur : ex.getStackTrace()){
				tekkitrestrict.loadWarning("[SQLite] " + cur.toString());
			}
			
			tekkitrestrict.dbworking += 2;
		}
		
		try {
			tekkitrestrict.db.query("CREATE TABLE IF NOT EXISTS tr_limiter ( "
					+ "player TEXT UNIQUE,"
					+ "blockdata TEXT);");
		} catch (Exception ex) {
			tekkitrestrict.loadWarning("[SQLite] Unable to create limiter table!");
			for (StackTraceElement cur : ex.getStackTrace()){
				tekkitrestrict.loadWarning("[SQLite] " + cur.toString());
			}
			tekkitrestrict.dbworking += 4;
		}
		
		dbFailMsg(tekkitrestrict.dbworking);
		if (tekkitrestrict.dbworking != 0)
			tekkitrestrict.loadWarning("[SQLite] Not all tables could be created!");
		else 
			tekkitrestrict.log.info("[SQLite] Database created successfully!");
	}
	private static void initNewMySQLDB(){
		tekkitrestrict.dbworking = 0;
		tekkitrestrict.log.info("[MySQL] Creating new database...");
		try {
			tekkitrestrict.db.query("CREATE TABLE IF NOT EXISTS tr_dbversion (version NUMERIC(3,2));");
			tekkitrestrict.db.query("INSERT OR REPLACE INTO tr_dbversion VALUES ("+tekkitrestrict.dbversion+");");
		} catch (Exception ex) {
			tekkitrestrict.loadWarning("[MySQL] Unable to write version to database!");
			for (StackTraceElement cur : ex.getStackTrace()){
				tekkitrestrict.loadWarning("[MySQL] " + cur.toString());
			}
			tekkitrestrict.dbworking += 1;
		}
	
		try {
			tekkitrestrict.db.query("CREATE TABLE IF NOT EXISTS tr_saferegion ( "
					+ "id INTEGER PRIMARY KEY AUTO_INCREMENT,"
					+ "name TINYTEXT,"
					+ "mode TINYINT UNSIGNED,"
					+ "data TINYTEXT,"
					+ "world TINYTEXT) CHARACTER SET latin1 COLLATE latin1_swedish_ci;");
		} catch (Exception ex) {
			tekkitrestrict.loadWarning("[MySQL] Unable to create safezones table!");
			for (StackTraceElement cur : ex.getStackTrace()){
				tekkitrestrict.loadWarning("[MySQL] " + cur.toString());
			}
			
			tekkitrestrict.dbworking += 2;
		}
		
		try {
			tekkitrestrict.db.query("CREATE TABLE IF NOT EXISTS tr_limiter ( "
					+ "player VARCHAR(16) UNIQUE,"
					+ "blockdata TEXT) CHARACTER SET latin1 COLLATE latin1_swedish_ci;");
		} catch (Exception ex) {
			tekkitrestrict.loadWarning("[MySQL] Unable to create limiter table!");
			for (StackTraceElement cur : ex.getStackTrace()){
				tekkitrestrict.loadWarning("[MySQL] " + cur.toString());
			}
			tekkitrestrict.dbworking += 4;
		}
		
		dbFailMsg(tekkitrestrict.dbworking);
		if (tekkitrestrict.dbworking != 0)
			tekkitrestrict.loadWarning("[MySQL] Not all tables could be created!");
		else 
			tekkitrestrict.log.info("[MySQL] Database created successfully!");
	}

	/** Transfer the database from PRE-1.00 version format to the new format. */
	private static void transferOldSQLite() {
		tekkitrestrict.dbworking = 0;
		tekkitrestrict.log.info("[SQLite] Transfering old database into the new database format...");
		
		LinkedList<LinkedList<String>> srvals = null, limvals=null;
		
		//tr_saferegion =	id name mode data world
		//tr_limiter = 		id player blockdata
		try {
			srvals = getTableVals("tr_saferegion");
		} catch(SQLException ex){
			tekkitrestrict.loadWarning("[SQLite] Unable to transfer safezones from the old format to the new one!");
		}
		try {
			limvals = getTableVals("tr_limiter");
		} catch(SQLException ex){
			tekkitrestrict.loadWarning("[SQLite] Unable to transfer limits from the old format to the new one!");
		}
		
		//Delete old tables
		try{tekkitrestrict.db.query("DROP TABLE tr_saferegion;");} catch(Exception ex){}
		try{tekkitrestrict.db.query("DROP TABLE tr_limiter;");} catch(Exception ex){}
		
		//################################### VERSION ###################################
		try {
			tekkitrestrict.db.query("CREATE TABLE IF NOT EXISTS tr_dbversion (version NUMERIC);");
			tekkitrestrict.db.query("INSERT OR REPLACE INTO tr_dbversion (version) VALUES("+tekkitrestrict.dbversion+");");
		} catch (Exception ex) {
			tekkitrestrict.loadWarning("[SQLite] Unable to write version to database!");
			for (StackTraceElement cur : ex.getStackTrace()){
				tekkitrestrict.loadWarning("[SQLite] " + cur.toString());
			}
			tekkitrestrict.dbworking += 1;
		}
		//###############################################################################
		
		//################################## SAFEZONES ##################################
		try {
			tekkitrestrict.db.query("CREATE TABLE IF NOT EXISTS tr_saferegion ( "
					+ "id INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ "name TEXT,"
					+ "mode INT,"
					+ "data TEXT,"
					+ "world TEXT); ");
		} catch (Exception ex) {
			tekkitrestrict.loadWarning("[SQLite] Unable to create safezones table!");
			for (StackTraceElement cur : ex.getStackTrace()){
				tekkitrestrict.loadWarning("[SQLite] " + cur.toString());
			}
			
			tekkitrestrict.dbworking += 2;
		}
		
		try {
			//Import safezones
			if(srvals != null){
				for(LinkedList<String> vals:srvals){
					String toadd = "";
					for(String str:vals) toadd+=","+str;
					//toadd = toadd.replace("null", "''");
					if(toadd.startsWith(",")) toadd=toadd.substring(1, toadd.length());
					tekkitrestrict.db.query("INSERT INTO tr_saferegion VALUES("+toadd+");");
				}
				
				tekkitrestrict.log.info("[SQLite] Transferred " + srvals.size() + " safezones.");
			}
		} catch (Exception ex) {
			tekkitrestrict.loadWarning("[SQLite] Unable to write safezones to database!");
			for (StackTraceElement cur : ex.getStackTrace()){
				tekkitrestrict.loadWarning("[SQLite] " + cur.toString());
			}
		}
		//###############################################################################
		
		//################################### LIMITER ###################################
		try {
			tekkitrestrict.db.query("CREATE TABLE IF NOT EXISTS tr_limiter ( "
						+ "player TEXT UNIQUE,"
						+ "blockdata TEXT);");
		} catch (Exception ex) {
			tekkitrestrict.loadWarning("[SQLite] Unable to create limiter table!");
			for (StackTraceElement cur : ex.getStackTrace()){
				tekkitrestrict.loadWarning("[SQLite] " + cur.toString());
			}
			tekkitrestrict.dbworking += 4;
		}
		
		try {
			if(limvals != null){
				for(LinkedList<String> vals:limvals){
					String toadd = "";
					for(String str:vals) toadd+=","+str;
					if(toadd.startsWith(",")) toadd=toadd.substring(1, toadd.length());
					tekkitrestrict.db.query("INSERT INTO tr_limiter VALUES("+toadd+");");
				}
				
				tekkitrestrict.log.info("[SQLite] Transferred "+ limvals.size() + " limits.");
			}
		} catch (Exception ex) {
			tekkitrestrict.loadWarning("[SQLite] Unable to write limits to database!");
			for (StackTraceElement cur : ex.getStackTrace()){
				tekkitrestrict.loadWarning("[SQLite] " + cur.toString());
			}
		}
		if (tekkitrestrict.dbworking == 0) {
			tekkitrestrict.log.info("[SQLite] Transfering into the new database format succeeded!");
		} else {
			dbFailMsg(tekkitrestrict.dbworking);
			tekkitrestrict.loadWarning("[SQLite] Transfering into the new database format failed!");
		}
		
	}
	private static void transferSQLite12To13(){
		tekkitrestrict.log.info("[SQLite] Updating Database to new format...");
		try {
			tekkitrestrict.db.query("ALTER TABLE tr_limiter RENAME TO tr_limiter_old");
			tekkitrestrict.db.query("CREATE TABLE tr_limiter ("
						+ "player TEXT UNIQUE,"
						+ "blockdata TEXT);");
			tekkitrestrict.db.query("INSERT INTO tr_limiter (player, blockdata) SELECT player, blockdata FROM tr_limiter_old ORDER BY player ASC");
			tekkitrestrict.db.query("DROP TABLE IF EXISTS tr_limiter_old");
		} catch (SQLException ex) {
			tekkitrestrict.loadWarning("[SQLite] Error while updating db!");
			for (StackTraceElement st : ex.getStackTrace()){
				tekkitrestrict.loadWarning("[SQLite] " + st.toString());
			}
		}
	}
	
	private static void dbFailMsg(int fail){
		String prefix = (tekkitrestrict.dbtype == DBType.MySQL ? "[MySQL] " : "[SQLite] ");
		if (fail == 1 || fail == 3 || fail == 5)
			tekkitrestrict.loadWarning(prefix+"The database will RESET upon next server startup because the version table couldn't be created!");
		if (fail == 2 || fail == 3 || fail == 6)
			tekkitrestrict.loadWarning(prefix+"Safezones will NOT work properly because the safezones table couldn't be created!");
		if (fail == 4 || fail == 5 || fail == 6)
			tekkitrestrict.loadWarning(prefix+"The limiter will NOT work properly because the limiter table couldn't be created!");
		else if (fail == 7) 
			tekkitrestrict.loadWarning(prefix+"All database actions failed! Safezones and the limiter will NOT be stored!");
	}
	
	private static LinkedList<LinkedList<String>> getTableVals(String table) throws SQLException {
		ResultSet rs = tekkitrestrict.db.query("SELECT * FROM `"+table+"`");
		LinkedList<LinkedList<String>> values = new LinkedList<LinkedList<String>>();
		if (rs == null) return values;
		while(rs.next()) {
			LinkedList<String> row = new LinkedList<String>();
			for (int i=1;i<=20;i++){
				try {
					row.add(rs.getString(i));
				} catch (Exception ex){
					break;
				}
			}
			values.add(row);
		}
		rs.close();
		return values;
	}
	
	public static String antisqlinject(String ins) {
		ins = ins.replaceAll("--", "");
		ins = ins.replaceAll("`", "");
		ins = ins.replaceAll("'", "");
		ins = ins.replaceAll("\"", "");
		return ins;
	}
}
