package nl.taico.tekkitrestrict.config;

import java.util.ArrayList;

public class LimiterConfig extends TRConfig {
	public static ArrayList<String> defaultContents(boolean extra){
		ArrayList<String> tbr = new ArrayList<String>(30);
		tbr.add("##############################################################################");
		tbr.add("## Configuration file for TekkitRestrict                                    ##");
		tbr.add("## Authors: Taeir, DreadEnd (aka DreadSlicer)                               ##");
		tbr.add("## BukkitDev: http://dev.bukkit.org/server-mods/tekkit-restrict/            ##");
		tbr.add("## Please ask questions/report issues on the BukkitDev page.                ##");
		tbr.add("##############################################################################");
		tbr.add("");
		tbr.add("##############################################################################");
		tbr.add("############################## Limiter Functions #############################");
		tbr.add("##############################################################################");
		tbr.add("");
		tbr.add("# Limits the number of blocks a player can place. (Global)");
		tbr.add("# Please note that these cannot be changed in-game.");
		tbr.add("# ID Limit (Please use ONE space between the ID and limit to separate them)");
		tbr.add("# Examples:");
		tbr.add("#- \"153 1\"");
		tbr.add("#- \"100-200 1\"");
		tbr.add("#- \"52:55 1\"");
		tbr.add("#- \"227 3\"");
		tbr.add("LimitBlocks: []");//20
		if (extra) tbr.add("#:-;-:# LimitBlocks");//21
		tbr.add("");
		tbr.add("##############################################################################");
		tbr.add("##############################################################################");
		tbr.add("##############################################################################");
		
		return tbr;
	}
	
	public static void upgradeFile(){
		upgradeFile("Limiter", convertDefaults(defaultContents(true)));
	}
}