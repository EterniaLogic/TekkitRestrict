package nl.taico.tekkitrestrict.logging;

import java.util.ArrayList;
import java.util.logging.Filter;
import java.util.logging.LogRecord;

public class TRLogHandler implements Filter {
	public static ArrayList<String> filterout = new ArrayList<String>();
	
	public static void initDefaults(){
		
	}
	
	@Override
	public boolean isLoggable(LogRecord record) {
		if (record.getMessage() == null) return true;
		
		if (TRFilter.shouldBeFiltered(record)) return false;
		TRSplitter.split(record);
		
		return true;
	}

}
