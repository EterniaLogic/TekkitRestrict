package nl.taico.tekkitrestrict;

import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.command.CommandSender;
import org.bukkit.command.Command;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import nl.taico.tekkitrestrict.TRConfigCache.Global;

public class Log {
	public static Level dupe;
	public static Level hack;
	/**
	 * Creates 2 custom levels and assigns the loggers.
	 */
	public static void init(){
		Log.mcLogger = Logger.getLogger("Minecraft");
		Log.trLogger = tekkitrestrict.getInstance().getLogger();
		new CustomLevel("Notice", 801);
		new CustomLevel("Command", 802);
		dupe = new CustomLevel("TRDupe", 803);
		hack = new CustomLevel("TRHack", 804);
	}
	public static void deinit(){
		mcLogger = null;
	}
	
	public static Logger mcLogger, trLogger;
	public static void Command(@NonNull Command cmd, @NonNull CommandSender sender, @NonNull String allArgs) {
		mcLogger.log(Level.parse("Command"), sender.getName()+": /"+cmd.getName()+" "+allArgs);
	}
	public static void Command(@NonNull String cmd, @NonNull CommandSender sender, @NonNull String allArgs) {
		mcLogger.log(Level.parse("Command"), sender.getName()+": /"+cmd+" "+allArgs);
	}
	
	public static class Load {
		public static void Blocked(final String type, final int count){
			trLogger.info("Blocked " + count + " " + type + (count==1 ? "." : "s."));
		}
		public static void EMC(final int count, final int count2){
			trLogger.info("Set " + count + " EMC value" + ((count==1) ? " (" : "s (")+ count2 + " if you count all data values)");
		}
	}
	public static class Cache {
		public static void Loaded(final String type, final int count){
			trLogger.info("[Cache] Cached " + count + " " + type + ((count==1) ? "." : "s."));
		}
		public static void Warning(final String message){
			trLogger.warning("[Cache] " + message);
		}
	}
	public static class Config {
		@Deprecated
		public static void Warning(final String message){
			trLogger.warning("[Config] " + message);
			Warning.configWarnings.add(message);
		}
		public static void Notice(final String message){
			trLogger.log(Level.parse("Notice"), "[Config] " + message);
		}
		public static void Loaded(final String type, final int count){
			trLogger.info("[Config] Loaded " + count + " " + type + ((count==1) ? "." : "s."));
		}
	}
	
	public static void Debug(@NonNull final String msg){
		if (!Global.debug) return;
		FileLog.getLogOrMake("Debug", true, false).log(msg);
	}
	public static void Dupe(final String message){
		mcLogger.log(Level.parse("TRDupe"), message);
	}
	public static void Hack(final String message){
		mcLogger.log(hack, message);
	}
	public static void Glitch(String type, String playername) {
		mcLogger.log(dupe, playername + " tried to glitch using a " + type + ".");
	}
	/** For each stackTrace element, it will write it to the debug log. */
	public static void debugEx(@NonNull final Exception ex){
		if (!Global.debug) return;
		final FileLog log = FileLog.getLogOrMake("Debug", true, false);
		for (final StackTraceElement element : ex.getStackTrace()) {
			log.log("     " + element.toString());
		}
	}
	/** For each stackTrace element, log to console and to debug log*/
	public static void Exception(@NonNull final Exception ex, final boolean severe){
		if (severe){
			for (final StackTraceElement element : ex.getStackTrace()){
				trLogger.severe(element.toString());
				Debug("[SEVERE]     "+element.toString());
			}
		} else {
			for (final StackTraceElement element : ex.getStackTrace()){
				trLogger.warning(element.toString());
				Debug("[WARNING]     "+element.toString());
			}
		}
		
	}
	
	@NonNull public static String replaceColors(@Nullable final String str){
		if (str == null) return "null";
		char[] b = str.toCharArray();
		for (int i = 0; i < b.length - 1; ++i) {
			if (b[i] == '&' && ("0123456789abcdefklmnor".indexOf(b[i+1]) > -1)) b[i] = 167;
		}
		
		return new String(b);
	}

	public static class Warning {
		public static LinkedList<String> loadWarnings = new LinkedList<String>();
		public static LinkedList<String> configWarnings = new LinkedList<String>();
		public static LinkedList<String> otherWarnings = new LinkedList<String>();
		public static LinkedList<String> dbWarnings = new LinkedList<String>();
		
		public static void config(final String message, final boolean severe){
			if (severe) trLogger.severe("[Config] " + message);
			else trLogger.warning("[Config] " + message);
			configWarnings.add(message);
			Debug("[Config] " + message);
		}
		public static void load(final String message, final boolean severe){
			if (severe) trLogger.severe(message);
			else trLogger.warning(message);
			loadWarnings.add(message);
			Debug(message);
		}
		public static void other(final String message, boolean severe){
			if (severe) trLogger.severe(message);
			else trLogger.warning(message);
			otherWarnings.add(message);
			Debug(message);
		}
		public static void dbAndLoad(final String message, boolean severe) {
			if (severe) trLogger.severe(message);
			else trLogger.warning(message);
			dbWarnings.add(message);
			loadWarnings.add(message);
			Debug(message);
		}
		public static void db(final String message, final boolean severe){
			if (severe) trLogger.severe(message);
			else trLogger.warning(message);
			dbWarnings.add(message);
			Debug(message);
		}
	}

	public static void info(String message){
		trLogger.info(message);
	}
	public static void warning(String message){
		trLogger.warning(message);
	}
	public static void severe(String message){
		trLogger.severe(message);
	}
	public static void log(Level level, String message){
		trLogger.log(level, message);
	}
	public static void log(Level level, String message, Exception ex){
		trLogger.log(level, message, ex);
	}
	public static void fine(String message) {
		trLogger.fine(message);
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
