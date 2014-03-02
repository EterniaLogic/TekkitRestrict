package nl.taico.tekkitrestrict.logging;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.configuration.ConfigurationSection;

import nl.taico.tekkitrestrict.config.SettingsStorage;

public class TRLogEnhancer {
	public static void reload(){
		enhanceCMD = SettingsStorage.loggingConfig.getBoolean("EnchanceEssentialsCmd", true);
		changeGive = SettingsStorage.loggingConfig.getBoolean("ChangeGive", true);
		shortenErrors = SettingsStorage.loggingConfig.getBoolean("ShortenErrors", true);
		enhanceCMDDeny = SettingsStorage.loggingConfig.getBoolean("EnhanceEssentialsCmdDeny", true);
		
		ConfigurationSection cs = SettingsStorage.loggingConfig.getConfigurationSection("Reformat");
		if (cs == null){
			replacements.clear();
			return;
		}
		HashMap<Pattern, String> l = new HashMap<Pattern, String>();
		for (String key : cs.getKeys(false)){
			l.put(Pattern.compile(cs.getString(key+".Message", "")), cs.getString(key+".Replacement", ""));
		}
		replacements = l;
	}
	private static boolean enhanceCMD;
	private static boolean changeGive;
	private static boolean shortenErrors;
	private static boolean enhanceCMDDeny;
	
	private static HashMap<Pattern, String> replacements = new HashMap<Pattern, String>();
	private static Pattern givePattern = Pattern.compile("Giving (\\d+) of (.*) to (.*)\\.");
	private static Pattern neiGive = Pattern.compile("Giving (.*) (\\d+) of (.*)");

	public static void enchance(LogRecord record){
		String msg = record.getMessage();
		if (enhanceCMD) msg = msg.replace("[PLAYER_COMMAND] ", "[CMD] ");
		
		{
		Matcher m;
		if (changeGive){
			if ((m=givePattern.matcher(msg)).matches()) msg = m.replaceAll("[CMD] SERVER: /give ($3) ($2) ($1)");
			else if ((m=neiGive.matcher(msg)).matches()) msg = m.replaceAll("[CMD] NEI: /give ($1) ($3) ($2)");
		}}
		
		
		if (shortenErrors && record.getLevel() == Level.SEVERE){
			
		}
		if (enhanceCMDDeny && record.getLevel() == Level.WARNING && msg.contains(" was denied access to command.")){
			msg = "[CMD] "+msg;
			record.setLevel(Level.INFO);
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
		//Giving 8 of x27563 to
		//"Giving (\\d+) of (.*) to (.*)\\."
	}
}
