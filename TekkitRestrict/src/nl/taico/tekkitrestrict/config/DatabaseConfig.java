package nl.taico.tekkitrestrict.config;

import java.util.ArrayList;

public class DatabaseConfig extends TRConfig {
	public static ArrayList<String> defaultContents(boolean extra){
		ArrayList<String> tbr = new ArrayList<String>();
		
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
		tbr.add("######################################################################################");
		tbr.add("######################################################################################");
		
		return tbr;
	}
	
	public static void upgradeFile(){
		upgradeFile("Database", convertDefaults(defaultContents(true)));
	}
}
