package com.github.dreadslicer.tekkitrestrict;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.configuration.ConfigurationSection;

import com.github.dreadslicer.tekkitrestrict.Log.Warning;
import com.github.dreadslicer.tekkitrestrict.annotations.Safe;
import com.github.dreadslicer.tekkitrestrict.objects.TREnums.ConfigFile;
import com.github.dreadslicer.tekkitrestrict.objects.TRItem;

public class TRCacheItem {
	//Block ID's Forge: 0-4095
	//Item ID's: 4096-32000
	//ID's: 5 chars
	//Block Dmg values: 0-15 (4bits)
	//Item Dmg values: 0-65536 (2 bytes)
	//Data: 5 chars
	
	// Pre-caches items into a range.
	private static ConcurrentHashMap<String, List<TRItem>> cacheMods = new ConcurrentHashMap<String, List<TRItem>>();
	
	private static String[] modItems = new String[] {
		"ee=27520-27599;126-130",
		"buildcraft=153-174;4056-4066;4298-4324",
		"additionalpipes=4299-4305;179",
		"industrialcraft=219-223;225-250;30171-30256",
		"nuclearcontrol=192;31256-31260",
		"powerconverters=190",
		"compactsolars=183",
		"chargingbench=187",
		"advancedmachines=253-254;188-191",
		"redpowercore=136",
		"redpowerlogic=138;1258-1328",
		"redpowercontrol=133-134;148",
		"redpowermachine=137;150-151",
		"redpowerlighting=147",
		"wirelessredstone=177;6358-6363;6406;6408-6412",
		"mffs=253-254;11366-11374",
		"railcraft=206-215;7256-7316",
		"tubestuffs=194",
		"ironchests=19727-19762;181",
		"balkonweaponmod=26483-26530",
		"enderchest=178;7493",
		"chunkloaders=4095;214;7303;179"
	};

	protected TRCacheItem() {}

	public int id = -1;
	public int data;
	//private String cacher = "";
	//private int Data1 = -1;
	//private Object Data2 = -1;

	//public int getIntData() {
	//	return Data1;
	//}
	//public void setIntData(int x) {
	//	Data1 = x;
	//}
	//public Object getObjectData() {
	//	return Data2;
	//}
	//public void setObjectData(Object x) {
	//	Data2 = x;
	//}

	/**
	 * Compare this CacheObject with the given id and data
	 * @return True if:<br>
	 * <ul>
	 * <li>this.id == -11</li>
	 * <li>this.id == id AND this.data == data</li>
	 * <li>this.id == id AND this.data == -1</li>
	 * <li>this.id == id AND this.data == -10 AND data == 0</li>
	 * </ul>
	 */
	@Safe
	public boolean compare(int id, int data) {
		//boolean isAllDataTypes = (this.data == -10 && data == 0);
		//boolean isALL = (this.id == -11);
		return (this.id == -11) || ((this.id == id && (this.data == data || this.data == -1 || (this.data == -10 && data == 0))));
	}

	/** @return A string representation of this Cache Item: "id:data" */
	@Override
	public String toString() {
		return new StringBuilder(12).append(id).append(":").append(data).toString();
	}
	
	@Override
	public Object clone(){
		TRCacheItem tci = new TRCacheItem();
		tci.id = this.id;
		tci.data = this.data;
		return tci;
	}

	public static void reload() {
		clearCache();
		for (String s : modItems) {
			if (s.contains("=")) {
				String[] gg = s.split("=");
				String mod = gg[0];
				TRNoItem.addGroup(mod, processMultiString(mod, gg[1]));
			}
		}

		// pre-load variables
		ConfigurationSection groups = tekkitrestrict.config.getConfigurationSection(ConfigFile.GroupPermissions, "PermissionGroups");
		if (groups != null) {
			pstring(groups);
		}
	}

	private static void pstring(ConfigurationSection cs) {
		Set<String> keys = cs.getKeys(true);
		Iterator<String> keyIterator = keys.iterator();
		while (keyIterator.hasNext()) {
			try {
				String groupName = keyIterator.next().toLowerCase();
				String value = cs.getString(groupName);

				if (value == null || value.equals("")) continue;
				
				if (value.contains(" ")) {
					Log.Warning.config("Invalid value in PermissionGroups: Invalid value \""+value+"\"!");
					continue;
				}
				List<TRItem> cacheItems = TRCacheItem.processMultiString(groupName, value);
				TRNoItem.addGroup(groupName, cacheItems);

			} catch (Exception ex) {
				tekkitrestrict.log.warning("Error in PermissionGroups: " + ex.getMessage());
				Log.Exception(ex, false);
			}
		}
	}
	
	public static List<TRItem> processItemStringNoCache(String item) {
		String itemx = item.replace(":-", ":=");
		// converts a variable string into a list of data.
		LinkedList<TRItem> tci = new LinkedList<TRItem>();

		//############################## RANGE OF ITEMS ###########################
		if (itemx.contains("-")) {
			// loop through this range and add each to the return stack.
			int data1 = -1;
			if (itemx.contains(":")) {
				String dataString = itemx.split(":")[1];
				if (dataString.equals("*")) data1 = -1;
				else {
					try {
						data1 = Integer.parseInt(dataString);
						if (data1 == 0){
							data1 = -10;
						}
					} catch (NumberFormatException ex){
						Warning.config("Invalid data value: \"" + dataString + "\" in \"" + itemx + "\"!");
					}
				}
				
				itemx = itemx.split(":")[0];
			}
			String[] t = itemx.split("-");
			int fromId = 0, toId = 0;
			try {
				fromId = Integer.parseInt(t[0]);
				toId = Integer.parseInt(t[1]);
			} catch (NumberFormatException ex){
				Warning.config("Invalid range: \"" + t[0]+"-"+t[1] + "\"");
				return tci;
			}

			for (int i = fromId; i <= toId; i++) {
				try {
					tci.add(TRItem.parseItem(i, data1));
				} catch (Exception ex) {
					Log.Exception(ex, true);
					return tci;
				}
			}
			return tci;
		}
		
		//############################## SINGLE ID WITH DATA ###########################
		else if (itemx.contains(":")) {
			String[] t = itemx.split(":");
			int id = 0, data = 0;
			
			try {
				id = Integer.parseInt(t[0]);
				if (t[1].equals("*"))
					data = -1;
				else
					data = Integer.parseInt(t[1].replace('=', '-'));
			} catch (NumberFormatException | ArrayIndexOutOfBoundsException ex){
				Warning.config("Invalid entry: \"" + itemx + "\"!");
				return tci;
			}
			
			if (data == 0) { //If :0, then :-10
				data = -10;
			}
			
			try {
				tci.add(TRItem.parseItem(id, data));
				return tci;
			} catch (Exception ex) {
				Log.Exception(ex, true);
				return tci;
			}
		}
		
		//############################## ALL ITEMS ###########################
		else if(itemx.equals("*")) {
			tci.add(TRItem.parseItem(99999, 99999)); //Every single freaking item.
			return tci;
		}
		
		//############################## SINGLE ID ###########################
		else {
			int id = 0;
			try {
				id = Integer.parseInt(itemx);
			} catch (NumberFormatException ex){
				if (cacheMods.containsKey(item.toLowerCase())) {
					List<TRItem> cc = cacheMods.get(item.toLowerCase());
					return cc;
				}
				Warning.config("You have an error in your Config: \""+itemx+"\" is not a valid item string.");
				return tci;
			}

			try {
				tci.add(TRItem.parseItem(id, -1));
				return tci;
			} catch (Exception ex) {
				Warning.config("Invalid entry: \"" + itemx + "\"!");
				return tci;
			}
		}
	}

	private static List<TRItem> processMultiString(String type, String ins) {
		if (ins.contains(";")) {
			String[] itemsStr = ins.split(";");
			List<TRItem> l = new LinkedList<TRItem>();
			for (String itemStr : itemsStr) {
				l.addAll(processItemStringNoCache(itemStr));
			}
			return l;
		} else if (ins.length() > 0) {
			return processItemStringNoCache(ins);
		}
		return new LinkedList<TRItem>();
	}

	/** Clears the cache so new perms can be added. */
	private static void clearCache() {
		synchronized (cacheMods) {
			for (String s : cacheMods.keySet()){
				List<TRItem> cis = cacheMods.get(s);
				for (TRItem ci : cis){
					Log.Debug("cacheMods.get("+s+"): " + ci.toString());
				}
			}
			cacheMods.clear();
		}
	}

}
