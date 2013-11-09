package com.github.dreadslicer.tekkitrestrict;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import nl.taico.tekkitrestrict.objects.TRItem;
import nl.taico.tekkitrestrict.objects.TREnums.ConfigFile;

import com.github.dreadslicer.tekkitrestrict.Log.Warning;
import com.github.dreadslicer.tekkitrestrict.TRConfigCache.Listeners;

/**
 * This class defines and enables the use of determining what items
 * the user has "specified" to be banned.
 */
public class TRNoItem {
	/** A list of all the (by config) banned items. */
	public static LinkedList<TRItem> DisabledItems = new LinkedList<TRItem>();
	/** A list of all the (by config) banned creative items. */
	private static LinkedList<TRItem> DisabledCreativeItems = new LinkedList<TRItem>();
	
	/**
	 * Uses allocateDisabledItems() and allocateDisabledCreativeItems()<br>
	 * Also rechecks if TekkitRestrict should use noItem and/or LimitedCreative<br>
	 * Also clears all currently banned items.
	 * 
	 * @see #allocateDisabledCreativeItems()
	 * @see #allocateDisabledItems()
	 */
	public static void reload() {
		DisabledItems.clear();
		DisabledCreativeItems.clear();
		allocateDisabledItems();
		allocateDisabledCreativeItems();
	}
	
	/**
	 * Loads the Disabled Items from the config.
	 */
	private static void allocateDisabledItems() {
		List<String> di = tekkitrestrict.config.getStringList(ConfigFile.DisableItems, "DisableItems");
		for (String str : di) {
			try {
				DisabledItems.addAll(TRItemProcessor.processItemString(str));
			} catch (TRException ex) {
				Warning.config("You have an error in your DisableItems.config.yml in DisableItems:");
				Warning.config(ex.getMessage());
				continue;
			}
		}
	}
	private static void allocateDisabledCreativeItems() {
		List<String> di = tekkitrestrict.config.getStringList(ConfigFile.LimitedCreative, "LimitedCreative");
		for (String str : di) {
			try {
				DisabledCreativeItems.addAll(TRItemProcessor.processItemString(str));
			} catch (TRException ex) {
				Warning.config("You have an error in your LimitedCreative.config.yml in LimitedCreative:");
				Warning.config(ex.getMessage());
				continue;
			}	
		}
	}
	
	/**
	 * Goes through all banned items and checks if the id and data match.
	 * 
	 * @return If the given block is a disabled item (set in the config).
	 */
	public static boolean isBlockBanned(Block block) {
		if (!Listeners.UseNoItem) return false;
		
		int id = block.getTypeId();
		byte data = block.getData();
		for (TRItem bannedItem : DisabledItems) {
			if (bannedItem.compare(id, data)) return true;
		}
		
		return false;
	}
	
	/**
	 * Note: It checks this player's permissions for tekkitrestrict.noitem.id[.data]
	 * and, if doBypassCheck is true, for tekkitrestrict.bypass.noitem
	 * 
	 * @return If the given id:data combination is banned for this player.
	 * 
	 * @see #isItemBanned(Player, int, int, boolean) Same as isItemBanned(player, id, 0, doBypassCheck)
	 */
	public static String isItemBanned(Player player, int id, boolean doBypassCheck) {
		return isItemBanned(player, id, 0, doBypassCheck);
	}
	/**
	 * Note: It checks this player's permissions for tekkitrestrict.noitem.id[.data]
	 * and, if doBypassCheck is true, for tekkitrestrict.bypass.noitem
	 * 
	 * @return If the given id:data combination is banned for this player.
	 */
	public static String isItemBanned(Player player, int id, int data, boolean doBypassCheck) {
		return Listeners.UseNoItem ? isTypeNoItemBanned(player, id, data, doBypassCheck) : null;
	}
	
	/**
	 * Goes through all banned items and checks if the id and data match.
	 * 
	 * @return If the given item/block is banned in the config.
	 */
	public static String isItemGloballyBanned(int id, int data) {
		if (!Listeners.UseNoItem) return null;
		
		for (TRItem bannedItem : DisabledItems) {
			if (bannedItem.compare(id, data)) return bannedItem.msg == null ? "" : bannedItem.msg;
		}
		
		return null;
	}

	/**
	 * Note: It checks this player's permissions for tekkitrestrict.creative.id[.data]
	 * and, if doBypassCheck is true, for tekkitrestrict.bypass.creative
	 * 
	 * @return If the given id:data combination is banned for this player when he/she is
	 * in creative mode.
	 */
	public static String isItemBannedInCreative(Player player, int id, int data, boolean doBypassCheck) {
		return Listeners.UseLimitedCreative ? isTypeCreativeBanned(player, id, data, doBypassCheck) : null;
	}

	private static String isTypeCreativeBanned(Player player, int id, int data, boolean doBypassCheck) {
		if (id < 8) return null;
		if (doBypassCheck && player.hasPermission("tekkitrestrict.bypass.creative")) return null;
		
		if (DisabledCreativeItems != null) {
			for (TRItem cc : DisabledCreativeItems){
				if (cc.compare(id, data)) return cc.msg == null ? "" : cc.msg;
			}
		}
		
		if (player.hasPermission("tekkitrestrict.creative.blockall")) return "";

		//TRCacheItem ci1 = TRCacheItem.getPermCacheItem(player, "c", "creative", id, data, false);
		//if (ci1 != null) return true;
		
		String idStr = "tekkitrestrict.creative."+id;
		
		if (player.hasPermission(idStr+"."+data)) return "";
		else if (player.hasPermission(idStr)) return "";
		else {
			Iterator<String> keys = TRItemProcessor.groups.keySet().iterator();
			while (keys.hasNext()) {
				String key = keys.next();
				if (player.hasPermission("tekkitrestrict.creative."+key)) {
					List<TRItem> mi = TRItemProcessor.groups.get(key);
					for(TRItem c:mi){
						if (c == null) continue;
						if (c.compare(id, data)) return "";
					}
				}
			}
		}
		
		return null;
	}
	
	private static String isTypeNoItemBanned(Player player, int id, int data, boolean doBypassCheck) {
		if (id < 8) return null;
		if (doBypassCheck && player.hasPermission("tekkitrestrict.bypass.noitem")) return null;

		if (DisabledItems != null) {
			for (TRItem cc : DisabledItems){
				if (cc.compare(id, data)) return cc.msg == null ? "" : cc.msg;
			}
		}
		
		if (player.hasPermission("tekkitrestrict.noitem.blockall")) return "";
		
		//TRCacheItem ci1 = TRCacheItem.getPermCacheItem(player, "n", "noitem", id, data, false);//Perms and cache??
		//if (ci1 != null) return true;
		
		String idStr = "tekkitrestrict.noitem."+id;
		
		if (player.hasPermission(idStr+"."+data)) return "";
		else if (player.hasPermission(idStr)) return "";
		else {
			Iterator<String> keys = TRItemProcessor.groups.keySet().iterator();
			while (keys.hasNext()) {
				String key = keys.next();
				if (player.hasPermission("tekkitrestrict.noitem."+key)) {
					List<TRItem> mi = TRItemProcessor.groups.get(key);
					for(TRItem c:mi){
						if (c == null) continue;
						if (c.compare(id, data)) return "";
					}
				}
			}
		}
		
		return null;
	}

	/*
	public static TRItem[] gettRangedItemValues(String ins) {
		String insx = ins.replace(":-", ":=");
		// ranged values may start from 1-100
		// they may also just be 1.
		// They can also have a data value 1:4
		List<TRItem> r = new LinkedList<TRItem>();

		// now, lets determine the type that we are using.
		// ranges generally have a "-" in the middle.
		if (insx.contains("-")) { // a range of items
			// loop through this range and add each to the return stack.
			int data = -1;
			if (insx.contains(":")) {
				String dataStr = insx.split(":")[1];
				if (dataStr.equals("*")) data = -1;
				else {
					try {
						data = Integer.parseInt(dataStr.replace("=", "-"));
					} catch (NumberFormatException ex){
						return r.toArray(new TRItem[0]);
					}
				}
				insx = insx.split(":")[0];
			}
			
			String[] t = insx.split("-");
			int from = Integer.parseInt(t[0]);
			int to = Integer.parseInt(t[1]);

			for (int i = from; i <= to; i++) {
				r.add(TRItem.parseItem(i, data));
			}
		} else if (insx.contains(":")) { // A single item with a datatype
			String[] t = insx.split(":");
			int id, data;
			try {
				id = Integer.parseInt(t[0]);
				data = Integer.parseInt(t[1].replace('=', '-'));
			} catch (NumberFormatException ex){
				return r.toArray(new TRItem[0]);
			}
			if (data == 0) data = -10;// tekkitrestrict.log.info(id+":::"+data);}
			
			r.add(TRItem.parseItem(id, data));
			
		} else { // Just a single item
			int id;
			try {
				id = Integer.parseInt(insx);
			} catch (Exception ex) {
				return r.toArray(new TRItem[0]);
			}
			r.add(TRItem.parseItem(id, -1));
		}

		TRItem[] isz = r.toArray(new TRItem[0]);
		//r.clear();
		return isz;
	}*/

	/*
	public static boolean isInRanged(String ins, int id, int data) {
		TRItem[] range = gettRangedItemValues(ins);

		for (TRItem g : range) {
			if (equalSet(g.id, g.data, id, data)) {
				return true;
			}
		}

		return false;
	}*/
	
	/** Used to report the total amount of banned items to the metrics. */
	public static int getBannedItemsAmount() {
		return DisabledItems.size();
	}

	/** @return True if: id1 == id2 and (data1 = 0 or data1 == data2 or (data1 = -10 and data2 = 0)) */
	public static boolean equalSet(int id1, int data1, int id2, int data2) {
		if (id1 != id2) return false;
		if (data1 == -1 || data1 == data2 || (data1 == -10 && data2 == 0)) {
			return true;
		}
		return false;
	}
	
	/** @return True if: id1 == id2 and (data1 = -1 or data1 == data2) */
	public static boolean equalSet2(int id1, int data1, int id2, int data2) {
		if (id1 != id2) return false;
		if (data1 == -1 || data1 == data2) {
			return true;
		}
		return false;
	}

	public static ArrayList<String> getDebugInfo(){
		ArrayList<String> tbr = new ArrayList<String>();
		for (TRItem TRCI : DisabledItems){
			tbr.add("N:"+TRCI.id+":"+TRCI.data);
		}
		for (TRItem TRCI : DisabledCreativeItems){
			tbr.add("C:"+TRCI.id+":"+TRCI.data);
		}
		
		return tbr;
	}

}

