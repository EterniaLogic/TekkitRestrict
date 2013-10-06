package com.github.dreadslicer.tekkitrestrict;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.Command;

import com.github.dreadslicer.tekkitrestrict.TRConfigCache.Hacks;
import com.github.dreadslicer.tekkitrestrict.objects.TREnums.HackType;

public class Log {
	/**
	 * Creates 2 custom levels and assigns the loggers.
	 */
	static void init(){
		McLogger = Logger.getLogger("Minecraft");
		new CustomLevel("Notice", 801);
		new CustomLevel("Command", 802);
		new CustomLevel("TRDupe", 803);
		new CustomLevel("TRHack", 804);
	}
	static void deinit(){
		McLogger = null;
	}
	
	static Logger McLogger;
	public static void Command(Command cmd, CommandSender sender, String allArgs) {
		McLogger.log(Level.parse("Command"), sender.getName()+": /"+cmd.getName()+" "+allArgs);
	}
	public static void Command(String cmd, CommandSender sender, String allArgs) {
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
	
	public static void Debug(String msg){
		TRLogger.Log("debug", msg);
	}
	public static void Dupe(String niceName, String type, String playername){
		String message = TRConfigCache.Dupes.broadcastFormat;
		message = replaceColors(message);
		message = message.replace("{PLAYER}", playername);
		message = message.replace("{TYPE}", niceName);
		McLogger.log(Level.parse("TRDupe"), message);
		if (TRConfigCache.Dupes.broadcast.contains(type)) Bukkit.broadcast("[TRDupe] " + message, "tekkitrestrict.notify.dupe");
	}
	public static void Hack(HackType type, String playername){
		String message = TRConfigCache.Hacks.broadcastFormat;
		message = replaceColors(message);
		message = message.replace("{PLAYER}", playername);
		message = message.replace("{TYPE}", type.toString());
		McLogger.log(Level.parse("TRHack"), message);
		boolean broadcast = false;
		if (type == HackType.fly){
			if (Hacks.flys.broadcast) broadcast = true; 
		} else if (type == HackType.forcefield) {
			if (Hacks.forcefields.broadcast) broadcast = true;
		} else if (type == HackType.speed){
			if (Hacks.speeds.broadcast) broadcast = true;
		}
		
		if (broadcast) Bukkit.broadcast("[TRHack] " + message, "tekkitrestrict.notify.hack");
	}
	public static void Glitch(String type, String playername) {
		String message = playername + " tried to glitch using a " + type + ".";
		McLogger.log(Level.parse("TRDupe"), message);
	}
	/** For each stackTrace element, it will write it to the debug log. */
	public static void debugEx(Exception ex){
		for (StackTraceElement element : ex.getStackTrace()) {
			TRLogger.Log("debug", "     " + element.toString());
		}
	}
	/** For each stackTrace element, log to console */
	public static void Exception(Exception ex, boolean severe){
		if (severe){
			for (StackTraceElement element : ex.getStackTrace())
				tekkitrestrict.log.severe(element.toString());
		} else {
			for (StackTraceElement element : ex.getStackTrace())
				tekkitrestrict.log.warning(element.toString());
		}
	}
	
	public static String replaceColors(String str){
		if (str == null) return "null";
		str = str.replace("&0", ChatColor.BLACK + "");
		str = str.replace("&1", ChatColor.DARK_BLUE + "");
		str = str.replace("&2", ChatColor.DARK_GREEN + "");
		str = str.replace("&3", ChatColor.DARK_AQUA + "");
		str = str.replace("&4", ChatColor.DARK_RED + "");
		str = str.replace("&5", ChatColor.DARK_PURPLE + "");
		str = str.replace("&6", ChatColor.GOLD + "");
		str = str.replace("&7", ChatColor.GRAY + "");
		str = str.replace("&8", ChatColor.DARK_GRAY + "");
		str = str.replace("&9", ChatColor.BLUE + "");
		str = str.replace("&a", ChatColor.GREEN + "");
		str = str.replace("&b", ChatColor.AQUA + "");
		str = str.replace("&c", ChatColor.RED + "");
		str = str.replace("&d", ChatColor.LIGHT_PURPLE + "");
		str = str.replace("&e", ChatColor.YELLOW + "");
		str = str.replace("&f", ChatColor.WHITE + "");
		str = str.replace("&k", ChatColor.MAGIC + "");
		str = str.replace("&l", ChatColor.BOLD + "");
		str = str.replace("&m", ChatColor.STRIKETHROUGH + "");
		str = str.replace("&n", ChatColor.UNDERLINE + "");
		str = str.replace("&o", ChatColor.ITALIC + "");
		str = str.replace("&r", ChatColor.RESET + "");
		return str;
	}

	public static class Warning {
		public static void config(String message){
			tekkitrestrict.log.warning("[Config] " + message);
			configWarnings.add(message);
		}
		public static void load(String message){
			tekkitrestrict.log.warning(message);
			loadWarnings.add(message);
		}
		public static void other(String message){
			tekkitrestrict.log.warning(message);
			otherWarnings.add(message);
		}
		
		public static ArrayList<String> loadWarnings = new ArrayList<String>();
		public static ArrayList<String> configWarnings = new ArrayList<String>();
		public static ArrayList<String> otherWarnings = new ArrayList<String>();
		public static boolean loadWarnings(){
			return !loadWarnings.isEmpty();
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
