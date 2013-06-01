package com.github.dreadslicer.tekkitrestrict.objects;

import java.util.ArrayList;

import com.github.dreadslicer.tekkitrestrict.Log;

public class BannedItem {
	public static ArrayList<BannedItem> configBannedItems = new ArrayList<BannedItem>();
	public int id;
	private String range;
	/**
	 * Creates a new BannedItem and adds it to the configBanItems.
	 * @param id
	 * @param range
	 * @param permission
	 */
	public BannedItem(int id, String range){
		this.id = id;
		this.range = range;
		configBannedItems.add(this);
	}

	/**
	 * Loops through all configBanItems and if the id and data match, returns that one.<br>
	 * Otherwise returns null.
	 * @param id
	 * @param data
	 * @return A ConfigBannedItem or null if not found.
	 */
	public static BannedItem getConfigBan(int id, int data){
		for (BannedItem current : configBannedItems){
			if (current.id == id && current.isInRange(data)) return current;
		}
		return null;
	}
	
	/**
	 * Uses getConfigBan. If it finds a configBanItem, it returns true.<br>
	 * Otherwise returns false.
	 * @param id
	 * @param data
	 * @return True if a configBanItem is found for this value, false otherwise.
	 * @see #getConfigBan(int, int)
	 */
	public static boolean isBanned(int id, int data){
		if (getConfigBan(id, data) != null) return true;
		return false;
	}
	
	public boolean isInRange(int value){
		try {
			if (range.equals("*")) return true;
			else if (range.contains(",")){
				String temp[] = range.split(",");
				for (String current : temp){
					if (!current.contains("-")){
						if (Integer.parseInt(current) == value) return true;
					} else {
						String v[] = current.split("-");
						if (value>=Integer.parseInt(v[0]) && value<=Integer.parseInt(v[1])) return true;
					}
				}
				return false;
			}
			else if (!range.contains("-")) return value == Integer.parseInt(range);
			String v[] = range.split("-");
			if (value>=Integer.parseInt(v[0]) && value<=Integer.parseInt(v[1])) return true;
			return false;
		} catch (NumberFormatException ex){
			//Log.Config.Warning("One of the ranges set in the bannedblocks config is invalid!");
			Log.Debug("NumberFormatException in isInRange. precheck apparently fails");
			return false;
		}
	}
}
