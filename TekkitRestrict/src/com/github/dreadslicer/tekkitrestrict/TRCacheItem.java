package com.github.dreadslicer.tekkitrestrict;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

@SuppressWarnings("unused")
public class TRCacheItem {
	// Pre-caches items into a range.
	//private static Map<String, TRCacheItem> cache = Collections
	//		.synchronizedMap(new HashMap<String, TRCacheItem>());
	/*private static Map<String, List<TRCacheItem>> cacheMods = Collections
			.synchronizedMap(new HashMap<String, List<TRCacheItem>>());
	private static Map<String, Set<String>> cachePermTypes = Collections
			.synchronizedMap(new HashMap<String, Set<String>>());*/
	private static Map<String, TRCacheItem> cache = new ConcurrentHashMap<String, TRCacheItem>();
	private static Map<String, List<TRCacheItem>> cacheMods = new ConcurrentHashMap<String, List<TRCacheItem>>();
	private static Map<String, Set<String>> cachePermTypes = new ConcurrentHashMap<String, Set<String>>();
	

	// cacheTypes is used for exclusive types and is returned through

	protected TRCacheItem() {
	}

	public int id = -1;
	public int data;
	public String cacher = "";
	private int Data1 = -1;
	private Object Data2 = -1;

	public int getData() {
		return data;
	}

	public com.github.dreadslicer.tekkitrestrict.ItemStack getTRItemStack(
			int amount) {
		return new com.github.dreadslicer.tekkitrestrict.ItemStack(id, amount,
				data);
	}

	public org.bukkit.inventory.ItemStack getBukkitItemStack(int amount) {
		return new org.bukkit.inventory.ItemStack(id, amount, (short) 0,
				(byte) data);
	}

	public net.minecraft.server.ItemStack getMCItemStack(int amount) {
		return new net.minecraft.server.ItemStack(id, amount, data);
	}

	public int getIntData() {
		return Data1;
	}

	public Object getObjectData() {
		return Data2;
	}

	public void setIntData(int x) {
		/*
		 * for(StackTraceElement ee:Thread.currentThread().getStackTrace()){
		 * tekkitrestrict.log.info("    "+ee.toString()); }
		 */
		Data1 = x;
	}

	public void setObjectData(Object x) {
		Data2 = x;
	}

	public boolean compare(int id, int data) {
		boolean isAllDataTypes = (this.data == -10 && data == 0);
		boolean isALL = (this.id == -11);
		return isALL || (((this.id == id) && (this.data == data || isAllDataTypes)));
	}

	public boolean compare(ItemStack iss) {
		return compare(iss.id, iss.data);
	}

	
	public String toString() {
		return (new StringBuilder()).append(id).append(":")
				.append(data).toString();
	}
	
	@Override
	public Object clone(){
		TRCacheItem tci = new TRCacheItem();
		tci.id = this.id;
		tci.data = this.data;
		tci.cacher = this.cacher;
		tci.setIntData(this.getIntData());
		tci.setObjectData(this.getObjectData());
		return tci;
	}
	
	
	// noitem;cake

	public static void reload() {
		clearCache();
		for (String s : modItems) {
			if (s.contains("=")) {
				String[] gg = s.split("=");
				String mod = gg[0];
				TRNoItem.aasdf(mod, addCacheList(mod, processModString("", mod, gg[1])));
			}
		}

		// pre-load variables
		ConfigurationSection cs = tekkitrestrict.config
				.getConfigurationSection("MicroPermissions");
		if (cs != null) {
			ConfigurationSection cNoItem = cs.getConfigurationSection("NoItem");
			ConfigurationSection cBlockLimiter = cs
					.getConfigurationSection("BlockLimiter");
			ConfigurationSection cLimitedCreative = cs
					.getConfigurationSection("LimitedCreative");
			if (cNoItem != null) {
				pstring("noitem", cNoItem);
			}
			if (cBlockLimiter != null) {
				pstring("limiter", cBlockLimiter);
			}
			if (cLimitedCreative != null) {
				pstring("creative", cLimitedCreative);
			}
		}
	}

	private static void pstring(String permType, ConfigurationSection cs) {
		Set<String> cc = cs.getKeys(true);
		Iterator<String> cit = cc.iterator();
		while (cit.hasNext()) {
			try {
				String k = cit.next().toLowerCase();
				String d = cs.getString(k);
				//tekkitrestrict.log.info(k+" - "+d);
				int i = -1;
				if (d != null && d != "") {
					if (d.contains(" ")) {
						try {
							String[] c = d.split(" ");
							i = Integer.parseInt(c[1]);
							d = c[0];
						} catch (Exception e) {
						}
					}
					List<TRCacheItem> j = TRCacheItem.processMultiString(
							permType, k, d, i);
					 //tekkitrestrict.log.info(permType+"."+k+" - "+d+" - s"+j.size()+" "+i);
					for (Object c1 : j) {
						if (c1 instanceof TRCacheItem) {
						}
					}
				}
			} catch (Exception e) {
				//e.printStackTrace();
			}
		}
	}

	/*
	 * public static boolean hasItem(String permType,String type,int id, int
	 * data){ return hasItem(permType+";"+type,id,data); } public static boolean
	 * hasItem(String type,int id, int data){ return
	 * hasItem(type+"="+id+":"+data); } public static boolean hasItem(String
	 * key){ return cache.containsKey(key); }
	 */
	public static List<TRCacheItem> getCacheList(String permType, String type) {
		return getCacheList(permType + ";" + type);
	}

	public static List<TRCacheItem> getCacheList(String type) {
		//synchronized (cacheMods) {
			return cacheMods.get(type.toLowerCase());
		//}
	}

	public static List<TRCacheItem> setCacheList(String permType, String type,
			int id, int data) {
		List<TRCacheItem> l = new LinkedList<TRCacheItem>();
		l.add(cacheItem(permType, type, id, data));
		return addCacheList(permType, type, l);
	}

	public static List<TRCacheItem> addCacheList(String permType, String type,
			List<TRCacheItem> list) {
		String key = permType + ";" + type;
		return addCacheList(key, list);
	}

	public static List<TRCacheItem> addCacheList(String key,
			List<TRCacheItem> list) {
		List<TRCacheItem> m = cacheMods.get(key.toLowerCase());
		if (m != null) {
			if (m.contains(key.toLowerCase())) {
				return m;
			} else {
				m.addAll(list);
				//synchronized (cacheMods) {
					cacheMods.put(key.toLowerCase(), m);
					return m;
				//}
			}
		} else {
			m = new LinkedList<TRCacheItem>();
			m.addAll(list);
			//synchronized (cacheMods) {
				cacheMods.put(key.toLowerCase(), m);
				return m;
			//}
		}
	}

	/**
	 * @param player
	 * @param permType
	 * @param id
	 * @param data
	 * @return null if id = 0 or player has bypass permission.
	 */
	public static TRCacheItem getPermCacheItem(Player player, String permType, int id, int data) {
		if (id == 0 || player.hasPermission("tekkitrestrict.bypass."+permType)) {
			return null;
		}
		
		Set<String> l = cachePermTypes.get(permType.toLowerCase());
		if (l == null) {
			l = new HashSet<String>();
		}
		// else tekkitrestrict.log.info("has "+permType);
		Iterator<String> itt = l.iterator();
		while (itt.hasNext()) {
			String type = itt.next();
			
			boolean hasALL = TRPermHandler.hasPermission(player, permType, "*", "");
			
			if (hasALL || TRPermHandler.hasPermission(player, permType, type, "") || type.equals("afsd90ujpj")) {
				boolean C = hasCacheItem(permType, type, 999999999, 999999999);
				if(C || hasALL) return new TRCacheItem(); // has "*"
				
				boolean A = hasCacheItem(permType, type, id, data);
				boolean B = hasCacheItem(permType, "afsd90ujpj", id, data);
				
				/*if(type.equals("afsd90ujpj"))
					tekkitrestrict.log.info("tekkitrestrict."+permType+"."+type+" -> "+id+":"+data);*/
				if (A || B) {
					//if(type.equals("afsd90ujpj"))
						//tekkitrestrict.log.info("===tekkitrestrict."+permType+"."+type+" -> "+id+":"+data);

					if (A) {
						return getCacheItem(permType, type, id, data);
					} else {
						return getCacheItem(permType, "afsd90ujpj", id, data);
					}
				}
			}
		}
		return null;
	}

	public static boolean hasCacheItem(String permType, String type, int id,
			int data) {
		return hasCacheItem(permType+";"+type,id,data);
	}
	
	public static boolean hasCacheItem(String type, int id, int data) {
		//noitem;type=99999999:9999999
		String keybase = type + "=" + id + ":";
		String key = keybase + data;
		String key0 = keybase + "0";
		String key10 = keybase + "-10";
		// tekkitrestrict.log.info(key);
		if (data == 0 && cache.get(key10.toLowerCase()) != null) {
			return true;
		}
		if (cache.get(key.toLowerCase()) != null) {
			return true;
		}
		if (cache.get(key0.toLowerCase()) != null) {
			return true;
		}

		return false;
	}

	public static TRCacheItem getCacheItem(String permType, String type,
			int id, int data) {
		String keybase = permType + ";" + type + "=" + id + ":";
		String key = keybase + data;
		String key0 = keybase + "0";
		String key10 = keybase + "-10";
		TRCacheItem cii = null;
		if (data == 0 && (cii = cache.get(key10.toLowerCase())) != null) {
			return cii;
		}
		if ((cii = cache.get(key.toLowerCase())) != null) {
			return cii;
		}
		if ((cii = cache.get(key0.toLowerCase())) != null) {
			return cii;
		}

		return null;
	}

	public static List<TRCacheItem> processItemString(String permType, String type, String item) {
		String key = permType + ";" + type;
		cpermtype(permType, type);
		return processItemString(key, item, -1);
	}

	public static List<TRCacheItem> processItemString(String permType, String type, String item, int data2) {
		String key = permType + ";" + type;
		cpermtype(permType, type);
		return processItemString(key, item, data2);
	}

	// used for reloads...
	public static List<TRCacheItem> processItemString(String type, String item, int data2) {
		String itemx = item.replace(":-", ":=");
		// converts a variable string into a list of data.
		List<TRCacheItem> tci = new LinkedList<TRCacheItem>();

		if (itemx.contains("-")) { // a range of items
			// loop through this range and add each to the return stack.
			if (itemx.contains(":")) {
				itemx = itemx.split(":")[0];
			}
			String[] t = itemx.split("-");
			int from = Integer.parseInt(t[0]);
			int to = Integer.parseInt(t[1]);

			for (int i = from; i <= to; i++) {
				try {
					tci.add(cacheItem(type, i, 0, data2));
				} catch (Exception e) {
					//e.printStackTrace();
				}
			}
		} else if (itemx.contains(":")) { // A single item with a datatype
			String[] t = itemx.split(":");
			int id = Integer.parseInt(t[0]);
			int data = Integer.parseInt(t[1].replace('=', '-'));
			if (t[1].equals("0")) {
				data = -10;// tekkitrestrict.log.info(id+":::"+data);}
			}
			try {
				tci.add(cacheItem(type, id, data, data2));
			} catch (Exception e) {
				//e.printStackTrace();
				addCacheList(type, tci);
				return tci;
			}
		} else { // Just a single item
					// if(ins.contains(":")) ins = ins.split(":")[0];
			//tekkitrestrict.log.info(item);
			// determine whether the item string is a mod or list. If so,
			// then... ?

			try {
				tci.add(cacheItem(type, Integer.parseInt(itemx), 0, data2));
				// r.add(new ItemStack(Integer.parseInt(item), 1, 0));
			} catch (Exception E) {
				// E.printStackTrace();

				// tekkitrestrict.log.info("=========="+type+item.toLowerCase());
				// Iterator<String> ci1 = cacheMods.keySet().iterator();
				/*
				 * while(ci1.hasNext()){ tekkitrestrict.log.info(ci1.next()); }
				 */
				if(itemx.equals("*")){
					tci.add(cacheItem(type, 999999999, 999999999, data2)); //Every single freaking item.
					addCacheList(type, tci);
					//tekkitrestrict.log.info("has infinite on "+type);
					return tci;
				}
				
				
				if (cacheMods.containsKey(item.toLowerCase())) {
					// modtypes
					List<TRCacheItem> cc = cacheMods.get(item.toLowerCase());
					for (TRCacheItem ci : cc) {
						//tekkitrestrict.log.info(type+" <- "+ci.toString());
						try{
							TRCacheItem ci2 = (TRCacheItem)ci.clone();
							ci2.setIntData(data2);
							ci2.cacher = type+"="+ci2.id+":"+ci2.data;
							cache.put(type+"="+ci2.id+":"+ci2.data, ci2);
						}
						catch(Exception e){}
					}
					//tekkitrestrict.log.info("place mod - "+item+" size: "+cc.size());
					if(type.contains(";")){
						String[] uu = type.split(";");
						cpermtype(uu[0], uu[1]);
					}
					addCacheList(type, cc);
					return cc;
				}
				addCacheList(type, tci);
				return tci;
			}
		}
		addCacheList(type, tci);
		return tci;
	}

	// to be used exclusively for types that require a permission type. (Type is
	// then the var name)
	public static TRCacheItem cacheItem(String permType, String type, int id,
			int data) {
		cpermtype(permType, type);
		return cacheItem(permType + ";" + type, id, data);
	}

	// do not use for permission types. use the permType version instead.
	public static TRCacheItem cacheItem(String type, int id, int data) {
		String key = type + "=" + id + ":" + data;
		TRCacheItem m = cache.get(key.toLowerCase());
		if (m == null) {
			TRCacheItem c = new TRCacheItem();
			c.id = id;
			c.data = data;
			c.cacher = key;
			//synchronized (cache) {
				cache.put(key.toLowerCase(), m = c);
			//}
		}
		return m;
	}

	public static TRCacheItem cacheItem(String type, int id, int data, int numdata) {
		String key = type + "=" + id + ":" + data;
		TRCacheItem m = cache.get(key.toLowerCase());
		if (m == null) {
			TRCacheItem c = new TRCacheItem();
			c.id = id;
			c.data = data;
			c.cacher = key;
			c.setIntData(numdata);
			// cacheTypes.put(key, value);
			/*synchronized (cache) {
				
			}*/
			cache.put(key.toLowerCase(), m = c);
		}
		return m;
	}

	public static TRCacheItem cacheItem(String type, int id, int data,
			Object objdata) {
		String key = type + "=" + id + ":" + data;
		TRCacheItem m = cache.get(key.toLowerCase());
		if (m == null) {
			TRCacheItem c = new TRCacheItem();
			c.id = id;
			c.data = data;
			c.cacher = key;
			c.Data2 = objdata;
			/*synchronized (cache) {
				
			}*/
			cache.put(key.toLowerCase(), m = c);
		}
		return m;
	}

	// used for any item call that is not directly using a type or permission
	// type
	public static TRCacheItem cacheItem(int id, int data) {
		return cacheItem("", id, data); // caches a null-string type.
	}

	private static void cpermtype(String ptype, String type) {
		Set<String> l = cachePermTypes.get(ptype.toLowerCase());
		if (l == null) {
			l = new HashSet<String>();
		}
		l.add(type.toLowerCase());

		synchronized (cachePermTypes) {
			cachePermTypes.put(ptype.toLowerCase(), l);
		}
	}

	public static List<TRCacheItem> processModString(String permType,
			String type, String ins) {
		cpermtype(permType, type);
		return processMultiString(permType + ";" + type, ins, -1);
	}

	public static List<TRCacheItem> processMultiString(String permType,
			String type, String ins, int data2) {
		cpermtype(permType, type);
		return processMultiString(permType + ";" + type, ins, data2);
	}

	public static List<TRCacheItem> processMultiString(String type, String ins,
			int data2) {
		if (ins.contains(";")) {
			String[] rs = ins.split(";");
			List<TRCacheItem> l = new LinkedList<TRCacheItem>();
			for (String re : rs) {
				l.addAll(processItemString(type, re, data2));
			}
			return l;
		} else if (ins.length() > 0) {
			return processItemString(type, ins, data2);
		}
		return new LinkedList<TRCacheItem>();
	}

	public static void clearCache() {
		// clears the cache so new perms can be added.
		// tekkitrestrict.log.info(cache.size()+" - "+cacheMods.size()+" - "+cachePermTypes.size());
		synchronized (cache) {
			cache.clear();
		}
		synchronized (cacheMods) {
			cacheMods.clear();
		}
		synchronized (cachePermTypes) {
			cachePermTypes.clear();
		}
	}

	private static String[] modItems = new String[] { "ee=27520-27599;126-130",
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
			"chunkloaders=4095;214;7303;179" };
}
