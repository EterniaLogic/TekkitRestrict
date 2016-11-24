package nl.taico.tekkitrestrict.logging;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Filter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nl.taico.taeirlib.config.interfaces.ISection;
import nl.taico.tekkitrestrict.Log;
import nl.taico.tekkitrestrict.config.SettingsStorage;
import nl.taico.tekkitrestrict.logging.TRFilter.Priority;

public class TRLogEnhancer implements Filter {
	private static TRLogEnhancer instance = new TRLogEnhancer();
	private static boolean enhanceCMD;

	private static boolean changeGive;

	private static boolean shortenErrors;
	private static boolean enhanceCMDDeny;
	private static HashMap<Pattern, String> replacements = new HashMap<Pattern, String>();
	private static Pattern givePattern = Pattern.compile("Giving (\\d+) of (.*) to (.*)\\.");
	private static Pattern neiGive = Pattern.compile("Giving (.*) (\\d+) of (.*)");

	public static void assignFilters(){
		for (final Handler h : Logger.getLogger("Minecraft").getHandlers()){
			if (h instanceof ConsoleHandler){
				if (h.getFilter() instanceof TRFilter){
					((TRFilter) h.getFilter()).addFilter(instance, Priority.LOW);
				} else {
					TRFilter trf = new TRFilter(h.getFilter());
					trf.addFilter(instance, Priority.LOW);
					h.setFilter(trf);
				}
			} else if (h instanceof FileHandler) {
				if (h.getFilter() instanceof TRFilter){
					((TRFilter) h.getFilter()).addFilter(instance, Priority.LOW);
				} else {
					TRFilter trf = new TRFilter(h.getFilter());
					trf.addFilter(instance, Priority.LOW);
					h.setFilter(trf);
				}
			}
		}
	}
	public static void disable(){
		for (final Handler h : Logger.getLogger("Minecraft").getHandlers()){
			Filter f = h.getFilter();
			if (f instanceof TRFilter){
				((TRFilter) f).removeAndConvert(h, instance);
			} else if (f == instance) h.setFilter(null);
		}
	}
	public static void reload(){
		enhanceCMD = SettingsStorage.loggingConfig.getBoolean("EnchanceEssentialsCmd", true);
		changeGive = SettingsStorage.loggingConfig.getBoolean("ChangeGive", true);
		shortenErrors = SettingsStorage.loggingConfig.getBoolean("ShortenErrors", true);
		enhanceCMDDeny = SettingsStorage.loggingConfig.getBoolean("EnhanceEssentialsCmdDeny", true);

		ISection cs = SettingsStorage.loggingConfig.getSection("Reformat");
		if ((cs == null) || cs.getKeys(false).isEmpty()){
			replacements.clear();
			return;
		}
		HashMap<Pattern, String> l = new HashMap<Pattern, String>();
		for (String key : cs.getKeys(false)){
			l.put(Pattern.compile(cs.getString(key+".Message", "")), cs.getString(key+".Replacement", ""));
		}
		replacements = l;
	}

	@Override
	public boolean isLoggable(LogRecord record){
		String msg = record.getMessage();
		if (enhanceCMD){
			if (msg.startsWith("[PLAYER_COMMAND] ")){
				msg = msg.substring(17);
				record.setLevel(Log.cmd);
			}
		}

		{
			Matcher m;
			if (changeGive){
				if ((m=givePattern.matcher(msg)).matches()){
					msg = m.replaceAll("SERVER: /give $3 $2 $1");
					record.setLevel(Log.cmd);
				}
				else if ((m=neiGive.matcher(msg)).matches()){
					msg = m.replaceAll("NEI: /give $1 $3 $2");
					record.setLevel(Log.cmd);
				}
			}}


		if (shortenErrors && (record.getLevel() == Level.SEVERE)){

		}
		if (enhanceCMDDeny && (record.getLevel() == Level.WARNING) && msg.contains(" was denied access to command.")){
			record.setLevel(Log.cmd);
		}

		try {
			final Iterator<Entry<Pattern, String>> it = replacements.entrySet().iterator();
			while (it.hasNext()){
				final Entry<Pattern, String> e = it.next();
				final Matcher matcher;
				if ((matcher = e.getKey().matcher(msg)).matches()) msg = matcher.replaceAll(e.getValue());
			}
		} catch (Exception ex){

		}

		record.setMessage(msg);
		return true;
		//Giving 8 of x27563 to
		//"Giving (\\d+) of (.*) to (.*)\\."
	}
}
