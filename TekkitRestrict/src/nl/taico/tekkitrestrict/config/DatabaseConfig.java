package nl.taico.tekkitrestrict.config;

import java.util.ArrayList;

public class DatabaseConfig extends TRConfig {
	public static ArrayList<String> defaultContents(boolean extra){
		ArrayList<String> tbr = new ArrayList<String>(50);
		
		tbr.add("######################################################################################");
		tbr.add("## Configuration file for TekkitRestrict                                            ##");
		tbr.add("## Authors: Taeir, DreadEnd (aka DreadSlicer)                                       ##");
		tbr.add("## BukkitDev: http://dev.bukkit.org/server-mods/tekkit-restrict/                    ##");
		tbr.add("## Please ask questions/report issues on the BukkitDev page.                        ##");
		tbr.add("######################################################################################");
		tbr.add("");
		tbr.add("######################################################################################");
		tbr.add("############################### Database Configuration ###############################");
		tbr.add("######################################################################################");
		tbr.add("");
		tbr.add("# DatabaseType");
		tbr.add("#");
		tbr.add("# Set the type of database tekkitrestrict should use.");
		tbr.add("# Possible: SQLite, MySQL");
		tbr.add("# Default: SQLite");
		tbr.add("DatabaseType: SQLite");
		if (extra) tbr.add("#:-;-:# DatabaseType");
		tbr.add("");
		tbr.add("MySQL:");
		tbr.add("    Hostname: localhost");
		if (extra) tbr.add("#:-;-:# MySQL.Hostname");
		tbr.add("    Port: 3306");
		if (extra) tbr.add("#:-;-:# MySQL.Port");
		tbr.add("    Username: root");
		if (extra) tbr.add("#:-;-:# MySQL.Username");
		tbr.add("    Password: minecraft");
		if (extra) tbr.add("#:-;-:# MySQL.Password");
		tbr.add("    Database: minecraft");
		if (extra) tbr.add("#:-;-:# MySQL.Database");
		tbr.add("");
		tbr.add("######################################################################################");
		tbr.add("################################# Transfer settings ##################################");
		tbr.add("######################################################################################");
		tbr.add("# Here you can set if you want to transfer a database from SQLite to MySQL or vice");
		tbr.add("# versa. Only one of these options can be true.");
		tbr.add("");
		tbr.add("# Transfer from SQLite to MySQL");
		tbr.add("# If you set this to true, the data currently in the Data.db file will be written to");
		tbr.add("# the MySQL database as set above.");
		tbr.add("TransferDBFromSQLiteToMySQL: false");
		tbr.add("");
		tbr.add("# Transfer from MySQL to SQLite");
		tbr.add("# If you set this to true, the data currently in the MySQL database as set above will");
		tbr.add("# be written into a SQLite database file named Data.db.");
		tbr.add("TransferDBFromMySQLToSQLite: false");
		tbr.add("");
		tbr.add("######################################################################################");
		tbr.add("######################################################################################");
		tbr.add("######################################################################################");
		
		return tbr;
	}
	
	public static void upgradeFile(){
		upgradeFile("Database", convertDefaults(defaultContents(true)));
	}
}
