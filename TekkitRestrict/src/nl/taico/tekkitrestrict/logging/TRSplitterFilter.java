package nl.taico.tekkitrestrict.logging;

import java.util.logging.Filter;
import java.util.logging.LogRecord;

import nl.taico.tekkitrestrict.Log;

public class TRSplitterFilter implements Filter {
	@Override
	public boolean isLoggable(LogRecord record) {
		try {
			TRLogSplitterPlus.split(record.getMessage(), record.getLevel());
		} catch (Exception ex){
			Log.debugEx(ex);
		}
		return true;
	}
}
