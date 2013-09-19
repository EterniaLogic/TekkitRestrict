package com.github.dreadslicer.tekkitrestrict;

import java.util.List;

import org.bukkit.entity.Player;

import com.github.dreadslicer.tekkitrestrict.objects.BannedItem;
import com.github.dreadslicer.tekkitrestrict.objects.TREnums.ConfigFile;

public class TRBans {
	/**
	 * Meant for noitem, not for the limiter.
	 * @return
	 */
	public static boolean isItemBanned(Player player, String permType, int id, short data){
		if (id == 0 || player.hasPermission("tekkitrestrict.bypass." + permType)) {
			return false;
		}
		
		//if (TRPermHandler.hasPermission(player, permType, "*")) return true; //tekkitrestrict.noitem.*
		if (player.hasPermission("tekkitrestrict." + permType + "." + id + "." + data)) return true; //tekkitrestrict.noitem.10.1
		if (player.hasPermission("tekkitrestrict." + permType + "." + id)) return true; //tekkitrestrict.noitem.10
		if (TRPermHandler.hasPermission(player, permType, "" + id, "*")) return true; //tekkitrestrict.noitem.10.*
		
		String group = Util.inGroup(id);
		if (group != null){
			if (player.hasPermission("tekkitrestrict." + permType + "." + group)) return true;
		}
		
		return false;
	}
	
	public static void LoadConfig(){
		List<String> Bans = tekkitrestrict.config.getStringList(ConfigFile.DisableItems, "DisableItems");
		int i = 0;
		for (String current : Bans){
			String[] temp = current.split(":");
			
			if (!temp[0].matches("\\d+")){
				Log.Warning.config("Invalid value in DisableItems: \""+current+"\"!");
				continue;
			}
			
			int id = Integer.parseInt(temp[0]);
			String range = temp.length == 1 ? "*" : temp[1];
			
			if (!valid(range, id, "DisableItems")) continue;
			
			new BannedItem(id, range);
			
			i++;
		}
		Log.Config.Loaded("Banned Items", i);
	}
	
	private static boolean valid(String range, int key, String type){
		if (range.equals("*")) return true;
		else if (range.contains(",")){
			String temp[] = range.split(",");
			for (String current : temp){
				if (current.contains("-")){
					String temp2[] = current.split("-");
					if (!temp2[0].matches("\\d+") || !temp2[1].matches("\\d+")){
						Log.Warning.config("The data range of '"+key+"' in "+type+" is invalid!");
						return false;
					}
				} else {
					if (!current.matches("\\d+")){
						Log.Warning.config("The data range of '"+key+"' in "+type+" is invalid!");
						return false;
					}
				}
			}
		} else if (range.contains("-")){//No comma's, only a - so just 1 split needed.
			String temp[] = range.split("-");
			if (!temp[0].matches("\\d+") || !temp[1].matches("\\d+")){
				Log.Warning.config("The data range of '"+key+"' in "+type+" is invalid!");
				return false;
			}
		} else { //Only 1 value. 
			if (!range.matches("\\d+")){
				Log.Warning.config("The data range of '"+key+"' in "+type+" is invalid!");
				return false;
			}
		}
		return true;
	}
	
	static boolean valid(String range, String key, String type){
		if (range.equals("*")) return true;
		else if (range.contains(",")){
			String temp[] = range.split(",");
			for (String current : temp){
				if (current.contains("-")){
					String temp2[] = current.split("-");
					if (!temp2[0].matches("\\d+") || !temp2[1].matches("\\d+")){
						Log.Warning.config("The data range of '"+key+"' in "+type+" is invalid!");
						return false;
					}
				} else {
					if (!current.matches("\\d+")){
						Log.Warning.config("The data range of '"+key+"' in "+type+" is invalid!");
						return false;
					}
				}
			}
		} else if (range.contains("-")){//No comma's, only a - so just 1 split needed.
			String temp[] = range.split("-");
			if (!temp[0].matches("\\d+") || !temp[1].matches("\\d+")){
				Log.Warning.config("The data range of '"+key+"' in "+type+" is invalid!");
				return false;
			}
		} else { //Only 1 value. 
			if (!range.matches("\\d+")){
				Log.Warning.config("The data range of '"+key+"' in "+type+" is invalid!");
				return false;
			}
		}
		return true;
	}
}
