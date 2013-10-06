package nl.taico.tekkitrestrict.config;

import java.util.ArrayList;

public class DisableItemsConfig extends TRConfig {
	public static ArrayList<String> defaultContents(boolean extra){
		ArrayList<String> tbr = new ArrayList<String>();
		
		tbr.add("#########################################################################");
		tbr.add("## Configuration file for TekkitRestrict                            	##");
		tbr.add("## Authors: Taeir, DreadEnd (aka DreadSlicer)                       	##");
		tbr.add("## BukkitDev: http://dev.bukkit.org/server-mods/tekkit-restrict/    	##");
		tbr.add("## Please ask questions/report issues on the BukkitDev page.        	##");
		tbr.add("#########################################################################");
		tbr.add("");
		tbr.add("#########################################################################");
		tbr.add("####################### DisableItems Configuration ######################");
		tbr.add("#########################################################################");
		tbr.add("");
		tbr.add("# Should disabledItemBlocks be removed from the map?");
		tbr.add("# WARNING: It can cause lag as the complete map has to be searched for");
		tbr.add("#          disabled blocks.");
		tbr.add("# Default: false");
		tbr.add("RemoveDisabledItemBlocks: false");
		if (extra) tbr.add("#:-;-:# RemoveDisabledItemBlocks");
		tbr.add("");
		tbr.add("# When a disabled item is found in someone's inventory, it is changed");
		tbr.add("# into this item ID.");
		tbr.add("#");
		tbr.add("# Default: 3 (dirt)");
		tbr.add("ChangeDisabledItemsIntoId: 3");
		if (extra) tbr.add("#:-;-:# ChangeDisabledItemsIntoId");
		tbr.add("");
		tbr.add("# All Items listed below will be \"disabled\". This means that if a");
		tbr.add("# player does not have the bypass permission");
		tbr.add("# (tekkitrestrict.bypass.noitem), any item listed here will be");
		tbr.add("# uncraftable for him. If he has an item listed here in his inventory,");
		tbr.add("# it will be changed to the item specified by");
		tbr.add("# ChangeDisabledItemsIntoId (default dirt).");
		tbr.add("#");
		tbr.add("# You can also use individual permissions to add bans to some players");
		tbr.add("# only. The individual permission is: tekkitrestrict.noitem.ID.DATA");
		tbr.add("#");
		tbr.add("# Examples:");
		tbr.add("#- EE");
		tbr.add("#- RedPowerCore");
		tbr.add("#- RedPowerControl");
		tbr.add("#- RedPowerLogic");
		tbr.add("#- RedPowerMachine");
		tbr.add("#- RedPowerLighting");
		tbr.add("#- WirelessRedstone");
		tbr.add("#- BuildCraft");
		tbr.add("#- AdditionalPipes");
		tbr.add("#- AdvancedMachines");
		tbr.add("#- IndustrialCraft");
		tbr.add("#- NuclearControl");
		tbr.add("#- CompactSolars");
		tbr.add("#- ChargingBench");
		tbr.add("#- PowerConverters");
		tbr.add("#- Mffs");
		tbr.add("#- RailCraft");
		tbr.add("#- TubeStuffs (Buffer, AutoCraftTableII, BlackHoleChest, Incinerator. Duplicator, Retrievulator)");
		tbr.add("#- IronChests");
		tbr.add("#- BalkonWeaponMod");
		tbr.add("#- EnderChest (EnderChest, EnderPouch)");
		tbr.add("#- ChunkLoaders");
		tbr.add("#- \"200-3000\"");
		tbr.add("#- 246");
		tbr.add("#- \"150:3\"");
		tbr.add("DisableItems: []");
		if (extra) tbr.add("#:-;-:# DisableItems");
		tbr.add("");
		tbr.add("#########################################################################");
		
		return tbr;
	}
	
	public static void upgradeFile(){
		upgradeFile("DisableItems", convertDefaults(defaultContents(true)));
	}
}
