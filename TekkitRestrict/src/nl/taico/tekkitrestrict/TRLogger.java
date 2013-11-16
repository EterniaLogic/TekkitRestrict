package nl.taico.tekkitrestrict;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

import org.eclipse.jdt.annotation.NonNull;

import nl.taico.tekkitrestrict.TRConfigCache.Global;
import nl.taico.tekkitrestrict.TRConfigCache.Logger;

public class TRLogger {
	private static HashMap<String, ArrayList<String>> logMessages = new HashMap<String, ArrayList<String>>();

	public static void Log(@NonNull String type, @NonNull String info) {
		if (!isLoggable(type)) return;

		Calendar c = new GregorianCalendar();
		String month = "" + (c.get(Calendar.MONTH) + 1);
		String year = "" + c.get(Calendar.YEAR);
		String day = "" + c.get(Calendar.DATE);
		String hour = "" + c.get(Calendar.HOUR_OF_DAY);
		String minute = "" + c.get(Calendar.MINUTE);
		String second = "" + c.get(Calendar.SECOND);
		
		String LogNameFormatter = TRConfigCache.LogFilter.fileFormat;
		LogNameFormatter = LogNameFormatter.replace("{MONTH}", month);
		LogNameFormatter = LogNameFormatter.replace("{YEAR}", year);
		LogNameFormatter = LogNameFormatter.replace("{DAY}", day);
		LogNameFormatter = LogNameFormatter.replace("{HOUR}", hour);
		LogNameFormatter = LogNameFormatter.replace("{TYPE}", type);

		String LogStringFormatter = TRConfigCache.LogFilter.logFormat;
		LogStringFormatter = LogStringFormatter.replace("{MONTH}", month);
		LogStringFormatter = LogStringFormatter.replace("{YEAR}", year);
		LogStringFormatter = LogStringFormatter.replace("{DAY}", day);
		LogStringFormatter = LogStringFormatter.replace("{HOUR}", hour);
		LogStringFormatter = LogStringFormatter.replace("{MINUTE}", minute);
		LogStringFormatter = LogStringFormatter.replace("{SECOND}", second);
		LogStringFormatter = LogStringFormatter.replace("{INFO}", info);

		LogStringFormatter = replacecolors(LogStringFormatter);

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

	private static boolean isLoggable(@NonNull String type) {
		type = type.toLowerCase();
		if (type.equals("eering")) return Logger.LogRings;
		if (type.equals("eedmtool")) return Logger.LogDMTools;
		if (type.equals("eermtool")) return Logger.LogRMTools;
		if (type.equals("eeamulet")) return Logger.LogAmulets;
		if (type.equals("eemisc")) return Logger.LogEEMisc;
		if (type.equals("eedestructive")) return Logger.LogEEDestructive;
		if (type.equals("debug")) return Global.debug;
		if (type.equals("error")) return true;

		return false;
	}
	
	public static void saveLogs() {
		for (String current : logMessages.keySet()){
			FileLog filelog = FileLog.getLogOrMake(current, true);
			ArrayList<String> msgs = logMessages.get(current);
			if (msgs == null) continue;
			for (String msg : msgs) filelog.log(msg+"\n");
			msgs.clear();
		}
		logMessages.clear();
	}
	
	@NonNull private static String replacecolors(@NonNull String input){
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
