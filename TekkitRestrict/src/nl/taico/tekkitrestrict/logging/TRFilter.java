package nl.taico.tekkitrestrict.logging;

import java.util.ArrayList;
import java.util.logging.LogRecord;

import nl.taico.tekkitrestrict.objects.TREnums.TRLogLevel;

public class TRFilter {	
	private String value;
	private boolean cs;
	private boolean regex;
	private TRLogLevel level;
	private static ArrayList<TRFilter> filters = new ArrayList<TRFilter>();
	
	/*
	public TRFilter(String value){
		this(value, false, TRLogLevel.ALL);
	}
	
	public TRFilter(String value, boolean cs){
		this(value, cs, TRLogLevel.ALL);
	}
	*/
	
	public TRFilter(String value, boolean cs, TRLogLevel level){
		this(value, cs, false, level);
	}
	
	public TRFilter(String value, boolean cs, boolean regex, TRLogLevel level){
		this.value = cs ? value : value.toLowerCase();
		this.cs = cs;
		this.regex = regex;
		this.level = level;
		filters.add(this);
	}
	
	public String getValue(){
		return value;
	}
	
	public boolean isCaseSensitive(){
		return cs;
	}
	
	public TRLogLevel getLogLevel(){
		return level;
	}
	
	public ArrayList<TRFilter> getFilters(){
		return filters;
	}
	
	public boolean matches(LogRecord record){
		if (!level.doesApply(record.getLevel())) return false;
		if (!regex){
			if (cs) return record.getMessage().contains(value);
			else return record.getMessage().toLowerCase().contains(value);
		} else {
			return record.getMessage().matches(value);
		}
	}
	
	public static boolean shouldBeFiltered(LogRecord record){
		for (TRFilter f : filters){
			if (f.matches(record)) return true;
		}
		return false;
	}
}


