package nl.taico.tekkitrestrict.logging;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Filter;
import java.util.logging.Handler;
import java.util.logging.Logger;

import org.bukkit.configuration.ConfigurationSection;

public class TRLogFilterPlus {
	public static Filter consoleFilter = new TRConsoleFilter();
	public static Filter forgeFilter = new TRForgeFilter();
	public static Filter logFilter = new TRLogFilter();
	
	public enum TRFilterMethod {
		STARTS_WITH, ENDS_WITH, CONTAINS, EQUALS, REGEX;
		public boolean caseSensitive;
		TRFilterMethod(){
			caseSensitive = false;
		}
		TRFilterMethod(boolean caseSensitive){
			this.caseSensitive = caseSensitive;
		}
		
		public boolean isCaseSensitive(){
			return this.caseSensitive;
		}
		public boolean isCS(){
			return this.caseSensitive;
		}
		
		public static TRFilterMethod byName(String name){
			name = name.replace(" ", "").replace("_", "").toUpperCase();
			for (TRFilterMethod m : TRFilterMethod.values()){
				if (name.equals(m.name().replace("_", ""))) return m;
			}
			return null;
		}
	}
	public enum FilterType {
		CONSOLE, CONSOLE_SERVER_LOG, SERVER_LOG, FORGE_SERVER_LOG, FORGE_LOG, ALL;
		public boolean isConsole(){
			return this == CONSOLE || this == CONSOLE_SERVER_LOG || this == ALL;
		}
		public boolean isServerLog(){
			return this == SERVER_LOG || this == CONSOLE_SERVER_LOG || this == ALL || this == FORGE_SERVER_LOG;
		}
		public boolean isForgeLog(){
			return this == FORGE_LOG || this == ALL || this == FORGE_SERVER_LOG;
		}
	}
	
	private TRFilterMethod method;
	private Set<String> filters = new HashSet<String>();
	private final FilterType type;
	public static ArrayList<TRLogFilterPlus> allFilters = new ArrayList<TRLogFilterPlus>();
	
	public static void loadFilters(ConfigurationSection cs){
		allFilters.clear();
		for (String key : cs.getKeys(false)){
			TRFilterMethod method = null;
			final String cmethod = cs.getString(key+".Method", "contains").replace("_", "").toUpperCase();
			for (TRFilterMethod m : TRFilterMethod.values()){
				if (m.name().replace("_", "").equals(cmethod)){
					method = m;
					break;
				}
			}
			if (method == null) method = TRFilterMethod.CONTAINS;
			method.caseSensitive = cs.getBoolean(key+".CaseSensitive", false);
			final String ctype = cs.getString(key+".Type", "all").replace("_", "").toUpperCase();
			FilterType type = null;
			for (FilterType t : FilterType.values()){
				if (t.name().replace("_", "").equals(ctype)){
					type = t;
					break;
				}
			}
			if (type == null) type = FilterType.ALL;
			new TRLogFilterPlus(method, type, cs.getStringList("Messages"));
		}
		//TODO make a separate filter for log_and_console, which runs on the minecraft logger
		((TRConsoleFilter) consoleFilter).reload();
		((TRForgeFilter) forgeFilter).reload();
		((TRLogFilter) logFilter).reload();
	}
	
	public TRLogFilterPlus(TRFilterMethod method, FilterType type, Collection<String> filters){
		this.method = method;
		this.type = type;
		for (String filter : filters) addFilter(filter);
		allFilters.add(this);
	}
	
	public void addFilter(String filter){
		filters.add(method.isCS() ? filter : filter.toLowerCase(Locale.ENGLISH));
	}
	
	public boolean matches(String input){
		if (!method.isCS()) input = input.toLowerCase(Locale.ENGLISH);
		switch (method){
			case CONTAINS:
				for (String filter : filters){
					if (input.contains(filter)) return true;
				}
				return false;
			case ENDS_WITH:
				for (String filter : filters){
					if (input.endsWith(filter)) return true;
				}
				return false;
			case EQUALS:
				return filters.contains(input);
			case STARTS_WITH:
				for (String filter : filters){
					if (input.startsWith(filter)) return true;
				}
				return false;
			case REGEX:
				for (String filter : filters){
					if (input.matches(filter)) return true;
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
			for (String s : filters) temp.add(s.toLowerCase(Locale.ENGLISH));
			filters = temp;
		}
	}
	
	public FilterType getType() {
		return type;
	}
	
	public static void assignFilters(){
		final Logger mclogger = Logger.getLogger("Minecraft");
		final Handler[] mchandlers = mclogger.getHandlers();
		
		for (Handler h : mchandlers){
			if (h instanceof ConsoleHandler){
				h.setFilter(consoleFilter);
			} else if (h instanceof FileHandler){
				h.setFilter(logFilter);
			}
		}

		final Logger forgelogger = Logger.getLogger("ForgeModLoader");
		for (Handler h : forgelogger.getHandlers()){
			if (!(h instanceof FileHandler)) continue;
			h.setFilter(forgeFilter);
		}
	}
	
	public static boolean matches(TRFilterMethod method, String input, String filter){
		switch (method){
			case REGEX:
				if (!method.isCS()) input = input.toLowerCase(Locale.ENGLISH);
				return input.matches(filter);
			case CONTAINS: 
				if (!method.isCS()){
					input = input.toLowerCase(Locale.ENGLISH);
					filter = filter.toLowerCase(Locale.ENGLISH);
				}
				return input.contains(filter);
			case ENDS_WITH: 
				if (!method.isCS()){
					input = input.toLowerCase(Locale.ENGLISH);
					filter = filter.toLowerCase(Locale.ENGLISH);
				}
				return input.endsWith(filter);
			case EQUALS: 
				if (!method.isCS()){
					input = input.toLowerCase(Locale.ENGLISH);
					filter = filter.toLowerCase(Locale.ENGLISH);
				}
				return input.equals(filter);
			case STARTS_WITH: 
				if (!method.isCS()){
					input = input.toLowerCase(Locale.ENGLISH);
					filter = filter.toLowerCase(Locale.ENGLISH);
				}
				return input.startsWith(filter);
			default: return false;
		}
	}
}
