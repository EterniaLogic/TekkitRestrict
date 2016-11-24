package nl.taico.tekkitrestrict;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import javax.annotation.Nullable;

import lombok.NonNull;
import nl.taico.tekkitrestrict.Log.Warning;

import org.bukkit.ChatColor;

public class FileLog {
	public static void closeAll(){
		for (final FileLog filelog : Logs.values()){
			if (!filelog.closeNoRemove()){
				Warning.other("Unable to close all logs. Some might not save properly.", false);
			}
		}
		Logs = null;
	}
	protected BufferedWriter out;
	protected String type = "";
	protected int day = 0;
	protected int counter = 0;
	protected static HashMap<String, FileLog> Logs = new HashMap<>();
	protected final boolean consoleLog;

	protected static final String sep = File.separator;

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yy");

	@NonNull public static FileLog getLog(@NonNull final String type){
		return Logs.get(type);
	}

	@NonNull public static FileLog getLogOrMake(@Nullable String type, final boolean consoleLog){
		if (type == null) type = "null";
		final FileLog tbr = Logs.get(type);
		return tbr == null ? new FileLog(type, consoleLog) : tbr;
	}

	protected boolean logged = false;

	protected boolean logged2 = false;

	private static final SimpleDateFormat timeFormat = new SimpleDateFormat("kk:mm:ss");

	@SuppressWarnings("deprecation")
	public FileLog(@NonNull final String type, final boolean consoleLog){
		this.type = type;
		this.consoleLog = consoleLog;
		this.day = new Date(System.currentTimeMillis()).getDay();

		final File log;
		final File folder;
		if (TRConfigCache.LogFilter.logLocation.matches("[a-zA-Z]:"+sep+".*")
				|| TRConfigCache.LogFilter.logLocation.startsWith("."+sep)
				|| TRConfigCache.LogFilter.logLocation.startsWith(sep)){
			folder = new File(TRConfigCache.LogFilter.logLocation+sep+type);
			if (!folder.exists()) folder.mkdirs();

			log = new File(folder, formatName(type));
		} else {
			folder = new File("."+sep+TRConfigCache.LogFilter.logLocation+sep+type);
			if (!folder.exists()) folder.mkdirs();

			log = new File(folder, formatName(type));
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
		if (TRConfigCache.LogFilter.logLocation.matches("[a-zA-Z]:"+sep+".*")
				|| TRConfigCache.LogFilter.logLocation.startsWith("."+sep)){
			folder = new File(TRConfigCache.LogFilter.logLocation, "type");
			if (!folder.exists()) folder.mkdirs();

			log = new File(folder, formatName(type));
		} else {
			folder = new File("."+sep+TRConfigCache.LogFilter.logLocation, type);
			if (!folder.exists()) folder.mkdirs();

			log = new File(folder, formatName(type));
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

	@NonNull protected String formatMsg(@Nullable final String msg){
		return formatMsg(msg, System.currentTimeMillis());
	}
	@NonNull protected String formatMsg(@Nullable String msg, final long time){
		if (msg == null) msg = "null";

		final String times = timeFormat.format(new Date(time));

		final String format = TRConfigCache.LogFilter.logFormat;
		if ((format == null) || format.isEmpty()){
			if (!logged2){
				Warning.other(ChatColor.RED + "The log format set in the Logging config is invalid!", false);
				logged2 = true;
			}

			return new StringBuilder("[").append(times).append("] ").append(msg).toString();
		} else {
			final String timestr[] = times.split(":");
			return format.replace("{HOUR}", timestr[0])
					.replace("{MINUTE}", timestr[1])
					.replace("{SECOND}", timestr[2])
					.replace("{INFO}", msg);
		}
	}
	@NonNull protected String formatName(@NonNull String type){
		final String data = dateFormat.format(new Date(System.currentTimeMillis()));
		final String date[] = data.split("-");

		String name = TRConfigCache.LogFilter.fileFormat;
		if ((name == null) || name.isEmpty() || name.contains("*") || name.endsWith(".")){
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
	@Override
	public int hashCode(){
		return type.hashCode() + (consoleLog ? 1 : 0);
	}

	public void log(@Nullable final String msg){
		log(msg, System.currentTimeMillis());
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
	@NonNull protected String replacecolors(@NonNull final String input){
		return input.replace("\033[30;22m", "�0")
				.replace("\033[34;22m", "�1")
				.replace("\033[32;22m", "�2")
				.replace("\033[36;22m", "�3")
				.replace("\033[31;22m", "�4")
				.replace("\033[35;22m", "�5")
				.replace("\033[33;22m", "�6")
				.replace("\033[37;22m", "�7")
				.replace("\033[30;1m", "�8")
				.replace("\033[34;1m", "�9")
				.replace("\033[32;1m", "�a")
				.replace("\033[36;1m", "�b")
				.replace("\033[31;1m", "�c")
				.replace("\033[35;1m", "�d")
				.replace("\033[33;1m", "�e")
				.replace("\033[37;1m", "�f")

				/*.replace("\033[0;30;22m", "�0");
					.replace("\033[0;34;22m", "�1");
					.replace("\033[0;32;22m", "�2");
					.replace("\033[0;36;22m", "�3");
					.replace("\033[0;31;22m", "�4");
					.replace("\033[0;35;22m", "�5");
					.replace("\033[0;33;22m", "�6");
					.replace("\033[0;37;22m", "�7");
					.replace("\033[0;30;1m", "�8");
					.replace("\033[0;34;1m", "�9");
					.replace("\033[0;32;1m", "�a");
					.replace("\033[0;36;1m", "�b");
					.replace("\033[0;31;1m", "�c");
					.replace("\033[0;35;1m", "�d");
					.replace("\033[0;33;1m", "�e");
					.replace("\033[0;37;1m", "�f");

					.replace("\033[30m", "�0");
					.replace("\033[32m", "�2");
					.replace("\033[36m", "�3");
					.replace("\033[31m", "�4");
					.replace("\033[35m", "�5");
					.replace("\033[33m", "�6");
					.replace("\033[37m", "�7");
					.replace("\033[30m", "�8");
					.replace("\033[34m", "�9");
					.replace("\033[32m", "�a");
					.replace("\033[36m", "�b");
					.replace("\033[31m", "�c");
					.replace("\033[35m", "�d");
					.replace("\033[33m", "�e");
					.replace("\033[37m", "�f");*/

				.replace("\033[5m", "�k")
				.replace("\033[21m", "�l")
				.replace("\033[9m", "�m")
				.replace("\033[4m", "�n")
				.replace("\033[3m", "�o")
				.replace("\033[0;39m", "�r")

				.replace("\033[0m", "�r")

				.replace("\033[m", "");
	}

	@NonNull protected String replaceshort(@NonNull final String input){
		return input.replace("\033[m", "");
	}
}
