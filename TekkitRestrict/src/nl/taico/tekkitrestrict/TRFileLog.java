package nl.taico.tekkitrestrict;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

public class TRFileLog extends FileLog {
	
	private TRFileLog(String type, boolean consoleLog) {
		super(type, consoleLog);
	}
	
	@Override
	@NonNull protected String formatMsg(@Nullable String msg){
		if (msg == null) msg = "null";
		return msg;
	}
	
	public static TRFileLog getLogOrMake(String type, boolean consoleLog){
		if (type == null) type = "null";
		FileLog tbr = Logs.get(type);
		if (tbr == null) return new TRFileLog(type, consoleLog);
		else if (!(tbr instanceof TRFileLog)) throw new RuntimeException("FileLog with this name already exists!");
		else return (TRFileLog) tbr;
	}
	
}
