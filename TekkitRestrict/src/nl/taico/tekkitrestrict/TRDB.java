package nl.taico.tekkitrestrict;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import nl.taico.tekkitrestrict.Log.Warning;
import nl.taico.tekkitrestrict.config.DatabaseConfig;
import nl.taico.tekkitrestrict.database.DBException;
import nl.taico.tekkitrestrict.database.MySQL;
import nl.taico.tekkitrestrict.database.SQLite;
import nl.taico.tekkitrestrict.objects.TRDBSS;
import nl.taico.tekkitrestrict.objects.TREnums.ConfigFile;
import nl.taico.tekkitrestrict.objects.TREnums.DBType;

public class TRDB {
	private static boolean newdb = false;
	/** @return If opening the database was successful. */
	public static boolean loadDB() {
		if (tekkitrestrict.config.getBoolean(ConfigFile.Database, "TransferDBFromSQLiteToMySQL", false)){
			{
				String msg = "[DB] Transferring SQLite database to MySQL...";
				Warning.dbWarnings.add(msg);
				tekkitrestrict.log.info(msg);
			}
			
			tekkitrestrict.dbtype = DBType.SQLite;
			File dbfile = new File(tekkitrestrict.getInstance().getDataFolder().getPath() + File.separator + "Data.db");
			if (!dbfile.exists()){
				tekkitrestrict.dbtype = DBType.Unknown;
				String msg = "[DB] Unable to find SQLite database to transfer data to MySQL!";
				Warning.dbWarnings.add("[SEVERE] "+msg);
				tekkitrestrict.log.severe(msg);
				return false;
			}
			
			tekkitrestrict.db = new SQLite("Data", tekkitrestrict.getInstance().getDataFolder().getPath());
			if (!tekkitrestrict.db.open()){
				tekkitrestrict.dbtype = DBType.Unknown;
				String msg = "[DB] Unable to open SQLite database to transfer data to MySQL!";
				Warning.dbWarnings.add("[SEVERE] "+msg);
				tekkitrestrict.log.severe(msg);
				return false;
			}
			
			if (transferSQLiteToMySQL()){
				String msg = "[DB] Transferred SQLite database to MySQL successfully!";
				Warning.dbWarnings.add(msg);
				tekkitrestrict.log.info(msg);
			} else {
				String msg = "[DB] Transferring SQLite database to MySQL failed!";
				Warning.dbWarnings.add("[SEVERE] "+ msg);
				tekkitrestrict.log.severe(msg);
				return false;
			}
			
			DatabaseConfig.upgradeFile();
			tekkitrestrict.getInstance().reloadConfig();
			
			String type = tekkitrestrict.config.getString(ConfigFile.Database, "DatabaseType", "sqlite").toLowerCase();
			if (!type.equals("mysql")){
				String msg = "[DB] You have transferred to MySQL but you still have SQLite set as your preferred database type! TekkitRestrict will continue to use SQLite until you change it in the config!";
				Warning.dbWarnings.add("[SEVERE] "+ msg);
				tekkitrestrict.log.severe(msg);
			}
		} else if (tekkitrestrict.config.getBoolean(ConfigFile.Database, "TransferDBFromMySQLToSQLite", false)){
			{
				String msg = "[DB] Transferring MySQL database to SQLite...";
				Warning.dbWarnings.add(msg);
				tekkitrestrict.log.info(msg);
			}
			
			tekkitrestrict.dbtype = DBType.MySQL;
			String host = tekkitrestrict.config.getString(ConfigFile.Database, "MySQL.Hostname", "localhost");
			String port = tekkitrestrict.config.getString(ConfigFile.Database, "MySQL.Port", "3306");
			String database = tekkitrestrict.config.getString(ConfigFile.Database, "MySQL.Database", "minecraft");
			String user = tekkitrestrict.config.getString(ConfigFile.Database, "MySQL.Username", "root");
			String password = tekkitrestrict.config.getString(ConfigFile.Database, "MySQL.Password", "");
			try {
				tekkitrestrict.db = new MySQL(host, port, database, user, password);
			} catch (DBException ex){
				tekkitrestrict.dbtype = DBType.Unknown;
				String msg = "[DB] Unable to connect to MySQL database to transfer data to SQLite!";
				Warning.dbWarnings.add("[SEVERE] "+msg);
				tekkitrestrict.log.severe(msg);
				
				return false;
			}
			
			if (!tekkitrestrict.db.open()){
				tekkitrestrict.dbtype = DBType.Unknown;
				String msg = "[DB] Unable to connect to MySQL database to transfer data to SQLite!";
				Warning.dbWarnings.add("[SEVERE] "+msg);
				tekkitrestrict.log.severe(msg);
				return false;
			}
			
			if (transferMySQLToSQLite()){
				String msg = "[DB] Transferred MySQL database to SQLite successfully!";
				Warning.dbWarnings.add(msg);
				tekkitrestrict.log.info(msg);
			} else {
				String msg = "[DB] Transferring MySQL database to SQLite failed!";
				Warning.dbWarnings.add("[SEVERE] "+ msg);
				tekkitrestrict.log.severe(msg);
				return false;
			}
			
			DatabaseConfig.upgradeFile();
			tekkitrestrict.getInstance().reloadConfig();
			
			String type = tekkitrestrict.config.getString(ConfigFile.Database, "DatabaseType", "sqlite").toLowerCase();
			if (!type.equals("sqlite")){
				String msg = "[DB] You have transferred to SQLite but you still have MySQL set as your preferred database type! TekkitRestrict will continue to use MySQL until you change it in the config!";
				Warning.dbWarnings.add("[SEVERE] "+ msg);
				tekkitrestrict.log.severe(msg);
			}
		}
		
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
				Warning.dbWarnings.add("[SEVERE] "+msg);
				tekkitrestrict.log.severe(msg);
				
				return false;
			}
			
			return tekkitrestrict.db.open();
		} else {
			tekkitrestrict.dbtype = DBType.Unknown;
			String msg = "[DB] You set an unknown/unsupported database type! Supported: SQLite and MySQL.";
			Warning.dbWarnings.add("[SEVERE] "+msg);
			tekkitrestrict.log.severe(msg);
			return false;
		}
		
	}

	public static boolean initSQLite() {
		if (!tekkitrestrict.db.isOpen()) {
			if (!tekkitrestrict.db.open()){
				Warning.dbAndLoad("[SQLite] Cannot open the database!");
				tekkitrestrict.dbworking = 20;
				return false;
			}
		}
		
		ResultSet prev = null;

		try {
			double verX = -1d;
			boolean purged = true;
			prev = tekkitrestrict.db.query("SELECT version FROM tr_dbversion");
			if (prev == null) return false;
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
	public static boolean initMySQL() {
		if (!tekkitrestrict.db.isOpen()) {
			if (!tekkitrestrict.db.open()){
				Warning.dbAndLoad("[MySQL] Cannot open the database connection!");
				tekkitrestrict.dbworking = 20;
				return false;
			}
		}
		
		ResultSet prev = null;

		try {
			double verX = -1d;
			boolean purged = true;
			prev = tekkitrestrict.db.query("SELECT version FROM tr_dbversion");
			if (prev == null) return false;
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
			Warning.dbAndLoad("[SQLite] Unable to write version to database!");
			for (StackTraceElement cur : ex.getStackTrace()){
				Warning.dbAndLoad("[SQLite] " + cur.toString());
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
			Warning.dbAndLoad("[SQLite] Unable to create safezones table!");
			for (StackTraceElement cur : ex.getStackTrace()){
				Warning.dbAndLoad("[SQLite] " + cur.toString());
			}
			
			tekkitrestrict.dbworking += 2;
		}
		
		try {
			tekkitrestrict.db.query("CREATE TABLE IF NOT EXISTS tr_limiter ( "
					+ "player TEXT UNIQUE,"
					+ "blockdata TEXT);");
		} catch (Exception ex) {
			Warning.dbAndLoad("[SQLite] Unable to create limiter table!");
			for (StackTraceElement cur : ex.getStackTrace()){
				Warning.dbAndLoad("[SQLite] " + cur.toString());
			}
			tekkitrestrict.dbworking += 4;
		}
		
		dbFailMsg(tekkitrestrict.dbworking);
		if (tekkitrestrict.dbworking != 0)
			Warning.dbAndLoad("[SQLite] Not all tables could be created!");
		else 
			tekkitrestrict.log.info("[SQLite] Database created successfully!");
	}
	private static void initNewMySQLDB(){
		tekkitrestrict.dbworking = 0;
		tekkitrestrict.log.info("[MySQL] Creating new database...");
		try {
			tekkitrestrict.db.query("CREATE TABLE IF NOT EXISTS tr_dbversion (version NUMERIC(3,2));");
			tekkitrestrict.db.query("REPLACE INTO tr_dbversion VALUES ("+tekkitrestrict.dbversion+");");
		} catch (Exception ex) {
			Warning.dbAndLoad("[MySQL] Unable to write version to database!");
			for (StackTraceElement cur : ex.getStackTrace()){
				Warning.dbAndLoad("[MySQL] " + cur.toString());
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
			Warning.dbAndLoad("[MySQL] Unable to create safezones table!");
			for (StackTraceElement cur : ex.getStackTrace()){
				Warning.dbAndLoad("[MySQL] " + cur.toString());
			}
			
			tekkitrestrict.dbworking += 2;
		}
		
		try {
			tekkitrestrict.db.query("CREATE TABLE IF NOT EXISTS tr_limiter ( "
					+ "player VARCHAR(32) UNIQUE,"
					+ "blockdata TEXT) CHARACTER SET latin1 COLLATE latin1_swedish_ci;");
		} catch (Exception ex) {
			Warning.dbAndLoad("[MySQL] Unable to create limiter table!");
			for (StackTraceElement cur : ex.getStackTrace()){
				Warning.dbAndLoad("[MySQL] " + cur.toString());
			}
			tekkitrestrict.dbworking += 4;
		}
		
		dbFailMsg(tekkitrestrict.dbworking);
		if (tekkitrestrict.dbworking != 0)
			Warning.dbAndLoad("[MySQL] Not all tables could be created!");
		else 
			tekkitrestrict.log.info("[MySQL] Database created successfully!");
	}

	/** Transfer the database from PRE-1.00 version format to the new format. */
	private static void transferOldSQLite() {
		tekkitrestrict.dbworking = 0;
		tekkitrestrict.log.info("[SQLite] Transfering old database into the new database format...");
		
		List<LinkedList<String>> srvals = null, limvals=null;
		
		//tr_saferegion =	id name mode data world
		//tr_limiter = 		id player blockdata
		try {
			srvals = getTableVals("tr_saferegion");
		} catch(SQLException ex){
			Warning.dbAndLoad("[SQLite] Unable to transfer safezones from the old format to the new one!");
		}
		try {
			limvals = getTableVals("tr_limiter");
		} catch(SQLException ex){
			Warning.dbAndLoad("[SQLite] Unable to transfer limits from the old format to the new one!");
		}
		
		//Delete old tables
		try{tekkitrestrict.db.query("DROP TABLE tr_saferegion;");} catch(Exception ex){}
		try{tekkitrestrict.db.query("DROP TABLE tr_limiter;");} catch(Exception ex){}
		
		//################################### VERSION ###################################
		try {
			tekkitrestrict.db.query("CREATE TABLE IF NOT EXISTS tr_dbversion (version NUMERIC);");
			tekkitrestrict.db.query("INSERT OR REPLACE INTO tr_dbversion (version) VALUES("+tekkitrestrict.dbversion+");");
		} catch (Exception ex) {
			Warning.dbAndLoad("[SQLite] Unable to write version to database!");
			for (StackTraceElement cur : ex.getStackTrace()){
				Warning.dbAndLoad("[SQLite] " + cur.toString());
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
			Warning.dbAndLoad("[SQLite] Unable to create safezones table!");
			for (StackTraceElement cur : ex.getStackTrace()){
				Warning.dbAndLoad("[SQLite] " + cur.toString());
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
			Warning.dbAndLoad("[SQLite] Unable to write safezones to database!");
			for (StackTraceElement cur : ex.getStackTrace()){
				Warning.dbAndLoad("[SQLite] " + cur.toString());
			}
		}
		//###############################################################################
		
		//################################### LIMITER ###################################
		try {
			tekkitrestrict.db.query("CREATE TABLE IF NOT EXISTS tr_limiter ( "
						+ "player TEXT UNIQUE,"
						+ "blockdata TEXT);");
		} catch (Exception ex) {
			Warning.dbAndLoad("[SQLite] Unable to create limiter table!");
			for (StackTraceElement cur : ex.getStackTrace()){
				Warning.dbAndLoad("[SQLite] " + cur.toString());
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
			Warning.dbAndLoad("[SQLite] Unable to write limits to database!");
			for (StackTraceElement cur : ex.getStackTrace()){
				Warning.dbAndLoad("[SQLite] " + cur.toString());
			}
		}
		if (tekkitrestrict.dbworking == 0) {
			tekkitrestrict.log.info("[SQLite] Transfering into the new database format succeeded!");
		} else {
			dbFailMsg(tekkitrestrict.dbworking);
			Warning.dbAndLoad("[SQLite] Transfering into the new database format failed!");
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
			Warning.dbAndLoad("[SQLite] Error while updating db!");
			for (StackTraceElement st : ex.getStackTrace()){
				Warning.dbAndLoad("[SQLite] " + st.toString());
			}
		}
	}
	
	public static boolean transferMySQLToSQLite(){
		ResultSet rs = null;
		try {
			rs = tekkitrestrict.db.query("SELECT * FROM tr_limiter;");
		} catch (SQLException ex) {
			Warning.dbAndLoad("[MySQL] Unable to read limits from MySql Database! Error: "+ex.getMessage());
			return false;
		}
		
		if (rs == null){
			Warning.dbAndLoad("[MySQL] Unable to read limits from MySql Database! Error: ResultSet is null");
			return false;
		}
		
		int i = 0;
		HashMap<String, String> limits = new HashMap<String, String>();
		try {
			while (rs.next()){
				limits.put(rs.getString(0), rs.getString(1));
				i++;
			}
		} catch (SQLException ex) {
			Warning.dbAndLoad("[MySQL] Unable to read limits from MySql Database! Error: "+ex.getMessage());
			try {
				rs.close();
			} catch (SQLException ex2) {}
			return false;
		}
		
		tekkitrestrict.log.info("[DB] Found "+i+" limiters to transfer.");
		
		try {
			rs.close();
		} catch (SQLException e) {}
		
		HashMap<Integer, TRDBSS> safezones = new HashMap<Integer, TRDBSS>();
		try {
			rs = tekkitrestrict.db.query("SELECT * FROM tr_saferegion;");
		} catch (SQLException ex) {
			Warning.dbAndLoad("[MySQL] Unable to read safezones from MySql Database! Error: "+ex.getMessage());
			return false;
		}
		
		if (rs == null){
			Warning.dbAndLoad("[MySQL] Unable to read safezones from MySql Database! Error: ResultSet is null.");
			return false;
		}
		int j = 0;
		try {
			while (rs.next()){
				safezones.put(rs.getInt("id"), new TRDBSS(rs.getString("name"), rs.getInt("mode"), rs.getString("data"), rs.getString("world")));
				j++;
			}
		} catch (SQLException ex) {
			Warning.dbAndLoad("[MySQL] Unable to read safezones from MySql Database! Error: "+ex.getMessage());
			try {
				rs.close();
			} catch (SQLException ex2) {}
			return false;
		}
		
		try {
			rs.close();
		} catch (SQLException e) {}
		
		tekkitrestrict.log.info("[DB] Found "+j+" SafeZones to transfer.");
		
		tekkitrestrict.dbtype = DBType.SQLite;
		String path = tekkitrestrict.getInstance().getDataFolder().getPath();
		File dbfile = new File(path + File.separator + "Data.db");
		File backup = new File(path + File.separator + "Data_backup.db");
		if (dbfile.exists()){
			if (!dbfile.renameTo(backup)){
				backup.delete();
				dbfile = new File(path + File.separator + "Data.db");
				if (!dbfile.renameTo(backup)){
					Warning.dbAndLoad("[DB] Unable to delete/backup old database file!");
					return false;
				}
			}
			dbfile = new File(path + File.separator + "Data.db");
		}
		tekkitrestrict.log.info("[DB] Creating database file...");
		try {
			tekkitrestrict.db = new SQLite("Data", path);
		} catch (DBException ex){
			Warning.dbAndLoad("[DB] Unable to create database file!");
			return false;
		}
		
		if (!tekkitrestrict.db.open()){
			Log.Warning.dbAndLoad("[SQLite] Unable to open Database!");
			return false;
		}
		
		initNewSQLiteDB();
		if (tekkitrestrict.dbworking != 0) return false;
		
		int k = i;
		Iterator<Entry<String, String>> limitsit = limits.entrySet().iterator();
		while (limitsit.hasNext()){
			Entry<String, String> entry = limitsit.next();
			try {
				tekkitrestrict.db.query("INSERT INTO tr_limiter (player, blockdata) VALUES (\""+entry.getKey()+"\", \""+entry.getValue()+"\");");
			} catch (SQLException ex) {
				Log.Warning.dbAndLoad("[SQLite] Unable to transfer limits of player " + entry.getKey() + "! Error: "+ex.getMessage());
				k--;
				continue;
			}
		}
		
		tekkitrestrict.log.info("[DB] Transferred "+k+" out of "+i+" limiters successfully.");
		
		int l = j;
		Iterator<Entry<Integer, TRDBSS>> ssit = safezones.entrySet().iterator();
		while (ssit.hasNext()){
			Entry<Integer, TRDBSS> entry = ssit.next();
			TRDBSS ss = entry.getValue();
			try {
				tekkitrestrict.db.query("INSERT INTO tr_saferegion (id, name, mode, data, world) VALUES ("+entry.getKey()+", \""+ss.name+"\", "+ss.mode+", \""+ss.data+"\", \""+ss.world+"\");");
			} catch (SQLException ex) {
				Log.Warning.dbAndLoad("[SQLite] Unable to transfer safezone " + ss.name + "! Error: "+ex.getMessage());
				l--;
				continue;
			}
		}
		
		tekkitrestrict.log.info("[DB] Transferred "+l+" out of "+j+" safezones successfully.");
		
		return true;
	}
	
	public static boolean transferSQLiteToMySQL(){
		ResultSet rs = null;
		try {
			rs = tekkitrestrict.db.query("SELECT * FROM tr_limiter;");
		} catch (SQLException ex) {
			Warning.dbAndLoad("[SQLite] Unable to read limits from SQLite Database! Error: "+ex.getMessage());
			return false;
		}
		
		if (rs == null){
			Warning.dbAndLoad("[SQLite] Unable to read limits from SQLite Database! Error: ResultSet is null");
			return false;
		}
		
		int i = 0;
		HashMap<String, String> limits = new HashMap<String, String>();
		try {
			while (rs.next()){
				limits.put(rs.getString(0), rs.getString(1));
				i++;
			}
		} catch (SQLException ex) {
			Warning.dbAndLoad("[SQLite] Unable to read limits from SQLite Database! Error: "+ex.getMessage());
			try {
				rs.close();
			} catch (SQLException ex2) {}
			return false;
		}
		
		tekkitrestrict.log.info("[DB] Found "+i+" limiters to transfer.");
		
		try {
			rs.close();
		} catch (SQLException e) {}
		
		HashMap<Integer, TRDBSS> safezones = new HashMap<Integer, TRDBSS>();
		try {
			rs = tekkitrestrict.db.query("SELECT * FROM tr_saferegion;");
		} catch (SQLException ex) {
			Warning.dbAndLoad("[SQLite] Unable to read safezones from SQLite Database! Error: "+ex.getMessage());
			return false;
		}
		
		if (rs == null){
			Warning.dbAndLoad("[SQLite] Unable to read safezones from SQLite Database! Error: ResultSet is null.");
			return false;
		}
		int j = 0;
		try {
			while (rs.next()){
				safezones.put(rs.getInt("id"), new TRDBSS(rs.getString("name"), rs.getInt("mode"), rs.getString("data"), rs.getString("world")));
				j++;
			}
		} catch (SQLException ex) {
			Warning.dbAndLoad("[SQLite] Unable to read safezones from SQLite Database! Error: "+ex.getMessage());
			try {
				rs.close();
			} catch (SQLException ex2) {}
			return false;
		}
		
		try {
			rs.close();
		} catch (SQLException e) {}
		
		tekkitrestrict.log.info("[DB] Found "+j+" SafeZones to transfer.");
		
		tekkitrestrict.dbtype = DBType.MySQL;
		String host = tekkitrestrict.config.getString(ConfigFile.Database, "MySQL.Hostname", "localhost");
		String port = tekkitrestrict.config.getString(ConfigFile.Database, "MySQL.Port", "3306");
		String database = tekkitrestrict.config.getString(ConfigFile.Database, "MySQL.Database", "minecraft");
		String user = tekkitrestrict.config.getString(ConfigFile.Database, "MySQL.Username", "root");
		String password = tekkitrestrict.config.getString(ConfigFile.Database, "MySQL.Password", "");
		try {
			tekkitrestrict.db = new MySQL(host, port, database, user, password);
		} catch (DBException ex){
			String msg = "[MySQL] Unable to connect to MySQL database! Error: " + ex.getMessage();
			Warning.dbWarnings.add("[SEVERE] "+msg);
			tekkitrestrict.log.severe(msg);
			
			return false;
		}
		
		if (!tekkitrestrict.db.open()){
			Log.Warning.dbAndLoad("[MySQL] Unable to connect to Database!");
			return false;
		}
		
		initNewMySQLDB();
		if (tekkitrestrict.dbworking != 0) return false;
		
		int k = i;
		Iterator<Entry<String, String>> limitsit = limits.entrySet().iterator();
		while (limitsit.hasNext()){
			Entry<String, String> entry = limitsit.next();
			try {
				tekkitrestrict.db.query("INSERT INTO tr_limiter (player, blockdata) VALUES (\""+entry.getKey()+"\", \""+entry.getValue()+"\");");
			} catch (SQLException ex) {
				Log.Warning.dbAndLoad("[MySQL] Unable to transfer limits of player " + entry.getKey() + "! Error: "+ex.getMessage());
				k--;
				continue;
			}
		}
		
		tekkitrestrict.log.info("[DB] Transferred "+k+" out of "+i+" limiters successfully.");
		
		int l = j;
		Iterator<Entry<Integer, TRDBSS>> ssit = safezones.entrySet().iterator();
		while (ssit.hasNext()){
			Entry<Integer, TRDBSS> entry = ssit.next();
			TRDBSS ss = entry.getValue();
			try {
				tekkitrestrict.db.query("INSERT INTO tr_saferegion (id, name, mode, data, world) VALUES ("+entry.getKey()+", \""+ss.name+"\", "+ss.mode+", \""+ss.data+"\", \""+ss.world+"\");");
			} catch (SQLException ex) {
				Log.Warning.dbAndLoad("[MySQL] Unable to transfer safezone " + ss.name + "! Error: "+ex.getMessage());
				l--;
				continue;
			}
		}
		
		tekkitrestrict.log.info("[DB] Transferred "+l+" out of "+j+" safezones successfully.");
		
		return true;
	}
	
	private static void dbFailMsg(int fail){
		String prefix = (tekkitrestrict.dbtype == DBType.MySQL ? "[MySQL] " : "[SQLite] ");
		if (fail == 1 || fail == 3 || fail == 5)
			Warning.dbAndLoad(prefix+"The database will RESET upon next server startup because the version table couldn't be created!");
		if (fail == 2 || fail == 3 || fail == 6)
			Warning.dbAndLoad(prefix+"Safezones will NOT work properly because the safezones table couldn't be created!");
		if (fail == 4 || fail == 5 || fail == 6)
			Warning.dbAndLoad(prefix+"The limiter will NOT work properly because the limiter table couldn't be created!");
		else if (fail == 7) 
			Warning.dbAndLoad(prefix+"All database actions failed! Safezones and the limiter will NOT be stored!");
	}
	
	private static List<LinkedList<String>> getTableVals(String table) throws SQLException {
		ResultSet rs = tekkitrestrict.db.query("SELECT * FROM `"+table+"`");
		List<LinkedList<String>> values = new LinkedList<LinkedList<String>>();
		if (rs == null) return values;
		while(rs.next()) {
			LinkedList<String> row = new LinkedList<String>();
			for (int i=1;i<=10;i++){
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
