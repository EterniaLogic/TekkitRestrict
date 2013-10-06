package nl.taico.tekkitrestrict.config;

import java.util.ArrayList;

import com.github.dreadslicer.tekkitrestrict.Log.Warning;

public class GroupPermissionsConfig extends TRConfig {
	public static ArrayList<String> defaultContents(boolean extra){
		ArrayList<String> tbr = new ArrayList<String>();
		
		tbr.add("##############################################################################################");
		tbr.add("## Configuration file for TekkitRestrict                                                    ##");
		tbr.add("## Authors: Taeir, DreadEnd (aka DreadSlicer)                                               ##");
		tbr.add("## BukkitDev: http://dev.bukkit.org/server-mods/tekkit-restrict/                            ##");
		tbr.add("## Please ask questions/report issues on the BukkitDev page.                                ##");
		tbr.add("##############################################################################################");
		tbr.add("");
		tbr.add("##############################################################################################");
		tbr.add("############################## Group Permissions Configuration ###############################");
		tbr.add("##############################################################################################");
		tbr.add("# PermissionGroups");
		tbr.add("# For more information, see: ");
		tbr.add("# http://dev.bukkit.org/bukkit-plugins/tekkit-restrict/pages/configuration/group-permisisons/");
		tbr.add("#");
		tbr.add("# Here you can add permission groups!");
		tbr.add("#");
		tbr.add("# You can reference items like:");
		tbr.add("# \"10\" (Simple ID) ");
		tbr.add("# \"10:0\" (ID with the only block that corresponds to data type \"0\") ");
		tbr.add("# \"10-20\" (Range of IDs that can be any number in between 10 and 20)");
		tbr.add("#");
		tbr.add("# The permissions will be:");
		tbr.add("# tekkitrestrict.noitem.name or tekkitrestrict.creative.name");
		tbr.add("#");
		tbr.add("# Example:");
		tbr.add("#   groupname: \"12;10:1;13;15-17\"");
		tbr.add("#");
		tbr.add("# If you now give the permission tekkitrestrict.noitem.groupname, the items");
		tbr.add("# 12, 10:1, 13, 15, 16 and 17 will be banned items for that player.");
		tbr.add("# If you give the permission tekkitrestrict.creative.groupname, those items will only be");
		tbr.add("# banned if the player is in creative mode.");
		tbr.add("#");
		tbr.add("PermissionGroups:");
		tbr.add("    default: \"\"");
		tbr.add("");
		tbr.add("##############################################################################################");
		
		return tbr;
	}
	
	@Deprecated
	public static void upgradeFile(){
		upgradeFile("GroupPermissions", convertDefaults(defaultContents(true)));
		Warning.loadWarnings.add("Unable to copy old GroupPermissions into new format! Please do this yourself!");
	}
}
