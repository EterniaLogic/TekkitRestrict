package nl.taico.tekkitrestrict.config;

import java.util.ArrayList;

public class AdvancedConfig extends TRConfig {
	public static ArrayList<String> defaultContents(boolean extra){
		ArrayList<String> tbr = new ArrayList<String>(80);
		tbr.add("##############################################################################");
		tbr.add("## Configuration file for TekkitRestrict                                    ##");
		tbr.add("## Authors: Taeir, DreadEnd (aka DreadSlicer)                               ##");
		tbr.add("## BukkitDev: http://dev.bukkit.org/server-mods/tekkit-restrict/            ##");
		tbr.add("## Please ask questions/report issues on the BukkitDev page.                ##");
		tbr.add("##############################################################################");
		tbr.add("");
		tbr.add("##############################################################################");
		tbr.add("############################# Advanced Functions #############################");
		tbr.add("##############################################################################");
		/*
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
		*/
		tbr.add("# All items listed here will be uncraftable. You can also use mod names to");
		tbr.add("# make all items of that mod uncraftable.");
		tbr.add("#");
		tbr.add("# Please note that removed recipes can NOT be re-added with /tr admin reload.");
		tbr.add("# In order to get them back you have to restart the server.");
		tbr.add("#");
		tbr.add("# Examples:");
		tbr.add("#RecipeBlock:");
		tbr.add("#- 27232");
		tbr.add("#- \"126:3\"");
		tbr.add("#- ee");
		tbr.add("RecipeBlock: []");
		if (extra) tbr.add("#:-;-:# RecipeBlock");//21
		tbr.add("");
		tbr.add("##############################################################################");
		tbr.add("# All items listed here will be unable to be smelted.");
		tbr.add("#");
		tbr.add("# For example, if you add 17 (log), you will be unable to smelt logs into");
		tbr.add("# charcoal.");
		tbr.add("#");
		tbr.add("# Please note that removed recipes can NOT be readded with /tr admin reload.");
		tbr.add("# In order to get them back you have to restart the server.");
		tbr.add("#");
		tbr.add("# Examples:");
		tbr.add("#RecipeFurnaceBlock:");
		tbr.add("#- 27232");
		tbr.add("#- \"126:3\"");
		tbr.add("RecipeFurnaceBlock: []");
		if (extra) tbr.add("#:-;-:# RecipeFurnaceBlock");
		tbr.add("");
		tbr.add("##############################################################################");
		tbr.add("# LWC Protection Extension for Tekkit");
		tbr.add("#");
		tbr.add("# All items listed here cannot be placed next to a lockette unless the player");
		tbr.add("# has access to it. This is useful for preventing players from \"pumping\"");
		tbr.add("# items out of chests, using block breakers to break chests, etc.");
		tbr.add("");
		tbr.add("# Default:");
		tbr.add("#LWCPreventNearLocked:");
		tbr.add("#- 4306");
		tbr.add("#- 4301");
		tbr.add("#- 150");
		tbr.add("#- 136");
		tbr.add("#- 166");
		tbr.add("LWCPreventNearLocked:");//0
		if (extra) tbr.add("#:-;-:# LWCPreventNearLocked 5");//1
		tbr.add("- 4306");//2/1
		tbr.add("- 4301");//3/2
		tbr.add("- 150");//4/3
		tbr.add("- 136");//5/4
		tbr.add("- 166");//6/5
		tbr.add("");
		tbr.add("##############################################################################");
		
		return tbr;
	}
	
	public static void upgradeFile(){
		upgradeFile("Advanced", convertDefaults(defaultContents(true)));
	}
}