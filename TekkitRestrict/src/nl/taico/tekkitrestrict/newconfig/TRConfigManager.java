package nl.taico.tekkitrestrict.newconfig;

import nl.taico.tekkitrestrict.tekkitrestrict;

public class TRConfigManager {
	public SimpleConfig TRGeneralConfig;
	public TRConfigManager(){
		SimpleConfigManager manager = new SimpleConfigManager(tekkitrestrict.getInstance());
		String[] header = {
			"######################################################################", 
			"## Configuration file for TekkitRestrict                            ##", 
			"## Authors: Taeir, DreadEnd (aka DreadSlicer)                       ##", 
			"## BukkitDev: http://dev.bukkit.org/server-mods/tekkit-restrict/    ##", 
			"## Please ask questions/report issues on the BukkitDev page.        ##", 
			"######################################################################", 
			"", 
			"######################################################################", 
			"## Some fast notes for you all. First, every option in this file is ##", 
			"## Reloadable. That means that by typing \"/tr admin reload\" in-game ##", 
			"## (with the \"tekkitrestrict.admin.reload\" permission), or by       ##", 
			"## typing \"tr admin reload\" in the console, you can reload every    ##", 
			"## single config change in this file.                               ##", 
			"##                                                                  ##", 
			"## Have fun, Play safe. Good luck.                                  ##", 
			"## * DreadEnd * out.                                                ##", 
			"######################################################################"
		};
		TRGeneralConfig = manager.getNewConfig("General.config.yml", header);
		String[] comments = {
			"Patch ComputerCraft once to prevent some server crashes with",
			"ComputerCraft computers.",
			"Default: true"
		};
		TRGeneralConfig.set("PatchComputerCraft", true, comments);
		
		comments = new String[] {
			"Change the lowest possible time for RedPower Timers to the value set",
			"in ModModifications.config.yml",
			"Default: true"
		};
		TRGeneralConfig.set("UseAutoRPTimer", true, comments);
		TRGeneralConfig.saveConfig();
	}
}
