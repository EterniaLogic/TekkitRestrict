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
		tbr.add("# All Items listed below will be \"disabled\". This means that if a player does not have");
		tbr.add("# the bypass permission (tekkitrestrict.bypass.noitem), any item listed here will be");
		tbr.add("# uncraftable for him. If he has an item listed here in his inventory, it will be changed");
		tbr.add("# to the item specified by ChangeDisabledItemsIntoId (default dirt), and he will be");
		tbr.add("# informed with the message you set, or a default message.");
		tbr.add("#");
		tbr.add("# You can also use individual permissions to add bans to some players only.");
		tbr.add("# The individual permission is: tekkitrestrict.noitem.ID.DATA");
		tbr.add("#");
		tbr.add("# There are multiple ways to add items to this list:");
		tbr.add("# 1. You can use single id's:");
		tbr.add("# - \"12\"");
		tbr.add("# - \"13:5\"");
		tbr.add("# 2. You can use ranges(*1):");
		tbr.add("# - \"20-30\"");
		tbr.add("# - \"30-45:5\"(*2)");
		tbr.add("# 3. You can use EE and IC2 item names(*3) (without spaces):");
		tbr.add("# - \"RedMatterPickaxe\"");
		tbr.add("# - \"Jetpack\"");
		tbr.add("# 4. You can use preset groups (NOT caseSENSItive):");
		tbr.add("# - \"ee\"");
		tbr.add("# - \"buildcraft\"");
		tbr.add("# 5. You can also use groups you made yourself in the GroupPermissions config.");
		tbr.add("#");
		tbr.add("# You can also set the message that is shown to a player when he tries to craft or click");
		tbr.add("# on a banned item like so:");
		tbr.add("# - \"50 {We don't like torches on this server...}\"");
		tbr.add("# - \"EE {Equivalent Exchange is too overpowered, so it is banned!}\"");
		tbr.add("# - \"20-30 {Items 20 to 30 are banned!}\"");
		tbr.add("#");
		tbr.add("# You can add colours(*4) and styling(*5) to these messages:");
		tbr.add("# - \"EE {&0&n(Black Underlined)NO EE!}\"");
		tbr.add("#");
		tbr.add("# You can add line breaks with \n:");
		tbr.add("# - \"50 {We don't like torches on this server...\nSo you are not allowed to have them!}\"");
		tbr.add("#");
		tbr.add("# These are all preset Groups (NOT caseSENSItive):");
		tbr.add("# EE, RedPowerCore, RedPowerControl, RedPowerLogic, RedPowerMachine,");
		tbr.add("# RedPowerLighting, WirelessRedstone, BuildCraft, AdditionalPipes,");
		tbr.add("# IronChests, IndustrialCraft, IC2, NuclearControl, CompactSolars,");
		tbr.add("# ChargingBench, PowerConverters, MFFS, RailCraft, TubeStuff,");
		tbr.add("# AdvancedMachines, WeaponMod, EnderChest and ChunkLoaders");
		tbr.add("#");
		tbr.add("# *1: Ranges are inclusive, 20-22 means items 20, 21 and 22.");
		tbr.add("# *2: 15-17:10 means items 15:10, 16:10 and 17:10.");
		tbr.add("# *3: Not all items names are included. You will be informed in the console when you add");
		tbr.add("#     an item that is not known.");
		tbr.add("#     You can also use /tr warnings config to view these warnings.");
		tbr.add("# *4: You can add colours with &0 to &9 and &a to &f.");
		tbr.add("# *5: You can add styling with &k to &o. &r will reset all styling and colours.");
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
