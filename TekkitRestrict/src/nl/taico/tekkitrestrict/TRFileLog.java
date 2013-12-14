package nl.taico.tekkitrestrict;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

public class TRFileLog extends FileLog {
	
	public TRFileLog(String type, boolean consoleLog) {
		super(type, consoleLog);
	}
	
	public TRFileLog(String type, boolean alternate, boolean consoleLog) {
		super(type, alternate, consoleLog);
	}
	
	@Override
	@NonNull protected String formatMsg(@Nullable String msg){
		if (msg == null) msg = "null";
		return msg;
	}
	
	public static TRFileLog getLogOrMake(String type, boolean alternate, boolean consoleLog){
		if (type == null) type = "null";
		FileLog tbr = Logs.get(type);
		if (tbr == null) return new TRFileLog(type, alternate, consoleLog);
		else if (!(tbr instanceof TRFileLog)) throw new RuntimeException("FileLog with this name already exists!");
		else return (TRFileLog) tbr;
	}
	
}
