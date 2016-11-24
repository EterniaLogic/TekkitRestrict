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

import lombok.ToString;
import nl.taico.taeirlib.config.interfaces.ISection;
import nl.taico.tekkitrestrict.Log;
import nl.taico.tekkitrestrict.logging.TRFilter.Priority;
import nl.taico.tekkitrestrict.objects.TREnums.TRFilterType;
import nl.taico.tekkitrestrict.objects.TREnums.TRMatchMethod;

@ToString
public class TRLogFilterPlus {
	public static Filter consoleFilter = new TRConsoleFilter();
	public static Filter forgeFilter = new TRForgeFilter();
	public static Filter logFilter = new TRLogFilter();

	private TRMatchMethod method;
	private Set<String> filters = new HashSet<String>();
	private final TRFilterType type;
	public static ArrayList<TRLogFilterPlus> allFilters = new ArrayList<TRLogFilterPlus>();

	public static void assignFilters(){
		Log.trace("TRLogFilterPlus - Assigning filters");
		for (final Handler h : Logger.getLogger("Minecraft").getHandlers()){
			if (h instanceof ConsoleHandler){
				if (h.getFilter() instanceof TRFilter){
					((TRFilter) h.getFilter()).addFilter(consoleFilter, Priority.LOW);
				} else {
					TRFilter trf = new TRFilter(h.getFilter());
					trf.addFilter(consoleFilter, Priority.LOW);
					h.setFilter(trf);
				}
				Log.trace("TRLogFilterPlus - Added ConsoleFilter");
			}
			else if (h instanceof FileHandler) {
				if (h.getFilter() instanceof TRFilter){
					((TRFilter) h.getFilter()).addFilter(logFilter, Priority.LOW);
				} else {
					TRFilter trf = new TRFilter(h.getFilter());
					trf.addFilter(logFilter, Priority.LOW);
					h.setFilter(trf);
				}
				Log.trace("TRLogFilterPlus - Added FileFilter");
			}
		}

		for (final Handler h : Logger.getLogger("ForgeModLoader").getHandlers()){
			if (h instanceof FileHandler){
				if (h.getFilter() instanceof TRFilter){
					((TRFilter) h.getFilter()).addFilter(forgeFilter, Priority.LOW);
				} else {
					TRFilter trf = new TRFilter(h.getFilter());
					trf.addFilter(forgeFilter, Priority.LOW);
					h.setFilter(trf);
				}
				Log.trace("TRLogFilterPlus - Added FileFilter for forge");
			}
		}
	}

	public static void disable(){
		Log.trace("TRLogFilterPlus - Disabling filters");
		for (final Handler h : Logger.getLogger("Minecraft").getHandlers()){
			Filter f = h.getFilter();
			if (f instanceof TRFilter){
				((TRFilter) f).removeAndConvert(h, consoleFilter, logFilter);
			} else if ((f == consoleFilter) || (f == logFilter)) h.setFilter(null);
		}

		for (final Handler h : Logger.getLogger("ForgeModLoader").getHandlers()){
			Filter f = h.getFilter();
			if (f instanceof TRFilter){
				((TRFilter) f).removeAndConvert(h, forgeFilter);
			} else if (f == forgeFilter) h.setFilter(null);
		}
	}

	public static void loadFilters(ISection cs){
		allFilters.clear();
		Log.trace("TRLogFilterPlus - Loading filters from config");
		Log.trace("TRLogFilterPlus - Config Filters: " + cs.getKeys(false));
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
			final String ctype = cs.getString(key+".Type", "all").replace("_", "").toUpperCase();
			TRFilterType type = null;
			for (TRFilterType t : TRFilterType.values()){
				if (t.name().replace("_", "").equals(ctype)){
					type = t;
					break;
				}
			}
			if (type == null) type = TRFilterType.ALL;
			new TRLogFilterPlus(method, type, cs.getStringList(key+".Messages"));
		}
		//TODO make a separate filter for log_and_console, which runs on the minecraft logger
		((TRConsoleFilter) consoleFilter).reload();
		((TRForgeFilter) forgeFilter).reload();
		((TRLogFilter) logFilter).reload();
	}

	public static boolean matches(TRMatchMethod method, String input, String filter){
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

	public TRLogFilterPlus(TRMatchMethod method, TRFilterType type, Collection<String> filters){
		this.method = method;
		this.type = type;
		for (String filter : filters) this.filters.add(method.isCS() ? filter : filter.toLowerCase(Locale.ENGLISH));
		allFilters.add(this);
		Log.trace("TRLogFilterPlus - Added Filter: " + this);
	}

	public TRFilterType getType() {
		return type;
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
}
