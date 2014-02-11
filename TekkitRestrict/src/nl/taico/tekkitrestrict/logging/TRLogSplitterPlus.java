package nl.taico.tekkitrestrict.logging;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.logging.FileHandler;
import java.util.logging.Filter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.configuration.ConfigurationSection;

import nl.taico.tekkitrestrict.FileLog;
import nl.taico.tekkitrestrict.objects.TREnums.TRMatchMethod;
import nl.taico.tekkitrestrict.objects.TREnums.TRSplitLevel;

import static nl.taico.tekkitrestrict.TRConfigCache.LogFilter.*;

public class TRLogSplitterPlus {
	public static Filter splitFilter = new TRSplitterFilter();
	
	public static void assignSplitter(){
		for (Handler h : Logger.getLogger("Minecraft").getHandlers()){
			if (h instanceof FileHandler) h.setFilter(splitFilter);
		}
	}
	
	public static void disable(){
		for (Handler h : Logger.getLogger("Minecraft").getHandlers()){
			if (h.getFilter() == splitFilter) h.setFilter(null);
		}
	}
	
	private TRMatchMethod method;
	private String file;
	private FileLog log = null;
	private TRSplitLevel level;
	private Set<String> splitters = new HashSet<String>();
	public static ArrayList<TRLogSplitterPlus> allSplitters = new ArrayList<TRLogSplitterPlus>();
	public static ArrayList<TRLogSplitterPlus> cmdSplitters = new ArrayList<TRLogSplitterPlus>();
	
	public static void loadSplitters(ConfigurationSection cs, ConfigurationSection cs2){
		allSplitters.clear();
		for (String key : cs.getKeys(false)){
			TRMatchMethod method = null;
			final String cmethod = cs.getString(key+".Method", "contains").replace("_", "").toUpperCase();
			for (TRMatchMethod m : TRMatchMethod.values()){
				if (m.name().replace("_", "").equals(cmethod)){
					method = m;
					break;
				}
			}
			if (method == null) method = TRMatchMethod.CONTAINS;
			method.caseSensitive = cs.getBoolean(key+".CaseSensitive", false);
			final String clevel = cs.getString(key+".Level", "all").replace("_", "").toUpperCase();
			TRSplitLevel level = null;
			for (TRSplitLevel l : TRSplitLevel.values()){
				if (l.name().replace("_", "").equals(clevel)){
					level = l;
					break;
				}
			}
			if (level == null) level = TRSplitLevel.ALL;
			final String file = cs.getString(key+".File", "Undefined");
			new TRLogSplitterPlus(file, method, level, cs.getStringList(key+".Messages"));
		}
		
		cmdSplitters.clear();
		for (String key : cs2.getKeys(false)){
			TRMatchMethod method = null;
			final String cmethod = cs2.getString(key+".Method", "equals").replace("_", "").toUpperCase();
			for (TRMatchMethod m : TRMatchMethod.values()){
				if (m.name().replace("_", "").equals(cmethod)){
					method = m;
					break;
				}
			}
			if (method == null) method = TRMatchMethod.EQUALS;
			method.caseSensitive = false;
			final String file = cs2.getString(key+".File", "Undefined");
			new TRLogSplitterPlus(file, method, TRSplitLevel.COMMAND, cs2.getStringList(key+".Commands"));
		}
	}
	
	public TRLogSplitterPlus(String file, TRMatchMethod method, TRSplitLevel level, Collection<String> splitters){
		this.file = file;
		this.method = method;
		this.level = level;
		if (level == TRSplitLevel.COMMAND){
			for (String s : splitters) addCommandSplitter(s);
			cmdSplitters.add(this);
		} else {
			for (String s : splitters) addSplitter(s);
			allSplitters.add(this);
		}
		
	}
	
	public void addSplitter(String splitter){
		splitters.add(method.isCS() ? splitter : splitter.toLowerCase(Locale.ENGLISH));
	}
	
	public void addCommandSplitter(String splitter){
		if (splitter.startsWith("/")) splitters.add(splitter.toLowerCase(Locale.ENGLISH).substring(1, splitter.length()));
		else splitters.add(splitter.toLowerCase(Locale.ENGLISH));
	}
	
	public boolean matches(String input, Level level){
		if (!this.level.matches(level)) return false;
		if (!method.isCS()) input = input.toLowerCase(Locale.ENGLISH);
		
		switch (method){
			case CONTAINS:
				for (String splitter : splitters){
					if (input.contains(splitter)) return true;
				}
				return false;
			case ENDS_WITH:
				for (String splitter : splitters){
					if (input.endsWith(splitter)) return true;
				}
				return false;
			case EQUALS:
				return splitters.contains(input);
			case STARTS_WITH:
				for (String splitter : splitters){
					if (input.startsWith(splitter)) return true;
				}
				return false;
			case REGEX:
				for (String splitter : splitters){
					if (input.matches(splitter)) return true;
				}
				return false;
			default:
				return false;
		}
	}
	
	public boolean matchesCommand(String input){
		switch (method){
			case EQUALS:
				return splitters.contains(input);
			case CONTAINS:
				for (String splitter : splitters){
					if (input.contains(splitter)) return true;
				}
				return false;
			case ENDS_WITH:
				input = input.split(" ")[0];
				for (String splitter : splitters){
					if (input.endsWith(splitter)) return true;
				}
				return false;
			case STARTS_WITH:
				for (String splitter : splitters){
					if (input.startsWith(splitter)) return true;
				}
				return false;
			case REGEX:
				for (String splitter : splitters){
					if (input.matches(splitter)) return true;
				}
				return false;
			default:
				return false;
		}
	}
	
	public void setCaseInSensitive(){
		if (method.isCS()){
			method.caseSensitive = false;
			Set<String> temp = new HashSet<String>();
			for (String s : splitters) temp.add(s.toLowerCase(Locale.ENGLISH));
			splitters = temp;
		}
	}
	
	public void log(String input){
		if (log == null) log = FileLog.getLogOrMake(file, true);
		log.log(input);
	}
	
	public static void split(String input, Level level){
		boolean found = false;
		final String output = "["+level.getName()+"] "+input;
		for (TRLogSplitterPlus splitter: allSplitters){
			if (!splitter.matches(input, level)) continue;
			splitter.log(output);
			found = true;
		}
		if (!found){
			FileLog.getLogOrMake("Info", true).log(output);
		}
	}
	
	public static void logCommand(String player, String message){
		String msg = player + " used " + message;
		if (logAllCommands){
			if (logAllCommandsLog == null) logAllCommandsLog = FileLog.getLogOrMake(logAllCommandsFile, false);
			logAllCommandsLog.log(msg);
		}
		
		message = message.substring(1, message.length());
		for (TRLogSplitterPlus splitter: cmdSplitters){
			if (!splitter.matchesCommand(message)) continue;
			splitter.log(msg);
		}
	}
	
	public static void logNEI(String message){
		if (logAllCommands){
			if (logAllCommandsLog == null) logAllCommandsLog = FileLog.getLogOrMake(logAllCommandsFile, false);
			logAllCommandsLog.log(message);
		}
		
		if (logNEIGive){
			if (logNEIGiveLog == null) logNEIGiveLog = FileLog.getLogOrMake(logNEIGiveFile, false);
			logNEIGiveLog.log(message);
		}
	}
}
