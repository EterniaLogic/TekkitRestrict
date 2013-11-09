package nl.taico.tekkitrestict.objects;

public class TRDupeSettings {
	public boolean prevent, broadcast, kick, useCommand;
	public String command = "";
	public int triggerAfter;
	
	public TRDupeSettings(){}
	
	public TRDupeSettings(boolean prevent, boolean broadcast, boolean kick, boolean useCommand, int triggerAfter, String command){
		this.prevent = prevent;
		this.broadcast = broadcast;
		this.kick = kick;
		this.useCommand = useCommand;
		this.triggerAfter = triggerAfter;
		this.command = command;
	}
}
