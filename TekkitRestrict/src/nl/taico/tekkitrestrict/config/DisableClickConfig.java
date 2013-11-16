package nl.taico.tekkitrestrict.config;

import java.util.ArrayList;

public class DisableClickConfig extends TRConfig {
	public static ArrayList<String> defaultContents(boolean extra){
		ArrayList<String> tbr = new ArrayList<String>(60);
		
		tbr.add("##########################################################################################");
		tbr.add("## Configuration file for TekkitRestrict                                                ##");
		tbr.add("## Authors: Taeir, DreadEnd (aka DreadSlicer)                                           ##");
		tbr.add("## BukkitDev: http://dev.bukkit.org/server-mods/tekkit-restrict/                        ##");
		tbr.add("## Please ask questions/report issues on the BukkitDev page.                            ##");
		tbr.add("##########################################################################################");
		tbr.add("");
		tbr.add("##########################################################################################");
		tbr.add("############################### DisableClick Configuration ###############################");
		tbr.add("##########################################################################################");
		tbr.add("# UseNoClickPermissions");
		tbr.add("# If you enable this, TekkitRestrict will check on every click action if the player that");
		tbr.add("# clicked has the permission \"tekkitrestrict.noclick.id.data[.left|right|trample]\"");
		tbr.add("# As this thus checks up to 2 permissions for each click action, it might cause some lag.");
		tbr.add("");
		tbr.add("# Default: false");
		tbr.add("UseNoClickPermissions: false");
		if (extra) tbr.add("#:-;-:# UseNoClickPermissions");
		tbr.add("");
		tbr.add("###################### Disable the left or right click of the item. ######################");
		tbr.add("#");
		tbr.add("#- \"27562\"                          All Item Data Types, Left and Right Clicking");
		tbr.add("#- \"27562 left\"                     All Item Data Types, Left clicking");
		tbr.add("#- \"27562-27566 left\"               All Items in range, Left clicking");
		tbr.add("#- \"27562:1 right\"                  Prevent right click (in the air and on blocks)");
		tbr.add("#- \"27562:1 both\"                   Prevent left and right click");
		tbr.add("#                                   (in the air and on blocks)");
		tbr.add("#- \"27562:1 trample\"                Prevent trampling while holding this item");
		tbr.add("#- \"27562:1 all\"                    Prevent clicking and trampling with this item");
		tbr.add("#                                   (in the air and on blocks)");
		tbr.add("#- \"27562:1 left safezone\"          Prevent left-clicking with this item in a safezone");
		tbr.add("#                                   (in the air and on blocks)");
		tbr.add("#- \"27562:1 safezone\"               Prevent clicking with this item in a safezone");
		tbr.add("#                                   (in the air and on blocks)");
		tbr.add("#- \"27562:1 right air\"              Prevent right-clicking with this item in the air");
		tbr.add("#- \"27562:1 both block\"             Prevent clicking with this item on a block");
		tbr.add("#- \"27562:1 all air safezone\"       Prevents clicking and trampling in the air");
		tbr.add("#                                   in a safezone.");
		tbr.add("#- \"27562-27566 all block safezone\" Prevents clicking and trampling on blocks");
		tbr.add("#                                   in a safezone with a range of items");
		tbr.add("#- \"ee left\"					    Prevents EE items from being right-clicked");
		tbr.add("#");
		tbr.add("######################## Disable the GUI or Right-click on a block #######################");
		tbr.add("#");
		tbr.add("#- \"block 126:1\"    When you right-click on this block, it will be disallowed.");
		tbr.add("#- \"block 126-150\"  When you right-click on a block in this range, it will be disallowed.");
		tbr.add("#- \"block ee\"       When you right-click on any EE block, it will be disallowed.");
		tbr.add("");
		tbr.add("DisableClick: []");
		if (extra) tbr.add("#:-;-:# DisableClick");
		tbr.add("");
		tbr.add("##########################################################################################");
		
		return tbr;
	}
	
	public static void upgradeFile(){
		upgradeFile("DisableClick", convertDefaults(defaultContents(true)));
	}
}
