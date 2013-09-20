package com.github.dreadslicer.tekkitrestrict;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.github.dreadslicer.tekkitrestrict.Log.Warning;
import com.github.dreadslicer.tekkitrestrict.TRConfigCache.Listeners;
import com.github.dreadslicer.tekkitrestrict.objects.TRItem;
import com.github.dreadslicer.tekkitrestrict.objects.TRItemStack;
import com.github.dreadslicer.tekkitrestrict.objects.TREnums.ConfigFile;

/**
 * This class defines and enables the use of determining what items
 * the user has "specified" to be banned.
 */
public class TRNoItem {
	/** A list of all the (by config) banned items. */
	private static LinkedList<TRItem> DisabledItems = new LinkedList<TRItem>();
	/** A list of all the (by config) banned creative items. */
	private static LinkedList<TRItem> DisabledCreativeItems = new LinkedList<TRItem>();
	
	

	/**	Clear all Lists and maps in this class (no items will be banned any more) */
	public static void clear() {
		DisabledItems.clear();
		DisabledCreativeItems.clear();
	}
	
	/**
	 * Uses allocateDisabledItems() and allocateDisabledCreativeItems()<br>
	 * Also rechecks if TekkitRestrict should use noItem and/or LimitedCreative
	 * 
	 * @see #allocateDisabledCreativeItems()
	 * @see #allocateDisabledItems()
	 */
	public static void reload() {
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
				DisabledItems.addAll(TRItemProcesser.processItemString(str));
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
				DisabledCreativeItems.addAll(TRItemProcesser.processItemString(str));
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
	public static boolean isItemBanned(Player player, int id, boolean doBypassCheck) {
		return isItemBanned(player, id, 0, doBypassCheck);
	}
	/**
	 * Note: It checks this player's permissions for tekkitrestrict.noitem.id[.data]
	 * and, if doBypassCheck is true, for tekkitrestrict.bypass.noitem
	 * 
	 * @return If the given id:data combination is banned for this player.
	 */
	public static boolean isItemBanned(Player player, int id, int data, boolean doBypassCheck) {
		return Listeners.UseNoItem ? isTypeNoItemBanned(player, id, data, doBypassCheck) : false;
	}
	/**
	 * Goes through all banned items and checks if the id and data match.
	 * 
	 * @return If the given item/block is banned in the config.
	 */
	public static boolean isItemGloballyBanned(int id, int data) {
		if (!Listeners.UseNoItem) return false;
		
		for (TRItem bannedItem : DisabledItems) {
			if (bannedItem.compare(id, data)) return true;
		}
		
		return false;
	}

	/**
	 * Note: It checks this player's permissions for tekkitrestrict.creative.id[.data]
	 * and, if doBypassCheck is true, for tekkitrestrict.bypass.creative
	 * 
	 * @return If the given id:data combination is banned for this player when he/she is
	 * in creative mode.
	 */
	public static boolean isItemBannedInCreative(Player player, int id, int data, boolean doBypassCheck) {
		return Listeners.UseLimitedCreative ? isTypeCreativeBanned(player, id, data, doBypassCheck) : false;
	}
	
	
	/*
	private static boolean isTypeBanned(String Type, String perm, List<TRCacheItem> tlist, Player player, int id, int data, boolean doBypassCheck) {
		if (doBypassCheck && player.hasPermission("tekkitrestrict.bypass."+perm)) return false;

		TRCacheItem ci1 = TRCacheItem.getPermCacheItem(player, Type, perm, id, data);
		if (ci1 != null) return true;
		
		String idStr = "tekkitrestrict."+perm+"."+id;
		if (player.hasPermission(idStr+"."+data))
			return true;
		else if (player.hasPermission(idStr))
			return true;
		else {

			Iterator<String> keys = modItemDat.keySet().iterator();
			while (keys.hasNext()) {
				String g = keys.next();
				if (TRPermHandler.hasPermission(player, perm, g)) {
					List<TRCacheItem> mi = modItemDat.get(g);
					for(TRCacheItem c:mi){
						if (c == null) continue;
						if (c.compare(id, data)) return true;
					}
				}
			}
		}
		if (tlist != null) {
			for (TRCacheItem cc : tlist){
				if (cc.compare(id, data)) return true;
			}
		}
		
		return false;
	}*/
	
	private static boolean isTypeCreativeBanned(Player player, int id, int data, boolean doBypassCheck) {
		if (id < 8) return false;
		if (doBypassCheck && player.hasPermission("tekkitrestrict.bypass.creative")) return false;
		
		if (DisabledCreativeItems != null) {
			for (TRItem cc : DisabledCreativeItems){
				if (cc.compare(id, data)) return true;
			}
		}
		
		if (player.hasPermission("tekkitrestrict.creative.blockall")) return true;

		//TRCacheItem ci1 = TRCacheItem.getPermCacheItem(player, "c", "creative", id, data, false);
		//if (ci1 != null) return true;
		
		String idStr = "tekkitrestrict.creative."+id;
		
		if (player.hasPermission(idStr+"."+data)) return true;
		else if (player.hasPermission(idStr)) return true;
		else {
			Iterator<String> keys = TRItemProcesser.groups.keySet().iterator();
			while (keys.hasNext()) {
				String key = keys.next();
				if (player.hasPermission("tekkitrestrict.creative."+key)) {
					List<TRItem> mi = TRItemProcesser.groups.get(key);
					for(TRItem c:mi){
						if (c == null) continue;
						if (c.compare(id, data)) return true;
					}
				}
			}
		}
		
		return false;
	}
	
	private static boolean isTypeNoItemBanned(Player player, int id, int data, boolean doBypassCheck) {
		if (id < 8) return false;
		if (doBypassCheck && player.hasPermission("tekkitrestrict.bypass.noitem")) return false;

		if (DisabledItems != null) {
			for (TRItem cc : DisabledItems){
				if (cc.compare(id, data)) return true;
			}
		}
		
		if (player.hasPermission("tekkitrestrict.noitem.blockall")) return true;
		
		//TRCacheItem ci1 = TRCacheItem.getPermCacheItem(player, "n", "noitem", id, data, false);//Perms and cache??
		//if (ci1 != null) return true;
		
		String idStr = "tekkitrestrict.noitem."+id;
		
		if (player.hasPermission(idStr+"."+data)) return true;
		else if (player.hasPermission(idStr)) return true;
		else {
			Iterator<String> keys = TRItemProcesser.groups.keySet().iterator();
			while (keys.hasNext()) {
				String key = keys.next();
				if (player.hasPermission("tekkitrestrict.noitem."+key)) {
					List<TRItem> mi = TRItemProcesser.groups.get(key);
					for(TRItem c:mi){
						if (c == null) continue;
						if (c.compare(id, data)) return true;
					}
				}
			}
		}
		
		
		
		return false;
	}

	public static List<TRItemStack> stack(List<TRItemStack> l, String ins) {
		// separated by |
		// tekkitrestrict.log.info("Stack-"+ins);
		
		 if (ins.contains(";")) { 
			 String[] rs = ins.split(";"); 
			 for (String re : rs) { 
				 for (TRItemStack g : gettRangedItemValues(re)) { //tekkitrestrict.log.info("Stack++"+g.id); 
					 l.add(g); 
				 } 
			 } 
		 } else if(ins.length() > 0) { 
			 for (TRItemStack g : gettRangedItemValues(ins)) {
				 l.add(g); 
			 } // l.add(new ItemStack(Integer.parseInt(ins), 1, 0)); 
		 } 
		 return l;
	}

	public static TRItemStack[] gettRangedItemValues(String ins) {
		String insx = ins.replace(":-", ":=");
		// ranged values may start from 1-100
		// they may also just be 1.
		// They can also have a data value 1:4
		List<TRItemStack> r = new LinkedList<TRItemStack>();

		/*List<ItemStack> tttx = modItemList.get(ins.toLowerCase());
		if (tttx != null) {
			for (ItemStack a : tttx) {
				r.add(a);
			}
		}*/

		// now, lets determine the type that we are using.
		// ranges generally have a "-" in the middle.
		if (insx.contains("-")) { // a range of items
			// loop through this range and add each to the return stack.
			if (insx.contains(":")) {
				insx = insx.split(":")[0];
			}
			String[] t = insx.split("-");
			int from = Integer.parseInt(t[0]);
			int to = Integer.parseInt(t[1]);

			for (int i = from; i <= to; i++) {
				r.add(new TRItemStack(i, 1, 0));
			}
		} else if (insx.contains(":")) { // A single item with a datatype
			String[] t = insx.split(":");
			int id = Integer.parseInt(t[0]);
			int data = Integer.parseInt(t[1].replace('=', '-'));
			if (t[1].equals("0")) {
				data = -10;// tekkitrestrict.log.info(id+":::"+data);}
			}
			TRItemStack e = new TRItemStack(id, 1, data);
			r.add(e);
			
		} else { // Just a single item
					// if(ins.contains(":")) ins = ins.split(":")[0];
			try {
				r.add(new TRItemStack(Integer.parseInt(insx), 1, 0));
			} catch (Exception ex) {
			}
		}

		TRItemStack[] isz = r.toArray(new TRItemStack[0]);
		//r.clear();
		return isz;
	}

	public static boolean isInRanged(String ins, int id, int data) {
		TRItemStack[] range = gettRangedItemValues(ins);

		for (TRItemStack g : range) {
			// tekkitrestrict.log.info("[getRangedItemValues] - "+g.id+":"+g.getData());
			if (equalSet(g.id, g.data, id, data)) {
				return true;
			}
		}

		return false;
	}
	
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
	
	/*private static String[] modItems = new String[] { "ee=27520-27599;126-130",
		"buildcraft=153-174;4056-4066;4298-4324",
		"additionalpipes=4299-4305;179",
		"industrialcraft=219-223;225-250;30171-30256",
		"nuclearcontrol=192;31256-31260", "powerconverters=190",
		"compactsolars=183", "chargingbench=187",
		"advancedmachines=253-254;188-191", "redpowercore=136",
		"redpowerlogic=138;1258-1328", "redpowercontrol=133-134;148",
		"redpowermachine=137;150-151", "redpowerlighting=147",
		"wirelessredstone=177;6358-6363;6406;6408-6412",
		"mffs=253-254;11366-11374", "railcraft=206-215;7256-7316",
		"tubestuffs=194", "ironchests=19727-19762;181",
		"balkonweaponmod=26483-26530", "enderchest=178;7493",
		"chunkloaders=4095;214;7303;179" };*/
}

/*
 * class safezonetrack{ private static LinkedList<String[]> StoredPairs = new
 * LinkedList<String[]>();
 * 
 * public static void track(Player p){ //look for the player in the stored
 * pairs...
 * 
 * String insz = safeZone.getSafeZone(p);
 * //tekkitrestrict.log.info(StoredPairs.size()+" ["+name+","+p.getName()+"]");
 * if(insz == ""){ boolean insx = false; String from = ""; int index = 0;
 * for(int i = 0;i<StoredPairs.size();i++){ String[] pair = StoredPairs.get(i);
 * if(pair[1].equals(p.getName())){ insx = true; from = pair[0]; index = i; } }
 * 
 * if(insx){ changedOut(index,p,from); } } else{ boolean insss = false; for(int
 * i = 0;i<StoredPairs.size();i++){ String[] pair = StoredPairs.get(i);
 * if(pair[0].equals(insz)){ if(pair[1].equals(p.getName())){ insss = true; } }
 * } if(!insss){ //tekkitrestrict.log.info("boo :( "+insz+" | "+p.getName());
 * changedIn(p,insz); } else{ monitorIn(p); } } //Add player to it! }
 * 
 * 
 * private static void monitorIn(Player p){ //monitors player for any stupid
 * moves. This function also de-charges said player's // equipment.
 * 
 * //has no use atm. }
 * 
 * private static void changedIn(Player p,String name){ String message =
 * tekkitrestrict.config.getString("SSEnterMessage"); message =
 * message.replace("[name]", name); p.sendRawMessage(message);
 * StoredPairs.add(new String[]{name,p.getName()}); } private static void
 * changedOut(int index,Player p,String name){ String message =
 * tekkitrestrict.config.getString("SSExitMessage"); message =
 * message.replace("[name]", name); p.sendRawMessage(message);
 * StoredPairs.remove(index); } }
 */

