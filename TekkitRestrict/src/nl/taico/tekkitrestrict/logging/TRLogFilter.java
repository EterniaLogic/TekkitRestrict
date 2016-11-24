package nl.taico.tekkitrestrict.logging;

import java.util.ArrayList;
import java.util.logging.Filter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class TRLogFilter implements Filter {
	private ArrayList<TRLogFilterPlus> filters;
	public void reload(){
		ArrayList<TRLogFilterPlus> tbr = new ArrayList<TRLogFilterPlus>();
		for (TRLogFilterPlus f : TRLogFilterPlus.allFilters){
			if (f.getType().isServerLog()) tbr.add(f);
		}
		filters = tbr;
	}
	
	@Override
	public boolean isLoggable(LogRecord record) {
		if (record.getMessage() == null) return true;
		final Level lvl = record.getLevel();
		if (lvl == Level.FINE || lvl == Level.FINER || lvl == Level.FINEST) return true;
		try {
			if (filters == null){
				filters = new ArrayList<TRLogFilterPlus>();
				for (TRLogFilterPlus f : TRLogFilterPlus.allFilters){
					if (f.getType().isServerLog()) filters.add(f);
				}
			}
			
			try {
				final String msg = record.getMessage();
				for (TRLogFilterPlus filter: filters){
					if (filter.matches(msg)) return false;
				}
			} catch (Exception ex){
				return true;
			}
			//debug
			//test calculation that takes a while to see if the server is affected
			//if it is, let the LOG filter run on a separate thread
			
			//for (int i = 0; i< 10000; i++){
			//	Math.sqrt(Math.sqrt(Math.sqrt(Math.sqrt(i))));
			//}
		} catch (Exception ex){
			
		}
		return true;
	}

}
