package com.github.dreadslicer.tekkitrestrict;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Queue;

public class TRLogger {
	private static String LogLocation, LogNameFormat, LogStringFormat,
			BroadcastDupeString, BroadcastHackString;
	private static boolean LogIsEnabled, LogChat, LogChunkRemoval,
			LogLowPerformance, LogDupes, LogAmulets, LogRings, LogGemPowers,
			LogDMTools, LogRMTools, LogOpenAlc, LogEEMisc, LogEEDestructive,
			LogDebug, LogFlyHack, LogForcefieldHack, LogSpeedHack;
	private static List<String> BroadcastDupes;
	private static List<String> hacks;

	private static Queue<String[]> LogQueue = new java.util.concurrent.LinkedBlockingQueue<String[]>(),
			LogQueue1 = new java.util.concurrent.LinkedBlockingQueue<String[]>();

	public TRLogger() {
		// reload();
	}

	public static void reload() {
		LogLocation = tekkitrestrict.config.getString("LogLocation");
		LogNameFormat = tekkitrestrict.config.getString("LogNameFormat");
		LogStringFormat = tekkitrestrict.config.getString("LogStringFormat");
		LogIsEnabled = tekkitrestrict.config.getBoolean("LogIsEnabled");
		LogChat = tekkitrestrict.config.getBoolean("LogChat");
		LogChunkRemoval = tekkitrestrict.config.getBoolean("LogChunkUnload");
		LogLowPerformance = tekkitrestrict.config
				.getBoolean("LogLowPerformance");
		LogDupes = tekkitrestrict.config.getBoolean("LogDupes");
		LogAmulets = tekkitrestrict.config.getBoolean("LogAmulets");
		LogRings = tekkitrestrict.config.getBoolean("LogRings");
		LogGemPowers = tekkitrestrict.config.getBoolean("LogGemPowers");

		LogDMTools = tekkitrestrict.config.getBoolean("LogDMTools");
		LogRMTools = tekkitrestrict.config.getBoolean("LogRMTools");
		LogOpenAlc = tekkitrestrict.config.getBoolean("LogOpenAlc");
		LogEEMisc = tekkitrestrict.config.getBoolean("LogEEMisc");
		LogEEDestructive = tekkitrestrict.config.getBoolean("LogEEDestructive");
		LogDebug = tekkitrestrict.config.getBoolean("LogDebug");
		BroadcastDupes = tekkitrestrict.config.getStringList("BroadcastDupes");
		for (int i = 0; i < BroadcastDupes.size(); i++) {
			BroadcastDupes.set(i, BroadcastDupes.get(i).toLowerCase());
		}
		BroadcastDupeString = tekkitrestrict.config
				.getString("BroadcastDupeString");
		hacks = Collections.synchronizedList(tekkitrestrict.config
				.getStringList("BroadcastHacks"));
		BroadcastHackString = tekkitrestrict.config
				.getString("BroadcastHackString");
		LogFlyHack = tekkitrestrict.config.getBoolean("LogFlyHack");
		LogForcefieldHack = tekkitrestrict.config
				.getBoolean("LogForcefieldHack");
		LogSpeedHack = tekkitrestrict.config.getBoolean("LogSpeedHack");
	}

	@SuppressWarnings("deprecation")
	public static void Log(String type, String info) {
		// Player may be null

		// determine if the log is enabled...
		if (LogIsEnabled) {
			String TLogLocation = "plugins/tekkitrestrict/" + LogLocation;
			// File BDir = new File(LogLocation);
			// BDir.mkdir();
			// tekkitrestrict.log.info("tt - "+type);
			//******************if(type.equals("debug")) tekkitrestrict.log.info("log: "+info);
			if (isLoggable(type)) {
				// tekkitrestrict.log.info("tt - "+type);
				String esplitter = !TLogLocation.endsWith("/") ? "/" : "";
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

				// tekkitrestrict.log.info(LogStringFormatter);
				LogQueue.add(new String[] {
						type,
						TLogLocation + esplitter + type,
						TLogLocation + esplitter + type + "/"
								+ LogNameFormatter, LogStringFormatter + "\n" });

			}
		}
	}

	private static boolean isLoggable(String type1) {
		String type = type1.toLowerCase();
		if (type.equals("chat")) {
			return LogChat;
		} else if (type.equals("dupe")) {
			return LogDupes;
		} else if (type.equals("chunk unloader")) {
			return LogChunkRemoval;
		} else if (type.equals("lowperformance")) {
			return LogLowPerformance;
		} else if (type.equals("eeamulet")) {
			return LogAmulets;
		} else if (type.equals("eering")) {
			return LogRings;
		} else if (type.equals("eegempower")) {
			return LogGemPowers;
		} else if (type.equals("eedmtool")) {
			return LogDMTools;
		} else if (type.equals("eermtool")) {
			return LogRMTools;
		} else if (type.equals("openalc")) {
			return LogOpenAlc;
		} else if (type.equals("eemisc")) {
			return LogEEMisc;
		} else if (type.equals("eedestructive")) {
			return LogEEDestructive;
		} else if (type.equals("debug")) {
			return LogDebug;
		} else if (type.equals("flyhack")) {
			return LogFlyHack;
		} else if (type.equals("forcefieldhack")) {
			return LogForcefieldHack;
		} else if (type.equals("speedhack")) {
			return LogSpeedHack;
		}

		return false;
	}

	public static void broadcastDupe(String player, String dupe, String dtype) {
		// OK, lets broadcast this dupe!
		// tekkitrestrict.getInstance().getServer().broadcastMessage("")
		if (BroadcastDupes.contains(dtype.toLowerCase())) {
			String eeer = TRLogger.BroadcastDupeString;
			eeer = eeer.replace("{PLAYER}", player);
			eeer = eeer.replace("{DTYPE}", dupe);
			tekkitrestrict.getInstance().getServer().broadcastMessage(eeer);
		}
	}

	public static void broadcastHack(String player, String hack, String action) {
		if (hacks.contains(hack.toLowerCase())) {
			String eeer = TRLogger.BroadcastHackString;
			eeer = eeer.replace("{PLAYER}", player);
			eeer = eeer.replace("{HTYPE}", hack);
			eeer = eeer.replace("{ACTION}", action);
			tekkitrestrict.getInstance().getServer().broadcastMessage(eeer);
		}
	}

	public static void saveLogs() {
		if (LogIsEnabled) {
			String TLogLocation = "plugins/tekkitrestrict/" + LogLocation;
			File BDir = new File(TLogLocation);
			BDir.mkdir();
			new File("log/").mkdir();
			File Log = null;
			BufferedWriter out = null;
			// tekkitrestrict.log.info(LogQueue.size()+"");
			while (LogQueue.size() >= 1) {
				try {
					String[] alog = LogQueue.poll();
					File TDir = new File(alog[1]);
					TDir.mkdir();
					try {
						Log = new File(alog[2]);
						if (!Log.exists()) {
							Log.createNewFile();
						}
						out = new BufferedWriter(new FileWriter(Log, true));
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
						Log = new File(alog[2]);
						if (!Log.exists()) {
							Log.createNewFile();
						}
						out = new BufferedWriter(new FileWriter(Log, true));
						out.write(alog[3]);
						out.close();
					} catch (Exception e) {
					}
				} catch (Exception e) {
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	public static void LogConsole(String type, String info) {
		// Player may be null
		// determine if the log is enabled...
		if (LogIsEnabled) {
			
			/*
			 * File BDir = new File(LogLocation); BDir.mkdir(); File TDir = new
			 * File(TLogLocation+type); TDir.mkdir();
			 */

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
	}
}
