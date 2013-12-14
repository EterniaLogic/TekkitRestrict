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
	protected boolean alternate;
	protected boolean consoleLog;
	protected static final String sep = File.separator;
	
	@SuppressWarnings("deprecation")
	public FileLog(@NonNull String type, boolean alternate, boolean consoleLog){
		this.alternate = alternate;
		this.consoleLog = consoleLog;
		this.type = type;
		Date curdate = new Date(System.currentTimeMillis());
		this.day = curdate.getDay();
		
		File log;
		File folder;
		if (!alternate){
			log = new File("."+sep+TRConfigCache.LogFilter.logLocation+sep+type+sep+formatName(type));
			folder = new File("."+sep+TRConfigCache.LogFilter.logLocation+sep+type+sep);
		} else {
			log = new File("plugins"+sep+"tekkitrestrict"+sep+"log"+sep+type+sep+formatName(type));
			folder = new File("plugins"+sep+"tekkitrestrict"+sep+"log"+sep+type+sep);
		}

		if (!folder.exists()) folder.mkdirs();
		
		if (!log.exists()){
			try {
				log.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			out = new BufferedWriter(new FileWriter(log, true));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Logs.put(type, this);
	}
	
	@SuppressWarnings("deprecation")
	public FileLog(@NonNull String type, boolean consoleLog){
		this.alternate = false;
		this.type = type;
		this.consoleLog = consoleLog;
		Date curdate = new Date(System.currentTimeMillis());
		this.day = curdate.getDay();
		
		File log = new File("."+sep+TRConfigCache.LogFilter.logLocation+sep+type+sep+formatName(type));
		File folder = new File("."+sep+TRConfigCache.LogFilter.logLocation+sep+type+sep);
		if (!folder.exists()) folder.mkdirs();
		
		if (!log.exists()){
			try {
				log.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			out = new BufferedWriter(new FileWriter(log, true));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Logs.put(type, this);
	}
	
	@NonNull public static FileLog getLog(@NonNull String type){
		return Logs.get(type);
	}
	
	@NonNull public static FileLog getLogOrMake(@Nullable String type, boolean consoleLog){
		if (type == null) type = "null";
		FileLog tbr = Logs.get(type);
		if (tbr == null) return new FileLog(type, consoleLog);
		return tbr;
	}
	
	@NonNull public static FileLog getLogOrMake(@Nullable String type, boolean alternate, boolean consoleLog){
		if (type == null) type = "null";
		FileLog tbr = Logs.get(type);
		if (tbr == null) return new FileLog(type, alternate, consoleLog);
		return tbr;
	}
	
	public void log(@Nullable String msg){
		try {
			if (consoleLog){
				if (type.equals("Chat"))
					out.write(replacecolors(formatMsg(msg)));
				else
					out.write(replaceshort(formatMsg(msg)));
			} else {
				out.write(Util.replaceColors(formatMsg(msg)));
			}
			out.newLine();
		} catch (IOException ex) {}
		
		counter++;

		if (counter>=10){
			counter = 0;
			try {
				out.flush();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			changeDate();
		}
	}
	
	public boolean close(){
		try {
			out.close();
		} catch (IOException e) {
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
		} catch (IOException e) {
			e.printStackTrace();
			Logs.remove(type);
			return false;
		}
		return true;
	}
	
	public static void closeAll(){
		for (FileLog filelog : Logs.values()){
			if (!filelog.closeNoRemove()){
				Warning.other("Unable to close all logs. Some might not save properly.", false);
			}
		}
		Logs = null;
	}
	
	@SuppressWarnings("deprecation")
	public void changeDate(){
		Date curdate = new Date(System.currentTimeMillis());
		int day = curdate.getDay();
		if (day == this.day) return;
		
		if (!close()){
			Warning.other("Unable to close the old log!", false);
			return;
		}
		this.day = day;
		
		File log;
		File folder;
		if (!alternate){
			log = new File("."+sep+TRConfigCache.LogFilter.logLocation+sep+type+sep+formatName(type));
			folder = new File("."+sep+TRConfigCache.LogFilter.logLocation+sep+type+sep);
		} else {
			log = new File("plugins"+sep+"tekkitrestrict"+sep+"log"+sep+type+sep+formatName(type));
			folder = new File("plugins"+sep+"tekkitrestrict"+sep+"log"+sep+type+sep);
		}
		
		if (!folder.exists()){
			folder.mkdir();
		}
		if (!log.exists()){
			try {
				log.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			out = new BufferedWriter(new FileWriter(log, true));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Logs.put(type, this);
	}
	
	@NonNull protected String replacecolors(@NonNull String input){
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

	@NonNull protected String replaceshort(@NonNull String input){
		return input.replace("\033[m", "");
	}
	
	protected boolean logged = false;
	protected boolean logged2 = false;
	@NonNull protected String formatName(@NonNull String type){
		Date curdate = new Date(System.currentTimeMillis());
		DateFormat formatter = new SimpleDateFormat("dd-MM-yy");
		String data = formatter.format(curdate);
		String date[] = data.split("-");
		
		String name = TRConfigCache.LogFilter.fileFormat;
		if (name == null || name.equals("") || name.contains("*") || name.endsWith(".")){
			if (!logged){
				Warning.other(ChatColor.RED + "The filename format set in the Logging config is invalid!", false);
				logged = true;
			}
			
			name = type + "-" + data + ".log";
		}
		
		return name.replace("{DAY}", date[0])
					.replace("{MONTH}", date[1])
					.replace("{YEAR}", date[2])
					.replace("{TYPE}", type)
					.replace("\\", "")
					.replace("/", "");
	}

	@NonNull protected String formatMsg(@Nullable String msg){
		if (msg == null) msg = "null";
		DateFormat formatter = new SimpleDateFormat("kk:mm:ss");
		String times = formatter.format(new Date(System.currentTimeMillis()));
		
		String format = TRConfigCache.LogFilter.logFormat;
		if (format == null || format.equals("")){
			if (!logged2){
				Warning.other(ChatColor.RED + "The log format set in the Logging config is invalid!", false);
				logged2 = true;
			}
			
			format = new StringBuilder("[").append(times).append("] ").append(msg).toString();
		} else {
			String time[] = times.split(":");
			format = format.replace("{HOUR}", time[0])
							.replace("{MINUTE}", time[1])
							.replace("{SECOND}", time[2])
							.replace("{INFO}", msg);
		}
		return format;
	}
}
