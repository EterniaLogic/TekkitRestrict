package com.github.dreadslicer.tekkitrestrict;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

//this class defines and enables the use of determining what items
//	the user has "specified" to be banned.

public class TRNoItem {
	private static List<TRCacheItem> DisabledItems = new LinkedList<TRCacheItem>(), DisabledCreativeItems = new LinkedList<TRCacheItem>();
	private static List<String> DisabledItemsStr = Collections
			.synchronizedList(new LinkedList<String>()),
			DisabledCreativeStr = Collections
					.synchronizedList(new LinkedList<String>());
	public static Map<String, List<TRCacheItem>> modItemDat = Collections.synchronizedMap(new HashMap<String, List<TRCacheItem>>());
	private static boolean useNoItem, useNoCreative;

	public static void clear() {
		DisabledItems.clear();
		DisabledCreativeItems.clear();
		DisabledItemsStr.clear();
		DisabledCreativeStr.clear();
		modItemDat.clear();
	}
	
	public static void reload() {
		allocateDisabledItems();
		allocateDisabledCreativeItems();
		useNoItem = tekkitrestrict.config.getBoolean("UseNoItem");
		useNoCreative = tekkitrestrict.config.getBoolean("UseLimitedCreative");
	}
	
	public static void aasdf(String a, List<TRCacheItem> b){
		modItemDat.put(a, b);
	}

	public static boolean isBlockDisabled(Block a) {
		if (useNoItem) {
			for (TRCacheItem rj : DisabledItems) {
				if (rj.compare(a.getTypeId(),a.getData())){
					return true;
				}
			}
		}
		return false;
	}

	@Deprecated
	public static boolean isCreativeItemBanned(Player p, ItemStack e) {
		return useNoCreative ? isTypeBanned("creative", DisabledCreativeItems, DisabledCreativeStr, p, e) : false;
	}
	
	public static boolean isCreativeItemBanned(Player p, int id, int data) {
		return useNoCreative ? isTypeBanned("creative", DisabledCreativeItems, DisabledCreativeStr, p, id, data) : false;
	}

	public static boolean isItemBanned(Player p, int id) {
		return useNoItem ? isItemBanned(p, id, 0) : false;
	}

	@Deprecated
	public static boolean isItemBanned(Player p, ItemStack e) {
		return useNoItem ? isTypeBanned("noitem", DisabledItems, DisabledItemsStr, p, e) : false;
	}
	
	public static boolean isItemBanned(Player p, int id, int data) {
		return useNoItem ? isTypeBanned("noitem", DisabledItems, DisabledItemsStr, p, id, data) : false;
	}
	
	public static boolean isTypeBanned(String Type, List<TRCacheItem> tlist, List<String> indices, Player p, int id, int data) {
		if (Util.hasBypass(p, Type)) return false;

		/*
		 * TRCacheItem ci = TRCacheItem.getPermCacheItem(p, Type, id, 0);
		 * if(ci != null) return true;
		 */
		TRCacheItem ci1 = TRCacheItem.getPermCacheItem(p, Type, id, data);
		if (ci1 != null) return true;

		if (TRPermHandler.hasPermission(p, Type, id + "", data + ""))
			return true;
		else if (TRPermHandler.hasPermission(p, Type, id + "", ""))
			return true;
		else {

			Iterator<String> keys = modItemDat.keySet().iterator();
			while (keys.hasNext()) {
				String g = keys.next();
				if (TRPermHandler.hasPermission(p, Type, g, "")) {
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
	}

	@Deprecated
	public static boolean isTypeBanned(String Type, List<TRCacheItem> tlist, List<String> indices, Player p, ItemStack e) {
		if (Util.hasBypass(p, Type)) return false;
		
		int id = e.id;
		int data = e.data;

		/*
		 * TRCacheItem ci = TRCacheItem.getPermCacheItem(p, Type, id, 0);
		 * if(ci != null) return true;
		 */
		TRCacheItem ci1 = TRCacheItem.getPermCacheItem(p, Type, id, data);
		if (ci1 != null) return true;
		
		//if(Type.equals("creative") && e.id == 35){
			//tekkitrestrict.log.info(p+" - "+Type+" - "+id+" - "+data);
			/*for(StackTraceElement ee:Thread.currentThread().getStackTrace()){
				tekkitrestrict.log.info("      "+ee.toString());
			}*/
		//}

		if (TRPermHandler.hasPermission(p, Type, id + "", data + ""))
			return true;
		else if (TRPermHandler.hasPermission(p, Type, id + "", ""))
			return true;
		else {

			Iterator<String> keys = modItemDat.keySet().iterator();
			while (keys.hasNext()) {
				String g = keys.next();
				if (TRPermHandler.hasPermission(p, Type, g, "")) {
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

		/*
		 * if(TRPermHandler.hasSpecialPermission(p,
		 * "tekkitrestrict.noitem."+e.id)){ if(e.getData() != 0){
		 * if(TRPermHandler.hasSpecialPermission(p,
		 * "tekkitrestrict.noitem."+e.id+"."+e.getData())){ return true; }else{
		 * return false; } }else{ return true; } }
		 */

		/*
		 * if(!TRPermHandler.hasPermission(p,"tekkitrestrict.noitem.bypass")){
		 * //Override via Disablement if(isItemDisabled(p,e)) return true;
		 * //tekkitrestrict.log.info("DisabledItems?--"); if(!r){ try{
		 * 
		 * 
		 * String[] list =
		 * TRPermHandler.getPermissions(p,"tekkitrestrict.noitem."); for(int i =
		 * 0;i<list.length;i++){ //tekkitrestrict.log.info(list[i]); //We need
		 * all permissions for tekkitrestrict.noitem. String permz = list[i];
		 * if(permz.startsWith("tekkitrestrict.noitem.")){ //ok, now we split it
		 * up. String itd = permz.replace("tekkitrestrict.noitem.","");
		 * ItemStack[] s = getRangedItemValues(itd); for(int
		 * j=0;j<s.length;j++){ ItemStack i1 = s[j]; if(i1.id == e.id){
		 * if(i1.getData() == 0 || (i1.getData() == e.getData())){ return true;
		 * } } } } } } catch(Exception e1){ //e1.printStackTrace();
		 * TRLogger.Log("debug", "isItemBanned-Error: "+e1.getMessage()); } } }
		 */
	}

	public static void allocateDisabledItems() {
		 //List<ItemStack> iss = new LinkedList<ItemStack>();
		List<String> di = tekkitrestrict.config.getStringList("DisableItems");
		for (int i = 0; i < di.size(); i++) {
			DisabledItems.addAll(TRCacheItem.processItemString("noitem", "", di.get(i)));
			 //ItemStack[] s = gettRangedItemValues(di.get(i));
			// if item data value is 0, ignore it.
			// loop through.
			
			 /*for (int j = 0; j < s.length; j++) { 
				 ItemStack i1 = s[j]; 
				 iss.add(i1);
				 DisabledItemsStr.add(i1.id+":"+i1.getData()); 
			 }*/
		}
		
		/*ItemStack[] isz = iss.toArray(new ItemStack[0]); 
		iss.clear();
		di.clear(); 
		DisabledItems = isz;*/
		
	}

	public static void allocateDisabledCreativeItems() {
		//List<ItemStack> iss = new LinkedList<ItemStack>();
		List<String> di = tekkitrestrict.config
				.getStringList("LimitedCreative");
		for (int i = 0; i < di.size(); i++) {
			List<TRCacheItem> cc = TRCacheItem.processItemString("creative", "afsd90ujpj", di.get(i));
			DisabledCreativeItems.addAll(cc);
			//TRCacheItem.addCacheList(key, list)
			
			/*ItemStack[] s = gettRangedItemValues(di.get(i)); 
			for (int j = 0; j <
			s.length; j++) { ItemStack i1 = s[j]; iss.add(i1);
			DisabledCreativeStr.add(i1.id+":"+i1.getData()); }*/
			
		}
		/*ItemStack[] isz = iss.toArray(new ItemStack[0]);
		iss.clear();
		di.clear();
		DisabledCreativeItems = isz;*/
	}

	public static int getTotalLen() {
		return DisabledCreativeItems.size() + DisabledItems.size();
	}

	public static List<ItemStack> stack(List<ItemStack> l, String ins) {
		// separated by |
		// tekkitrestrict.log.info("Stack-"+ins);
		
		 if (ins.contains(";")) { 
			 String[] rs = ins.split(";"); 
			 for (String re : rs) { 
				 for (ItemStack g : gettRangedItemValues(re)) { //tekkitrestrict.log.info("Stack++"+g.id); 
					 l.add(g); 
				 } 
			 } 
		 } else if(ins.length() > 0) { 
			 for (ItemStack g : gettRangedItemValues(ins)) {
				 l.add(g); 
			 } // l.add(new ItemStack(Integer.parseInt(ins), 1, 0)); 
		 } 
		 return l;
	}

	public static ItemStack[] gettRangedItemValues(String ins) {
		String insx = ins.replace(":-", ":=");
		// ranged values may start from 1-100
		// they may also just be 1.
		// They can also have a data value 1:4
		List<ItemStack> r = new LinkedList<ItemStack>();

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
				r.add(new ItemStack(i, 1, 0));
			}
		} else if (insx.contains(":")) { // A single item with a datatype
			String[] t = insx.split(":");
			int id = Integer.parseInt(t[0]);
			int data = Integer.parseInt(t[1].replace('=', '-'));
			if (t[1].equals("0")) {
				data = -10;// tekkitrestrict.log.info(id+":::"+data);}
			}
			ItemStack e = new ItemStack(id, 1, data);
			r.add(e);
			
		} else { // Just a single item
					// if(ins.contains(":")) ins = ins.split(":")[0];
			try {
				r.add(new ItemStack(Integer.parseInt(insx), 1, 0));
			} catch (Exception E) {
			}
		}

		ItemStack[] isz = r.toArray(new ItemStack[0]);
		r.clear();
		return isz;
	}

	public static boolean isInRanged(String ins, int id, int data) {
		ItemStack[] range = gettRangedItemValues(ins);

		for (ItemStack g : range) {
			// tekkitrestrict.log.info("[getRangedItemValues] - "+g.id+":"+g.getData());
			if (equalSet(g.id, g.data, id, data)) {
				return true;
			}
		}

		return false;
	}

	@SuppressWarnings({ "unused" })
	public static void test() {
		if (false) {
			/*
			 * List<ItemStack> r = new LinkedList<ItemStack>(); ItemStack[] c =
			 * getRangedItemValues("ee"); ItemStack[] c1 =
			 * getRangedItemValues("110:0"); for (ItemStack g : c) { r.add(g); }
			 * for (ItemStack g : c1) { r.add(g); } for (ItemStack g : r) {
			 * tekkitrestrict.log.info(g.id + ":" + g.getData()); }
			 */
		}
	}

	public static boolean equalSet(ItemStack is1, ItemStack is2) {
		return equalSet(is1.id, is1.data, is2.id, is2.data);
	}

	public static boolean equalSet(int id1, int data1, int id2, int data2) {
		if (id1 != id2) return false;
		if (data1 == 0 || data1 == data2 || (data1 == -10 && data2 == 0)) {
			return true;
		}
		return false;
	}
	
	/**
	 * @param id1
	 * @param data1
	 * @param id2
	 * @param data2
	 * @return True if: id1 == id2 and data1 = -1 or data1 == data2
	 */
	public static boolean equalSet2(int id1, int data1, int id2, int data2) {
		if (id1 != id2) return false;
		if (data1 == -1 || data1 == data2) {
			return true;
		}
		return false;
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

