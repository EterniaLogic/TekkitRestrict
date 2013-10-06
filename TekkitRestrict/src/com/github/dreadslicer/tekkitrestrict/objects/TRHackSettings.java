package com.github.dreadslicer.tekkitrestrict.objects;

public class TRHackSettings {
	public int tolerance;
	public boolean enable, broadcast, kick, useCommand;
	public String command = "";
	public int triggerAfter;
	public double value;
	
	public TRHackSettings(){}
	
	public TRHackSettings(boolean enable, boolean broadcast, boolean kick, int tolerance, double value, boolean useCommand, int triggerAfter, String command){
		this.enable = enable;
		this.broadcast = broadcast;
		this.kick = kick;
		this.tolerance = tolerance;
		this.value = value;
		this.useCommand = useCommand;
		this.triggerAfter = triggerAfter;
		this.command = command;
	}
}
