package nl.taico.tekkitrestrict.logging;

import java.util.ArrayList;
import java.util.logging.Filter;
import java.util.logging.LogRecord;

import nl.taico.tekkitrestrict.Log;

/**
 * Filter meant for ConsoleHandlers.
 * @author Taico
 */
public class TRConsoleFilter implements Filter {
	private ArrayList<TRLogFilterPlus> filters;
	public void reload(){
		ArrayList<TRLogFilterPlus> tbr = new ArrayList<TRLogFilterPlus>();
		for (TRLogFilterPlus f : TRLogFilterPlus.allFilters){
			if (f.getType().isConsole()) tbr.add(f);
		}
		filters = tbr;
	}
	
	@Override
	public boolean isLoggable(LogRecord record) {
		try {
			if (filters == null) reload();
		} catch (Exception ex){
			Log.debug("Exception in TRConsoleFilter reload: ");
			Log.debugEx(ex);
		}
		
		try {
			final String msg = record.getMessage();
			for (TRLogFilterPlus filter: filters){
				if (filter.matches(msg)) return false;
			}
		} catch (Exception ex){
			Log.debug("Exception in TRConsoleFilter reload: ");
			Log.debugEx(ex);
		}
		
		return true;
	}

}
