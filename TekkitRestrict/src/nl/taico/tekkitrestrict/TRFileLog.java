package nl.taico.tekkitrestrict;

import javax.annotation.Nullable;

import lombok.NonNull;

public class TRFileLog extends FileLog {

	public static TRFileLog getLogOrMake(String type, boolean consoleLog){
		if (type == null) type = "null";
		FileLog tbr = Logs.get(type);
		if (tbr == null) return new TRFileLog(type, consoleLog);
		else if (!(tbr instanceof TRFileLog)) throw new RuntimeException("FileLog with this name already exists!");
		else return (TRFileLog) tbr;
	}

	private TRFileLog(String type, boolean consoleLog) {
		super(type, consoleLog);
	}

	@Override
	@NonNull protected String formatMsg(@Nullable String msg){
		if (msg == null) msg = "null";
		return msg;
	}

}
