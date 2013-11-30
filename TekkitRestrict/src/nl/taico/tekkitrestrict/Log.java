package nl.taico.tekkitrestrict;

import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.Command;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

public class Log {
	/**
	 * Creates 2 custom levels and assigns the loggers.
	 */
	public static void init(){
		McLogger = Logger.getLogger("Minecraft");
		new CustomLevel("Notice", 801);
		new CustomLevel("Command", 802);
		new CustomLevel("TRDupe", 803);
		new CustomLevel("TRHack", 804);
	}
	public static void deinit(){
		McLogger = null;
	}
	
	public static Logger McLogger;
	public static void Command(@NonNull Command cmd, @NonNull CommandSender sender, @NonNull String allArgs) {
		McLogger.log(Level.parse("Command"), sender.getName()+": /"+cmd.getName()+" "+allArgs);
	}
	public static void Command(@NonNull String cmd, @NonNull CommandSender sender, @NonNull String allArgs) {
		McLogger.log(Level.parse("Command"), sender.getName()+": /"+cmd+" "+allArgs);
	}
	
	public static class Load {
		public static void Blocked(String type, int count){
			String extra = (count==1) ? "" : "s"; 
			tekkitrestrict.log.info("Blocked " + count + " " + type + extra + ".");
		}
		public static void EMC(int count, int count2){
			String extra = (count==1) ? "" : "s";
			tekkitrestrict.log.info("Set " + count + " EMC value" + extra + " (" + count2 + " if you count all data values)");
		}
	}
	public static class Cache {
		public static void Loaded(String type, int count){
			String extra = (count==1) ? "" : "s"; 
			tekkitrestrict.log.info("[Cache] Cached " + count + " " + type + extra + ".");
		}
		public static void Warning(String message){
			tekkitrestrict.log.log(Level.WARNING, "[Cache] " + message);
		}
	}
	public static class Config {
		@Deprecated
		public static void Warning(String message){
			tekkitrestrict.log.warning("[Config] " + message);
			Warning.configWarnings.add(message);
		}
		public static void Notice(String message){
			tekkitrestrict.log.log(Level.parse("Notice"), "[Config] " + message);
		}
		public static void Loaded(String type, int count){
			String extra = (count==1) ? "" : "s"; 
			tekkitrestrict.log.info("[Config] Loaded " + count + " " + type + extra + ".");
		}
	}
	
	public static void Debug(@NonNull String msg){
		TRLogger.Log("debug", msg);
	}
	public static void Dupe(String message){
		McLogger.log(Level.parse("TRDupe"), message);
	}
	public static void Hack(String message){
		McLogger.log(Level.parse("TRHack"), message);
	}
	public static void Glitch(String type, String playername) {
		String message = playername + " tried to glitch using a " + type + ".";
		McLogger.log(Level.parse("TRDupe"), message);
	}
	/** For each stackTrace element, it will write it to the debug log. */
	public static void debugEx(@NonNull Exception ex){
		for (StackTraceElement element : ex.getStackTrace()) {
			TRLogger.Log("debug", "     " + element.toString());
		}
	}
	/** For each stackTrace element, log to console and to debug log*/
	public static void Exception(@NonNull Exception ex, boolean severe){
		if (severe){
			for (StackTraceElement element : ex.getStackTrace()){
				tekkitrestrict.log.severe(element.toString());
				Debug("[SEVERE]     "+element.toString());
			}
		} else {
			for (StackTraceElement element : ex.getStackTrace()){
				tekkitrestrict.log.warning(element.toString());
				Debug("[WARNING]     "+element.toString());
			}
		}
		
	}
	
	@NonNull public static String replaceColors(@Nullable String str){
		if (str == null) return "null";
		String tbr = str;
		tbr = tbr.replace("&0", ChatColor.BLACK + "");
		tbr = tbr.replace("&1", ChatColor.DARK_BLUE + "");
		tbr = tbr.replace("&2", ChatColor.DARK_GREEN + "");
		tbr = tbr.replace("&3", ChatColor.DARK_AQUA + "");
		tbr = tbr.replace("&4", ChatColor.DARK_RED + "");
		tbr = tbr.replace("&5", ChatColor.DARK_PURPLE + "");
		tbr = tbr.replace("&6", ChatColor.GOLD + "");
		tbr = tbr.replace("&7", ChatColor.GRAY + "");
		tbr = tbr.replace("&8", ChatColor.DARK_GRAY + "");
		tbr = tbr.replace("&9", ChatColor.BLUE + "");
		tbr = tbr.replace("&a", ChatColor.GREEN + "");
		tbr = tbr.replace("&b", ChatColor.AQUA + "");
		tbr = tbr.replace("&c", ChatColor.RED + "");
		tbr = tbr.replace("&d", ChatColor.LIGHT_PURPLE + "");
		tbr = tbr.replace("&e", ChatColor.YELLOW + "");
		tbr = tbr.replace("&f", ChatColor.WHITE + "");
		tbr = tbr.replace("&k", ChatColor.MAGIC + "");
		tbr = tbr.replace("&l", ChatColor.BOLD + "");
		tbr = tbr.replace("&m", ChatColor.STRIKETHROUGH + "");
		tbr = tbr.replace("&n", ChatColor.UNDERLINE + "");
		tbr = tbr.replace("&o", ChatColor.ITALIC + "");
		tbr = tbr.replace("&r", ChatColor.RESET + "");
		return tbr;
	}

	public static class Warning {
		public static LinkedList<String> loadWarnings = new LinkedList<String>();
		public static LinkedList<String> configWarnings = new LinkedList<String>();
		public static LinkedList<String> otherWarnings = new LinkedList<String>();
		public static LinkedList<String> dbWarnings = new LinkedList<String>();
		
		public static void config(String message, boolean severe){
			if (severe) tekkitrestrict.log.severe("[Config] " + message);
			else tekkitrestrict.log.warning("[Config] " + message);
			configWarnings.add(message);
			Debug("[Config] " + message);
		}
		public static void load(String message, boolean severe){
			if (severe) tekkitrestrict.log.severe(message);
			else tekkitrestrict.log.warning(message);
			loadWarnings.add(message);
			Debug(message);
		}
		public static void other(String message, boolean severe){
			if (severe) tekkitrestrict.log.severe(message);
			else tekkitrestrict.log.warning(message);
			otherWarnings.add(message);
			Debug(message);
		}
		public static void dbAndLoad(String message, boolean severe) {
			if (severe) tekkitrestrict.log.severe(message);
			else tekkitrestrict.log.warning(message);
			dbWarnings.add(message);
			loadWarnings.add(message);
			Debug(message);
		}
		public static void db(String message, boolean severe){
			if (severe) tekkitrestrict.log.severe(message);
			else tekkitrestrict.log.warning(message);
			dbWarnings.add(message);
			Debug(message);
		}
	}
}

class CustomLevel extends Level {
	private static final long serialVersionUID = 1L;
	CustomLevel(String name, int value, String localName) {
		super(name, value, localName);
	}
	CustomLevel(String name, int value) {
		super(name, value);
	}	
}
