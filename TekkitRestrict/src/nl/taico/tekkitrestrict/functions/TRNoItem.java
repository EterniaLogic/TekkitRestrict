package nl.taico.tekkitrestrict.functions;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import nl.taico.tekkitrestrict.Log;
import nl.taico.tekkitrestrict.TRException;
import nl.taico.tekkitrestrict.TRItemProcessor2;
import nl.taico.tekkitrestrict.Log.Warning;
import nl.taico.tekkitrestrict.TRConfigCache.Listeners;
import nl.taico.tekkitrestrict.config.SettingsStorage;
import nl.taico.tekkitrestrict.objects.TRItem;
import nl.taico.tekkitrestrict.objects.itemprocessor.TRMod;

/**
 * This class defines and enables the use of determining what items
 * the user has "specified" to be banned.
 */
public class TRNoItem {
	/** A list of all the (by config) banned items. */
	private static LinkedList<TRItem> DisabledItems = new LinkedList<TRItem>();
	/** A list of all the (by config) banned creative items. */
	private static LinkedList<TRItem> DisabledCreativeItems = new LinkedList<TRItem>();
	
	private static ArrayList<String> DisabledItemGroups = new ArrayList<String>();
	
	public static ArrayList<TRMod> bannedMods = new ArrayList<TRMod>();
	public static ArrayList<TRMod> bannedCreativeMods = new ArrayList<TRMod>();
	/**
	 * Uses allocateDisabledItems() and allocateDisabledCreativeItems()<br>
	 * Also rechecks if TekkitRestrict should use noItem and/or LimitedCreative<br>
	 * Also clears all currently banned items.
	 * 
	 * @see #allocateDisabledCreativeItems()
	 * @see #allocateDisabledItems()
	 */
	public static void reload() {
		DisabledItemGroups.clear();
		bannedMods.clear();
		allocateDisabledItems();
		allocateDisabledCreativeItems();
	}
	
	/**
	 * Loads the Disabled Items from the config.
	 */
	private static void allocateDisabledItems() {
		Log.trace("Loading Banned Items...");
		final List<String> di = SettingsStorage.bannedConfig.getStringList("BannedItems");
		final LinkedList<TRItem> temp1 = new LinkedList<TRItem>();
		
		for (String str : di) {
			try {
				temp1.addAll(TRItemProcessor2.processString(str));
				
				final int m = str.indexOf('{');
				if (m != -1) str = str.substring(0, m);
				str = str.replace(" ", "");
				for (final TRMod mod : TRItemProcessor2.mods){
					if (mod.is(str)) bannedMods.add(mod);
				}
			} catch (TRException ex) {
				Warning.config("You have an error in your DisableItems.config.yml in DisableItems:", false);
				Warning.config(ex.getMessage(), false);
				continue;
			}
		}
		DisabledItems = temp1;
	}
	private static void allocateDisabledCreativeItems() {
		Log.trace("Loading Limited Creative Banned Items...");
		final List<String> di = SettingsStorage.limitedCreativeConfig.getStringList("LimitedCreative");
		final LinkedList<TRItem> temp1 = new LinkedList<TRItem>();
		
		for (String str : di) {
			try {
				temp1.addAll(TRItemProcessor2.processString(str));
				
				final int m = str.indexOf('{');
				if (m != -1) str = str.substring(0, m);
				str = str.replace(" ", "");
				for (final TRMod mod : TRItemProcessor2.mods){
					if (mod.is(str)) bannedMods.add(mod);
				}
			} catch (TRException ex) {
				Warning.config("You have an error in your LimitedCreative.config.yml in LimitedCreative:", false);
				Warning.config(ex.getMessage(), false);
				continue;
			}	
		}
		
		DisabledCreativeItems = temp1;
	}
	
	/**
	 * Goes through all banned items and checks if the id and data match.
	 * 
	 * @return If the given block is a disabled item (set in the config).
	 */
	public static boolean isBlockBanned(@NonNull Block block) {
		if (!Listeners.UseNoItem) return false;
		
		final int id = block.getTypeId();
		final int data = block.getData();
		for (final TRItem bannedItem : DisabledItems) {
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
	@Nullable public static String isItemBanned(@NonNull Player player, int id, boolean doBypassCheck) {
		return isItemBanned(player, id, 0, doBypassCheck);
	}
	/**
	 * Note: It checks this player's permissions for tekkitrestrict.noitem.id[.data]
	 * and, if doBypassCheck is true, for tekkitrestrict.bypass.noitem
	 * 
	 * @return If the given id:data combination is banned for this player.
	 */
	@Nullable public static String isItemBanned(@NonNull Player player, int id, int data, boolean doBypassCheck) {
		return Listeners.UseNoItem ? isTypeNoItemBanned(player, id, data, doBypassCheck) : null;
	}
	
	/**
	 * Goes through all banned items and checks if the id and data match.
	 * 
	 * @return If the given item/block is banned in the config.
	 */
	@Nullable public static String isItemGloballyBanned(int id, int data) {
		if (!Listeners.UseNoItem) return null;
		
		for (final TRItem bannedItem : DisabledItems) {
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
	@Nullable public static String isItemBannedInCreative(@NonNull Player player, int id, int data, boolean doBypassCheck) {
		return Listeners.UseLimitedCreative ? isTypeCreativeBanned(player, id, data, doBypassCheck) : null;
	}

	@Nullable private static String isTypeCreativeBanned(@NonNull Player player, int id, int data, boolean doBypassCheck) {
		if (id < 8) return null;
		if (doBypassCheck && player.hasPermission("tekkitrestrict.bypass.creative")) return null;
		
		if (DisabledCreativeItems != null) {
			for (final TRItem cc : DisabledCreativeItems){
				if (cc.compare(id, data)) return cc.msg == null ? "" : cc.msg;
			}
		}
		
		if (player.hasPermission("tekkitrestrict.creative.blockall")) return "";
		
		final String idStr = "tekkitrestrict.creative."+id;
		
		if (player.hasPermission(idStr+"."+data)) return "";
		else if (player.hasPermission(idStr)) return "";
		else {
			for (final TRMod mod : TRItemProcessor2.mods){
				for (final String name : mod.names){
					if (player.hasPermission("tekkitrestrict.creative."+name)) {
						for(final TRItem c : mod.getItemsNoCopy()){
							if (c.compare(id, data)) return c.msg == null?"":c.msg;
						}
					}
				}
			}
			
			for (final TRMod group : TRItemProcessor2.groups){
				for (final String name : group.names){
					if (player.hasPermission("tekkitrestrict.creative."+name)) {
						for(final TRItem c : group.getItemsNoCopy()){
							if (c.compare(id, data)) return c.msg == null?"":c.msg;
						}
					}
				}
			}
		}
		
		return null;
	}
	
	@Nullable private static String isTypeNoItemBanned(@NonNull Player player, int id, int data, boolean doBypassCheck) {
		if (id < 8) return null;
		if (doBypassCheck && player.hasPermission("tekkitrestrict.bypass.noitem")) return null;

		if (DisabledItems != null) {
			for (final TRItem cc : DisabledItems){
				if (cc.compare(id, data)) return cc.msg == null ? "" : cc.msg;
			}
		}
		
		if (player.hasPermission("tekkitrestrict.noitem.blockall")) return "";
		
		final String idStr = "tekkitrestrict.noitem."+id;
		
		if (player.hasPermission(idStr+"."+data)) return "";
		else if (player.hasPermission(idStr)) return "";
		else {
			for (final TRMod mod : TRItemProcessor2.mods){
				for (final String name : mod.names){
					if (player.hasPermission("tekkitrestrict.noitem."+name)) {
						for(final TRItem c : mod.getItemsNoCopy()){
							if (c.compare(id, data)) return c.msg == null?"":c.msg;
						}
					}
				}
			}
			
			for (final TRMod group : TRItemProcessor2.groups){
				for (final String name : group.names){
					if (player.hasPermission("tekkitrestrict.noitem."+name)) {
						for(final TRItem c : group.getItemsNoCopy()){
							if (c.compare(id, data)) return c.msg == null?"":c.msg;
						}
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

	public static List<TRItem> getBannedItems(){
		return DisabledItems;
	}
	
	/** @return True if: id1 == id2 and (data1 = -1 or data1 == data2 or (data1 = -10 and data2 = 0)) */
	public static boolean equalSet(int id1, int data1, int id2, int data2) {
		if (id1 != id2) return false;
		if (data1 == -1 || data1 == data2){// || (data1 == -10 && data2 == 0)) { TODO change -10
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

