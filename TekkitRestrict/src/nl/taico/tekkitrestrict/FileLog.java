package nl.taico.tekkitrestrict;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import nl.taico.tekkitrestrict.Log.Warning;

public class FileLog {
	protected BufferedWriter out;
	protected String type = "";
	protected int day = 0;
	protected int counter = 0;
	protected static HashMap<String, FileLog> Logs = new HashMap<String, FileLog>();
	protected final boolean alternate;
	protected final boolean consoleLog;
	protected static final String sep = File.separator;
	protected static final String base = "plugins"+sep+"tekkitrestrict"+sep+"log"+sep;
	
	@SuppressWarnings("deprecation")
	public FileLog(@NonNull final String type, final boolean alternate, final boolean consoleLog){
		this.alternate = alternate;
		this.consoleLog = consoleLog;
		this.type = type;
		final Date curdate = new Date(System.currentTimeMillis());
		this.day = curdate.getDay();
		
		final File log;
		final File folder;
		if (!alternate){
			log = new File("."+sep+TRConfigCache.LogFilter.logLocation+sep+type+sep+formatName(type));
			folder = new File("."+sep+TRConfigCache.LogFilter.logLocation+sep+type+sep);
		} else {
			log = new File(base+type+sep+formatName(type));
			folder = new File(base+type+sep);
		}

		if (!folder.exists()) folder.mkdirs();
		
		if (!log.exists()){
			try {
				log.createNewFile();
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}
		try {
			out = new BufferedWriter(new FileWriter(log, true));
		} catch (final IOException e) {
			e.printStackTrace();
		}
		
		Logs.put(type, this);
	}
	
	@SuppressWarnings("deprecation")
	public FileLog(@NonNull final String type, final boolean consoleLog){
		this.alternate = false;
		this.type = type;
		this.consoleLog = consoleLog;
		final Date curdate = new Date(System.currentTimeMillis());
		this.day = curdate.getDay();
		
		final File log = new File("."+sep+TRConfigCache.LogFilter.logLocation+sep+type+sep+formatName(type));
		final File folder = new File("."+sep+TRConfigCache.LogFilter.logLocation+sep+type+sep);
		if (!folder.exists()) folder.mkdirs();
		
		if (!log.exists()){
			try {
				log.createNewFile();
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}
		try {
			out = new BufferedWriter(new FileWriter(log, true));
		} catch (final IOException e) {
			e.printStackTrace();
		}
		
		Logs.put(type, this);
	}
	
	@NonNull public static FileLog getLog(@NonNull final String type){
		return Logs.get(type);
	}
	
	@NonNull public static FileLog getLogOrMake(@Nullable String type, final boolean consoleLog){
		if (type == null) type = "null";
		final FileLog tbr = Logs.get(type);
		return tbr == null ? new FileLog(type, consoleLog) : tbr;
	}
	
	@NonNull public static FileLog getLogOrMake(@Nullable String type, final boolean alternate, final boolean consoleLog){
		if (type == null) type = "null";
		final FileLog tbr = Logs.get(type);
		return tbr == null ? new FileLog(type, alternate, consoleLog) : tbr;
	}
	
	public void log(@Nullable final String msg, final long time){
		try {
			if (consoleLog){
				if (type.equals("Chat"))
					out.write(replacecolors(formatMsg(msg, time)));
				else
					out.write(replaceshort(formatMsg(msg, time)));
			} else {
				out.write(Util.replaceColors(formatMsg(msg, time)));
			}
			out.newLine();
		} catch (final IOException ex) {}
		
		counter++;

		if (counter>=10){
			counter = 0;
			try {
				out.flush();
			} catch (final IOException ex) {
				ex.printStackTrace();
			}
			changeDate();
		}
	}
	
	public void log(@Nullable final String msg){
		log(msg, System.currentTimeMillis());
	}
	
	public boolean close(){
		try {
			out.close();
		} catch (final IOException e) {
			e.printStackTrace();
			Logs.remove(type);
			return false;
		}
		Logs.remove(type);
		return true;
	}
	
	private boolean closeNoRemove(){
		try {
			out.close();
		} catch (final IOException e) {
			e.printStackTrace();
			Logs.remove(type);
			return false;
		}
		return true;
	}
	
	public static void closeAll(){
		for (final FileLog filelog : Logs.values()){
			if (!filelog.closeNoRemove()){
				Warning.other("Unable to close all logs. Some might not save properly.", false);
			}
		}
		Logs = null;
	}
	
	@SuppressWarnings("deprecation")
	public void changeDate(){
		final Date curdate = new Date(System.currentTimeMillis());
		final int day = curdate.getDay();
		if (day == this.day) return;
		
		if (!close()){
			Warning.other("Unable to close the old log!", false);
			return;
		}
		this.day = day;
		
		final File log;
		final File folder;
		if (!alternate){
			log = new File("."+sep+TRConfigCache.LogFilter.logLocation+sep+type+sep+formatName(type));
			folder = new File("."+sep+TRConfigCache.LogFilter.logLocation+sep+type+sep);
		} else {
			log = new File(base+type+sep+formatName(type));
			folder = new File(base+type+sep);
		}
		
		if (!folder.exists()){
			folder.mkdir();
		}
		if (!log.exists()){
			try {
				log.createNewFile();
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}
		try {
			out = new BufferedWriter(new FileWriter(log, true));
		} catch (final IOException e) {
			e.printStackTrace();
		}
		Logs.put(type, this);
	}
	
	@NonNull protected String replacecolors(@NonNull final String input){
		return input.replace("\033[30;22m", "§0")
					.replace("\033[34;22m", "§1")
					.replace("\033[32;22m", "§2")
					.replace("\033[36;22m", "§3")
					.replace("\033[31;22m", "§4")
					.replace("\033[35;22m", "§5")
					.replace("\033[33;22m", "§6")
					.replace("\033[37;22m", "§7")
					.replace("\033[30;1m", "§8")
					.replace("\033[34;1m", "§9")
					.replace("\033[32;1m", "§a")
					.replace("\033[36;1m", "§b")
					.replace("\033[31;1m", "§c")
					.replace("\033[35;1m", "§d")
					.replace("\033[33;1m", "§e")
					.replace("\033[37;1m", "§f")
			
					/*.replace("\033[0;30;22m", "§0");
					.replace("\033[0;34;22m", "§1");
					.replace("\033[0;32;22m", "§2");
					.replace("\033[0;36;22m", "§3");
					.replace("\033[0;31;22m", "§4");
					.replace("\033[0;35;22m", "§5");
					.replace("\033[0;33;22m", "§6");
					.replace("\033[0;37;22m", "§7");
					.replace("\033[0;30;1m", "§8");
					.replace("\033[0;34;1m", "§9");
					.replace("\033[0;32;1m", "§a");
					.replace("\033[0;36;1m", "§b");
					.replace("\033[0;31;1m", "§c");
					.replace("\033[0;35;1m", "§d");
					.replace("\033[0;33;1m", "§e");
					.replace("\033[0;37;1m", "§f");
			
					.replace("\033[30m", "§0");
					.replace("\033[32m", "§2");
					.replace("\033[36m", "§3");
					.replace("\033[31m", "§4");
					.replace("\033[35m", "§5");
					.replace("\033[33m", "§6");
					.replace("\033[37m", "§7");
					.replace("\033[30m", "§8");
					.replace("\033[34m", "§9");
					.replace("\033[32m", "§a");
					.replace("\033[36m", "§b");
					.replace("\033[31m", "§c");
					.replace("\033[35m", "§d");
					.replace("\033[33m", "§e");
					.replace("\033[37m", "§f");*/
			
					.replace("\033[5m", "§k")
					.replace("\033[21m", "§l")
					.replace("\033[9m", "§m")
					.replace("\033[4m", "§n")
					.replace("\033[3m", "§o")
					.replace("\033[0;39m", "§r")
			
					.replace("\033[0m", "§r")
			
					.replace("\033[m", "");
	}

	@NonNull protected String replaceshort(@NonNull final String input){
		return input.replace("\033[m", "");
	}
	
	protected boolean logged = false;
	protected boolean logged2 = false;
	@NonNull protected String formatName(@NonNull String type){
		final Date curdate = new Date(System.currentTimeMillis());
		final DateFormat formatter = new SimpleDateFormat("dd-MM-yy");
		final String data = formatter.format(curdate);
		final String date[] = data.split("-");
		
		String name = TRConfigCache.LogFilter.fileFormat;
		if (name == null || name.isEmpty() || name.contains("*") || name.endsWith(".")){
			if (!logged){
				Warning.other(ChatColor.RED + "The filename format set in the Logging config is invalid!", false);
				logged = true;
			}
			
			name = type + "-" + data + ".log";
		}
		
		return name.replaceAll("(?i)\\{DAY\\}", date[0])
					.replaceAll("(?i)\\{MONTH\\}", date[1])
					.replaceAll("(?i)\\{YEAR\\}", date[2])
					.replaceAll("(?i)\\{TYPE\\}", type)
					.replace("\\", "")
					.replace("/", "");
	}

	@NonNull protected String formatMsg(@Nullable final String msg){
		return formatMsg(msg, System.currentTimeMillis());
	}
	
	@NonNull protected String formatMsg(@Nullable String msg, final long time){
		if (msg == null) msg = "null";
		final DateFormat formatter = new SimpleDateFormat("kk:mm:ss");
		final String times = formatter.format(new Date(time));
		
		final String format = TRConfigCache.LogFilter.logFormat;
		if (format == null || format.isEmpty()){
			if (!logged2){
				Warning.other(ChatColor.RED + "The log format set in the Logging config is invalid!", false);
				logged2 = true;
			}
			
			return new StringBuilder("[").append(times).append("] ").append(msg).toString();
		} else {
			final String timestr[] = times.split(":");
			return format.replaceAll("(?i)\\{HOUR\\}", timestr[0])
						 .replaceAll("(?i)\\{MINUTE\\}", timestr[1])
						 .replaceAll("(?i)\\{SECOND\\}", timestr[2])
						 .replaceAll("(?i)\\{INFO\\}", msg);
		}
	}
}
