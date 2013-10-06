package nl.taico.tekkitrestrict.config;

import java.io.File;
import java.util.ArrayList;

public class GeneralConfig extends TRConfig {
	public static String s = File.separator;
	public static ArrayList<String> defaultContents(boolean extra){
		ArrayList<String> tbr = new ArrayList<String>();
		tbr.add("######################################################################");
		tbr.add("## Configuration file for TekkitRestrict                            ##");
		tbr.add("## Authors: Taeir, DreadEnd (aka DreadSlicer)                       ##");
		tbr.add("## BukkitDev: http://dev.bukkit.org/server-mods/tekkit-restrict/    ##");
		tbr.add("## Please ask questions/report issues on the BukkitDev page.        ##");
		tbr.add("######################################################################");
		tbr.add("");
		tbr.add("######################################################################");
		tbr.add("## Some fast notes for you all. First, every option in this file is ##");
		tbr.add("## Reloadable. That means that by typing \"/tr admin reload\" in-game ##");
		tbr.add("## (with the \"tekkitrestrict.admin.reload\" permission), or by       ##");
		tbr.add("## typing \"tr admin reload\" in the console, you can reload every    ##");
		tbr.add("## single config change in this file.                               ##");
		tbr.add("##                                                                  ##");
		tbr.add("## Have fun, Play safe. Good luck.                                  ##");
		tbr.add("## * DreadEnd * out.                                                ##");
		tbr.add("######################################################################");
		tbr.add("# Patch ComputerCraft once to prevent some server crashes with");
		tbr.add("# ComputerCraft computers.");
		tbr.add("# Default: true");
		tbr.add("PatchComputerCraft: true");
		if (extra) tbr.add("#:-;-:# PatchComputerCraft");
		tbr.add("");
		tbr.add("UseAutoRPTimer: true");
		if (extra) tbr.add("#:-;-:# UseAutoRPTimer");
		tbr.add("UseItemLimiter: true");
		if (extra) tbr.add("#:-;-:# UseItemLimiter");
		tbr.add("UseLimitedCreative: true");
		if (extra) tbr.add("#:-;-:# UseLimitedCreative");
		tbr.add("# If UseOpenAlc is false, the command /openalc will be disabled.");
		tbr.add("# Default: true");
		tbr.add("UseOpenAlc: true");
		if (extra) tbr.add("#:-;-:# UseOpenAlc");
		tbr.add("");
		tbr.add("# Should NoItem (Bans items) be enabled?");
		tbr.add("# Default: true");
		tbr.add("UseNoItem: true");
		if (extra) tbr.add("#:-;-:# UseNoItem");
		tbr.add("");
		tbr.add("# If KickFromConsole is true, the console will execute the /kick");
		tbr.add("# command instead of the default Bukkit kick when a player gets");
		tbr.add("# kicked by TekkitRestrict (e.g. for hacking).");
		tbr.add("# Useful if you want to log kicks or use a different bansystem.");
		tbr.add("# Default: false");
		tbr.add("KickFromConsole: false");
		if (extra) tbr.add("#:-;-:# KickFromConsole");
		tbr.add("");
		tbr.add("######################################################################");
		tbr.add("############################## Updater ###############################");
		tbr.add("# Should TekkitRestrict check for an update when the server starts?");
		tbr.add("# Default: true");
		tbr.add("CheckForUpdateOnStartup: true");
		if (extra) tbr.add("#:-;-:# CheckForUpdateOnStartup");
		tbr.add("");
		tbr.add("# Should the update be downloaded automatically if there is one?");
		tbr.add("# Default: true");
		tbr.add("Auto-Update: true");
		if (extra) tbr.add("#:-;-:# Auto-Update");
		tbr.add("");
		tbr.add("# Should TekkitRestrict update to beta versions?");
		tbr.add("# If you are currently running a beta version, this option is ignored.");
		tbr.add("# Default: false");
		tbr.add("UpdateToBetaVersions: false");
		if (extra) tbr.add("#:-;-:# UpdateToBetaVersions");
		tbr.add("");
		tbr.add("# Should TekkitRestrict update to development versions?");
		tbr.add("# If you are currently running a dev version, this option is ignored.");
		tbr.add("# Default: false");
		tbr.add("UpdateToDevelopmentVersions: false");
		if (extra) tbr.add("#:-;-:# UpdateToDevelopmentVersions");
		tbr.add("");
		tbr.add("######################################################################");
		tbr.add("######################################################################");
		tbr.add("# Do NOT change this. It will reset the config files if you do.");
		tbr.add("ConfigVersion: 1.5");
		tbr.add("");
		tbr.add("######################################################################");
		return tbr;
	}
	
	public static void upgradeFile(){
		upgradeFile("General", convertDefaults(defaultContents(true)));
	}
}
