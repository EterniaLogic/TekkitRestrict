package com.github.dreadslicer.tekkitrestrict;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class TRLogger {
	private static String LogNameFormat, LogStringFormat;
	private static boolean LogIsEnabled, LogChat, LogChunkRemoval,
			LogLowPerformance, LogAmulets, LogRings, LogGemPowers,
			LogDMTools, LogRMTools, LogEEMisc, LogEEDestructive,
			LogDebug;
	//private static Queue<String[]> LogQueue = new LinkedBlockingQueue<String[]>(),
	//		LogQueue1 = new LinkedBlockingQueue<String[]>();
	private static HashMap<String, ArrayList<String>> logMessages = new HashMap<String, ArrayList<String>>();
	
	public TRLogger() {
		// reload();
	}

	public static void reload() {
		LogNameFormat = tekkitrestrict.config.getString("LogNameFormat");
		LogStringFormat = tekkitrestrict.config.getString("LogStringFormat");
		LogIsEnabled = tekkitrestrict.config.getBoolean("LogIsEnabled");
		LogChat = tekkitrestrict.config.getBoolean("LogChat");
		LogChunkRemoval = tekkitrestrict.config.getBoolean("LogChunkUnload");
		LogLowPerformance = tekkitrestrict.config.getBoolean("LogLowPerformance");
		LogAmulets = tekkitrestrict.config.getBoolean("LogAmulets");
		LogRings = tekkitrestrict.config.getBoolean("LogRings");
		LogGemPowers = tekkitrestrict.config.getBoolean("LogGemPowers");

		LogDMTools = tekkitrestrict.config.getBoolean("LogDMTools");
		LogRMTools = tekkitrestrict.config.getBoolean("LogRMTools");
		//LogOpenAlc = tekkitrestrict.config.getBoolean("LogOpenAlc");
		LogEEMisc = tekkitrestrict.config.getBoolean("LogEEMisc");
		LogEEDestructive = tekkitrestrict.config.getBoolean("LogEEDestructive");
		LogDebug = tekkitrestrict.config.getBoolean("LogDebug");
	}

	@SuppressWarnings("deprecation")
	public static void Log(String type, String info) {
		// Player may be null

		// determine if the log is enabled...
		if (LogIsEnabled) {
			//String TLogLocation = "plugins/tekkitrestrict/" + LogLocation;
			// File BDir = new File(LogLocation);
			// BDir.mkdir();
			// tekkitrestrict.log.info("tt - "+type);
			//******************if(type.equals("debug")) tekkitrestrict.log.info("log: "+info);
			if (!isLoggable(type)) return;

			//String esplitter = !TLogLocation.endsWith("/") ? "/" : "";
			// File TDir = new File(TLogLocation+esplitter+type);
			// TDir.mkdir();

			Date date = new Date();

			String LogNameFormatter = LogNameFormat;
			LogNameFormatter = LogNameFormatter.replace("{MONTH}",
					(date.getMonth() + 1) + "");
			LogNameFormatter = LogNameFormatter.replace("{YEAR}",
					(date.getYear() + 1900) + "");
			LogNameFormatter = LogNameFormatter.replace("{DAY}",
					date.getDate() + "");
			LogNameFormatter = LogNameFormatter.replace("{HOUR}",
					date.getHours() + "");
			LogNameFormatter = LogNameFormatter.replace("{DTYPE}", type);

			// File Log = new
			// File(TLogLocation+esplitter+type+"/"+LogNameFormatter);

			String LogStringFormatter = LogStringFormat;
			LogStringFormatter = LogStringFormatter.replace("{MONTH}",
					(date.getMonth() + 1) + "");
			LogStringFormatter = LogStringFormatter.replace("{YEAR}",
					(date.getYear() + 1900) + "");
			LogStringFormatter = LogStringFormatter.replace("{DAY}",
					date.getDate() + "");
			LogStringFormatter = LogStringFormatter.replace("{HOUR}",
					date.getHours() + "");
			LogStringFormatter = LogStringFormatter.replace("{MINUTE}",
					date.getMinutes() + "");
			LogStringFormatter = LogStringFormatter.replace("{SECOND}",
					date.getSeconds() + "");
			LogStringFormatter = LogStringFormatter.replace("{INFO}", info);

			LogStringFormatter = replacecolors(LogStringFormatter);

			//Type, LogLocation/type, Loglocation/type/date.txt, Info\n
			//LogQueue.add(new String[] {type,
			//		TLogLocation + esplitter + type,
			//		TLogLocation + esplitter + type + "/" + LogNameFormatter,
			//		LogStringFormatter + "\n" });

			ArrayList<String> old = logMessages.get(type);
			
			if (old == null){
				ArrayList<String> msgs = new ArrayList<String>();
				msgs.add(LogStringFormatter);
				logMessages.put(type, msgs);
			} else {
				old.add(LogStringFormatter);
				logMessages.put(type, old);
			}
		}
	}

	private static boolean isLoggable(String type) {
		type = type.toLowerCase();
		if (type.equals("chat")) return LogChat;
		if (type.equals("chunk unloader")) return LogChunkRemoval;
		if (type.equals("lowperformance")) return LogLowPerformance;
		if (type.equals("eeamulet")) return LogAmulets;
		if (type.equals("eering")) return LogRings;
		if (type.equals("eegempower")) return LogGemPowers;
		if (type.equals("eedmtool")) return LogDMTools;
		if (type.equals("eermtool")) return LogRMTools;
		//} else if (type.equals("openalc"))
		//	return LogOpenAlc;
		if (type.equals("eemisc")) return LogEEMisc;
		if (type.equals("eedestructive")) return LogEEDestructive;
		if (type.equals("debug")) return LogDebug;

		return false;
	}
	
	public static void saveLogs() {
		
		if (!LogIsEnabled) return;
		for (String current : logMessages.keySet()){
			FileLog filelog = FileLog.getLogOrMake(current, true);
			ArrayList<String> msgs = logMessages.get(current);
			if (msgs == null) continue;
			for (String msg : msgs) filelog.log(msg+"\n");
			msgs.clear();
		}
		logMessages.clear();
			
		/*String TLogLocation = "plugins/tekkitrestrict/" + LogLocation;
		File BDir = new File(TLogLocation);
		BDir.mkdir();
		new File("log/").mkdir();
		File logFile = null;
		BufferedWriter out = null;
		// tekkitrestrict.log.info(LogQueue.size()+"");
		while (LogQueue.size() >= 1) {
			try {
				String[] alog = LogQueue.poll();
				File TDir = new File(alog[1]);
				TDir.mkdir();
				try {
					logFile = new File(alog[2]);
					if (!logFile.exists()) {
						logFile.createNewFile();
					}
					out = new BufferedWriter(new FileWriter(logFile, true));
					out.write(alog[3]);
					out.close();
				} catch (Exception e) {
				}
			} catch (Exception e) {
			}
		}
		while (LogQueue1.size() >= 1) {
			try {
				String[] alog = LogQueue1.poll();
				File TDir = new File(alog[1]);
				TDir.mkdir();
				try {
					logFile = new File(alog[2]);
					if (!logFile.exists()) {
						logFile.createNewFile();
					}
					out = new BufferedWriter(new FileWriter(logFile, true));
					out.write(alog[3]);
					out.close();
				} catch (Exception e) {
				}
			} catch (Exception e) {
			}
		}*/
	}

	/*@SuppressWarnings("deprecation")
	public static void LogConsole(String type, String info) {
		// determine if the log is enabled...
		if (LogIsEnabled) {
			
			
			 * File BDir = new File(LogLocation); BDir.mkdir(); File TDir = new
			 * File(TLogLocation+type); TDir.mkdir();
			 

			Date date = new Date();

			String LogNameFormatter = LogNameFormat;
			LogNameFormatter = LogNameFormatter.replace("{MONTH}",
					(date.getMonth() + 1) + "");
			LogNameFormatter = LogNameFormatter.replace("{YEAR}",
					(date.getYear() + 1900) + "");
			LogNameFormatter = LogNameFormatter.replace("{DAY}", date.getDate()
					+ "");
			LogNameFormatter = LogNameFormatter.replace("{HOUR}",
					date.getHours() + "");
			LogNameFormatter = LogNameFormatter.replace("{DTYPE}", type);

			// File Log = new
			// File(TLogLocation+esplitter+type+"/"+LogNameFormatter);

			String LogStringFormatter = LogStringFormat;
			LogStringFormatter = LogStringFormatter.replace("{MONTH}",
					(date.getMonth() + 1) + "");
			LogStringFormatter = LogStringFormatter.replace("{YEAR}",
					(date.getYear() + 1900) + "");
			LogStringFormatter = LogStringFormatter.replace("{DAY}",
					date.getDate() + "");
			LogStringFormatter = LogStringFormatter.replace("{HOUR}",
					date.getHours() + "");
			LogStringFormatter = LogStringFormatter.replace("{MINUTE}",
					date.getMinutes() + "");
			LogStringFormatter = LogStringFormatter.replace("{SECOND}",
					date.getSeconds() + "");
			LogStringFormatter = LogStringFormatter.replace("{INFO}", info);
			
			LogStringFormatter = replacecolors(LogStringFormatter);
			
			// tekkitrestrict.log.info(LogStringFormatter);
			final String ttype = type;
			final String formattedName = LogNameFormatter;
			final String formatted = LogStringFormatter;
			final String TLogLocation = "log/";

			tekkitrestrict.basfo.execute(new Runnable(){

				@Override
				public void run() {
					LogQueue1.add(new String[] { ttype, TLogLocation + ttype,
							TLogLocation + ttype + "/" + formattedName,
							formatted + "\n" });
				}
				
			});
			
		}
	}*/
	
	private static String replacecolors(String input){
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
}
