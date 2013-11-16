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
	private BufferedWriter out;
	private String type = "";
	private int day = 0;
	private int counter = 0;
	private static HashMap<String, FileLog> Logs = new HashMap<String, FileLog>();
	private boolean alternate;
	private static final String sep = File.separator;
	
	@SuppressWarnings("deprecation")
	public FileLog(@NonNull String type, boolean alternate){
		this.alternate = alternate;
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
	public FileLog(@NonNull String type){
		this.alternate = false;
		this.type = type;
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
	
	public static FileLog getLog(@NonNull String type){
		return Logs.get(type);
	}
	
	public static FileLog getLogOrMake(String type){
		if (type == null) type = "null";
		FileLog tbr = Logs.get(type);
		if (tbr == null) return new FileLog(type);
		return tbr;
	}
	
	public static FileLog getLogOrMake(String type, boolean alternate){
		if (type == null) type = "null";
		FileLog tbr = Logs.get(type);
		if (tbr == null) return new FileLog(type, alternate);
		return tbr;
	}
	
	public void log(String msg){
		try {
			if (type.equals("Chat"))
				out.write(replacecolors(formatMsg(msg)));
			else
				out.write(replaceshort(formatMsg(msg)));
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
				Warning.other("Unable to close all logs. Some might not save properly.");
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
			Warning.other("Unable to close the old log!");
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
	
	private String replacecolors(@NonNull String input){
		input = input.replace("\033[30;22m", "§0");
		input = input.replace("\033[34;22m", "§1");
		input = input.replace("\033[32;22m", "§2");
		input = input.replace("\033[36;22m", "§3");
		input = input.replace("\033[31;22m", "§4");
		input = input.replace("\033[35;22m", "§5");
		input = input.replace("\033[33;22m", "§6");
		input = input.replace("\033[37;22m", "§7");
		input = input.replace("\033[30;1m", "§8");
		input = input.replace("\033[34;1m", "§9");
		input = input.replace("\033[32;1m", "§a");
		input = input.replace("\033[36;1m", "§b");
		input = input.replace("\033[31;1m", "§c");
		input = input.replace("\033[35;1m", "§d");
		input = input.replace("\033[33;1m", "§e");
		input = input.replace("\033[37;1m", "§f");

		/*input = input.replace("\033[0;30;22m", "§0");
		input = input.replace("\033[0;34;22m", "§1");
		input = input.replace("\033[0;32;22m", "§2");
		input = input.replace("\033[0;36;22m", "§3");
		input = input.replace("\033[0;31;22m", "§4");
		input = input.replace("\033[0;35;22m", "§5");
		input = input.replace("\033[0;33;22m", "§6");
		input = input.replace("\033[0;37;22m", "§7");
		input = input.replace("\033[0;30;1m", "§8");
		input = input.replace("\033[0;34;1m", "§9");
		input = input.replace("\033[0;32;1m", "§a");
		input = input.replace("\033[0;36;1m", "§b");
		input = input.replace("\033[0;31;1m", "§c");
		input = input.replace("\033[0;35;1m", "§d");
		input = input.replace("\033[0;33;1m", "§e");
		input = input.replace("\033[0;37;1m", "§f");

		input = input.replace("\033[30m", "§0");
		input = input.replace("\033[32m", "§2");
		input = input.replace("\033[36m", "§3");
		input = input.replace("\033[31m", "§4");
		input = input.replace("\033[35m", "§5");
		input = input.replace("\033[33m", "§6");
		input = input.replace("\033[37m", "§7");
		input = input.replace("\033[30m", "§8");
		input = input.replace("\033[34m", "§9");
		input = input.replace("\033[32m", "§a");
		input = input.replace("\033[36m", "§b");
		input = input.replace("\033[31m", "§c");
		input = input.replace("\033[35m", "§d");
		input = input.replace("\033[33m", "§e");
		input = input.replace("\033[37m", "§f");*/

		input = input.replace("\033[5m", "§k");
		input = input.replace("\033[21m", "§l");
		input = input.replace("\033[9m", "§m");
		input = input.replace("\033[4m", "§n");
		input = input.replace("\033[3m", "§o");
		input = input.replace("\033[0;39m", "§r");

		input = input.replace("\033[0m", "§r");

		input = input.replace("\033[m", "");
		return input;
	}

	private String replaceshort(@NonNull String input){
		return input.replace("\033[m", "");
	}
	
	private boolean logged = false;
	private boolean logged2 = false;
	private String formatName(@NonNull String type){
		Date curdate = new Date(System.currentTimeMillis());
		DateFormat formatter = new SimpleDateFormat("dd-MM-yy");
		String data = formatter.format(curdate);
		String date[] = data.split("-");
		
		String name = TRConfigCache.LogFilter.fileFormat;
		if (name == null || name.equals("") || name.contains("*") || name.endsWith(".")){
			if (!logged){
				Warning.other(ChatColor.RED + "The filename format set in the Logging config is invalid!");
				logged = true;
			}
			
			name = type + "-" + data + ".log";
		}
		
		name = name.replace("{DAY}", date[0]);
		name = name.replace("{MONTH}", date[1]);
		name = name.replace("{YEAR}", date[2]);
		name = name.replace("{TYPE}", type);
		name = name.replace("\\", "").replace("/", "");
		return name;
	}

	private String formatMsg(@Nullable String msg){
		if (msg == null) msg = "null";
		DateFormat formatter = new SimpleDateFormat("kk:mm:ss");
		String times = formatter.format(new Date(System.currentTimeMillis()));
		
		String format = TRConfigCache.LogFilter.logFormat;
		if (format == null || format.equals("")){
			if (!logged2){
				Warning.other(ChatColor.RED + "The log format set in the Logging config is invalid!");
				logged2 = true;
			}
			
			format = new StringBuilder("[").append(times).append("] ").append(msg).toString();
		} else {
			String time[] = times.split(":");
			format = format.replace("{HOUR}", time[0]);
			format = format.replace("{MINUTE}", time[1]);
			format = format.replace("{SECOND}", time[2]);
			format = format.replace("{INFO}", msg);
		}
		return format;
	}
}
