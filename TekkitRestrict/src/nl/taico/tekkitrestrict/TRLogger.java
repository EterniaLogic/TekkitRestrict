package nl.taico.tekkitrestrict;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import lombok.NonNull;

public class TRLogger {
	private static final HashMap<String, ArrayList<String>> logMessages = new HashMap<String, ArrayList<String>>();

	private static String convertTime(String input){
		final Calendar c = new GregorianCalendar();
		final int hour = c.get(Calendar.HOUR_OF_DAY);
		final int min = c.get(Calendar.MINUTE);
		final int sec = c.get(Calendar.SECOND);

		final String h, m, s;
		if (hour < 10) h = "0"+hour;
		else h = ""+hour;

		if (min < 10) m = "0"+min;
		else m = ""+min;

		if (sec < 10) s = "0"+sec;
		else s = ""+sec;

		return s.replace("{HOUR}", h)
				.replace("{MINUTE}", m)
				.replace("{SECOND}", s);
	}

	public static void Log(@NonNull final String type, @NonNull final String info) {
		final String msg = convertTime(TRConfigCache.LogFilter.logFormat).replace("{INFO}", info);

		ArrayList<String> old = logMessages.get(type);

		if (old == null){
			old = new ArrayList<String>();
			old.add(msg);
			logMessages.put(type, old);
		} else {
			old.add(msg);
			logMessages.put(type, old);
		}
	}

	public static void saveLogs() {
		final Iterator<Entry<String, ArrayList<String>>> entries = logMessages.entrySet().iterator();
		while (entries.hasNext()){
			final Entry<String, ArrayList<String>> e = entries.next();
			final TRFileLog filelog = TRFileLog.getLogOrMake(e.getKey(), false);
			final ArrayList<String> msgs = e.getValue();
			if (msgs == null) continue;
			for (final String msg : msgs) filelog.log(msg);
			msgs.clear();
		}
		/*
		for (String current : logMessages.keySet()){
			FileLog filelog = TRFileLog.getLogOrMake(current, true, false);
			ArrayList<String> msgs = logMessages.get(current);
			if (msgs == null) continue;
			for (String msg : msgs) filelog.log(msg+"\n");
			msgs.clear();
		}*/
	}
}
