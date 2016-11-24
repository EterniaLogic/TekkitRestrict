package nl.taico.tekkitrestrict;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNull;

import nl.taico.taeirlib.config.interfaces.ISection;
import nl.taico.tekkitrestrict.Log.Warning;
import nl.taico.tekkitrestrict.objects.TRItem;
import nl.taico.tekkitrestrict.objects.itemprocessor.TRMod;

import static nl.taico.tekkitrestrict.config.SettingsStorage.groupPermsConfig;

public class TRItemProcessor2 {
	public static final ArrayList<TRMod> mods = new ArrayList<TRMod>();
	public static ArrayList<TRMod> groups = new ArrayList<TRMod>();
	
	static{
		List<Integer> items = new ArrayList<Integer>();
		for (int i=27520;i<=27599;i++) items.add(i);
		for (int i=126;i<=130;i++) items.add(i);
		mods.add(new TRMod(new String[] {"EE","EquivalentExchange"}, items));
		
		items = new ArrayList<Integer>();
		for (int i=153;i<=174;i++) items.add(i);
		for (int i=4056;i<=4066;i++) items.add(i);
		for (int i=4298;i<=4324;i++) items.add(i);
		mods.add(new TRMod(new String[] {"BuildCraft","BC"}, items));
		
		items = new ArrayList<Integer>();
		for (int i=4299;i<=4305;i++) items.add(i);
		items.add(179);
		mods.add(new TRMod(new String[] {"AdditionalPipes"}, items));
		
		items = new ArrayList<Integer>();
		for (int i=219;i<=223;i++) items.add(i);
		for (int i=225;i<=250;i++) items.add(i);
		for (int i=30171;i<=30256;i++) items.add(i);
		mods.add(new TRMod(new String[] {"IndustrialCraft", "IC2", "IndustrialCraft2"}, items));
		
		items = new ArrayList<Integer>();
		items.add(192);
		for (int i=31256;i<=31260;i++) items.add(i);
		mods.add(new TRMod(new String[] {"NuclearControl"}, items));
		
		items = new ArrayList<Integer>();
		items.add(253);
		items.add(254);
		for (int i=188;i<=191;i++) items.add(i);
		mods.add(new TRMod(new String[] {"AdvancedMachines"}, items));
		
		items = new ArrayList<Integer>();
		items.add(190);
		mods.add(new TRMod(new String[] {"PowerConverters"}, items));
		
		items = new ArrayList<Integer>();
		items.add(183);
		mods.add(new TRMod(new String[] {"CompactSolars"}, items));

		items = new ArrayList<Integer>();
		items.add(187);
		mods.add(new TRMod(new String[] {"ChargingBench"}, items));
		
		items = new ArrayList<Integer>();
		items.add(136);
		mods.add(new TRMod(new String[] {"RedpowerCore"}, items));
		
		items = new ArrayList<Integer>();
		items.add(138);
		for (int i=1258;i<=1328;i++) items.add(i);
		mods.add(new TRMod(new String[] {"RedpowerLogic"}, items));
		
		items = new ArrayList<Integer>();
		items.add(133);
		items.add(134);
		items.add(148);
		mods.add(new TRMod(new String[] {"RedpowerControl"}, items));
		
		items = new ArrayList<Integer>();
		items.add(147);
		mods.add(new TRMod(new String[] {"RedpowerLighting"}, items));
		
		items = new ArrayList<Integer>();
		items.add(137);
		items.add(150);
		items.add(151);
		mods.add(new TRMod(new String[] {"RedpowerMachine", "RedpowerMachines"}, items));
		
		items = new ArrayList<Integer>();
		items.add(177);
		items.add(6406);
		for (int i=6358;i<=6363;i++) items.add(i);
		for (int i=6408;i<=6412;i++) items.add(i);
		mods.add(new TRMod(new String[] {"WirelessRedstone"}, items));
		
		items = new ArrayList<Integer>();
		items.add(253);
		items.add(254);
		for (int i=11366;i<=11374;i++) items.add(i);
		mods.add(new TRMod(new String[] {"MFFS"}, items));
		
		items = new ArrayList<Integer>();
		for (int i=206;i<=215;i++) items.add(i);
		for (int i=7256;i<=7316;i++) items.add(i);
		mods.add(new TRMod(new String[] {"RailCraft"}, items));
		
		items = new ArrayList<Integer>();
		items.add(194);
		mods.add(new TRMod(new String[] {"TubeStuff", "TubeStuffs"}, items));
		
		items = new ArrayList<Integer>();
		items.add(181);
		for (int i=19727;i<=19762;i++) items.add(i);
		mods.add(new TRMod(new String[] {"IronChests"}, items));
		
		items = new ArrayList<Integer>();
		for (int i=26483;i<=26530;i++) items.add(i);
		mods.add(new TRMod(new String[] {"BalkonsWeaponMod", "WeaponMod", "BalkonWeaponMod"}, items));
		
		items = new ArrayList<Integer>();
		items.add(178);
		items.add(7493);
		mods.add(new TRMod(new String[] {"EnderChest"}, items));
		
		items = new ArrayList<Integer>();
		items.add(4095);
		items.add(214);
		items.add(7303);
		items.add(179);
		mods.add(new TRMod(new String[] {"ChunkLoaders"}, items));
	}
	
	public static void load(){
		
		final ISection cs = groupPermsConfig.getSection("PermissionGroups");
		final Set<String> keys;
		if (cs == null || (keys = cs.getKeys(false)).isEmpty()){
			groups = new ArrayList<TRMod>(0);
			return;
		}
		
		Log.trace("Loading Permission Groups...");
		ArrayList<TRMod> temp = new ArrayList<TRMod>();
		
		final Iterator<String> it = keys.iterator();
		while (it.hasNext()) {
			try {
				final String groupName = it.next();
				final String value = cs.getString(groupName);

				if (value == null || value.isEmpty()) continue;
				
				if (value.contains(" ")) {
					Log.Warning.config("Invalid value in PermissionGroups: Invalid value \""+value+"\"!", false);
					continue;
				}
				
				List<TRItem> items = new ArrayList<TRItem>();
				for (String item : value.split(";")) items.addAll(processString(item));
				
				temp.add(new TRMod(groupName, items));
			} catch (Exception ex) {
				Warning.other("Error in PermissionGroups: " + ex.toString(), false);
				Log.Exception(ex, false);
			}
		}
		groups = temp;
			
	}
	
	public static List<TRItem> processModString(String mod){
		mod = mod.toLowerCase();
		for (TRMod m : mods){
			if (m.is(mod)) return m.getItems();
		}
		
		return null;
	}
	
	public static List<TRItem> processModString(String mod, String msg){
		mod = mod.toLowerCase();
		for (TRMod m : mods){
			if (m.is(mod)) return m.getItems(msg);
		}
		
		return null;
	}
	
	public static List<TRItem> processGroupString(String group){
		group = group.toLowerCase();
		for (TRMod g : groups){
			if (g.is(group)) return g.getItems();
		}
		
		return null;
	}
	
	public static List<TRItem> processGroupString(String group, String msg){
		group = group.toLowerCase();
		for (TRMod g : groups){
			if (g.is(group)) return g.getItems(msg);
		}
		
		return null;
	}
	
	public static TRItem processItemnameString(String itemname){
		return NameProcessor.getItem(itemname);
	}
	
	public static List<TRItem> processItemnameString2(String itemname){
		final TRItem it = NameProcessor.getItem(itemname);
		if (it == null) return null;
		final List<TRItem> tbr = new ArrayList<TRItem>(1);
		tbr.add(it);
		return tbr;
	}
	
	public static List<TRItem> processRange(String range, String message) throws TRException{
		int data;
		int m = range.indexOf(":");
		if (m!=-1) {
			final String dataStr = range.substring(m+1);
			if (dataStr.equals("*")) data = -1;
			else {
				try {
					data = Integer.parseInt(dataStr);
					//if (data == 0) data = -10; TODO convert -10
				} catch (Exception ex){
					throw new TRException("Invalid data value: \"" + dataStr + "\" in \"" + range + "\"!");//Throw exception
				}
			}
			
			range = range.substring(0, m);
		} else {
			data = -1;
		}
		
		final String[] temp = range.split("-");
		final int fromId, toId;
		try {
			fromId = Integer.parseInt(temp[0]);
			toId = Integer.parseInt(temp[1]);
		} catch (Exception ex){
			throw new TRException("Invalid range: \"" + range + "\"");
		}
		
		final List<TRItem> tbr = new ArrayList<TRItem>(toId-fromId+1);
		for (int i = fromId; i <= toId; i++) tbr.add(new TRItem(i, data, message));
		
		return tbr;
	}
	
	public static List<TRItem> processSingleData(String item, String message) throws TRException{
		final String[] temp = item.split(":");
		final String dataStr = temp[1];
		
		int data;
		if (dataStr.equals("*")) data = -1;
		else {
			try {
				data = Integer.parseInt(dataStr);
				//if (data == 0) data = -10; TODO Convert -10
			} catch (Exception ex){
				throw new TRException("Invalid data value: \"" + dataStr + "\" in \"" + item + "\"!");//Throw exception
			}
		}
		
		try {
			int id = Integer.parseInt(temp[0]);
			List<TRItem> tbr = new ArrayList<TRItem>(1);
			tbr.add(new TRItem(id, data, message));
			return tbr;															//Single ID:Data
		} catch (Exception ex){
			final TRItem it = processItemnameString(temp[0]);
			if (it != null) {
				it.data = data;
				it.msg = message;
				List<TRItem> tbr = new ArrayList<TRItem>(1);
				tbr.add(it);
				return tbr;														//Single Itemname:Data
			}
			throw new TRException("Invalid id/item: \"" + temp[0] + "\" in \"" + item + "\"!");
		}
	}
	
	public static List<TRItem> processSingle(String item, String message) throws TRException {
		try {
			int id = Integer.parseInt(item);
			List<TRItem> tbr = new ArrayList<TRItem>(1);
			tbr.add(new TRItem(id, -1, message));
			return tbr;															//Single ID
		} catch (Exception ex){
			TRItem it = processItemnameString(item);							//Single Itemname
			if (it != null) {
				it.msg = message;
				List<TRItem> tbr = new ArrayList<TRItem>(1);
				tbr.add(it);
				return tbr;
			} else {
				List<TRItem> tbr = processModString(item, message);				//Mod
				if (tbr != null) return tbr;
				tbr = processGroupString(item, message);						//Group
				if (tbr != null) return tbr;
				
				throw new TRException("Invalid id/item/mod/group: \"" + item + "\"!");
			}
		}
	}
	
	/**
	 * Processes a string of the following kinds:<br>
	 * <table>
	 * <tr><td>Ranges + Damage</td><td>1-5:1</td><td>{@link #processRange(String, String)}</td></tr>
	 * <tr><td>Ranges</td><td>1-5</td><td>{@link #processRange(String, String)}</td></tr>
	 * <tr><td>Single item + Damage</td><td>1:2</td><td>{@link #processSingleData(String, String)}</td></tr>
	 * <tr><td style="padding-right: 5px;">Single item name + Damage</td><td>Stone:2</td><td>{@link #processSingleData(String, String)}</td></tr>
	 * <tr><td>Single item</td><td>3</td><td>{@link #processSingle(String, String)}</td></tr>
	 * <tr><td>Single item name</td><td>Stone</td><td>{@link #processSingle(String, String)}</td></tr>
	 * <tr><td>Mod/Group</td><td style="padding-right: 5px;">EquivalentExchange</td><td>{@link #processSingle(String, String)}</td></tr>
	 * </table>
	 */
	public static List<TRItem> processString(String str) throws TRException {
		final String item;
		final String message;
		int m = str.indexOf('{');
		
		if (m!=-1){
			item = str.substring(0, m).replace(" ", "");
			int j = str.indexOf("}");
			message = Log.replaceColors(str.substring(m+1, j==-1?str.length():j-1));
		} else {
			item = str;
			message = "";
		}
		
		if (item.contains(":-")) return processSingleData(item, message);
		if (item.contains("-")) return processRange(item, message);
		if (item.contains(":")) return processSingleData(item, message);
		return processSingle(item, message);
	}
	
	public static boolean isInRange(@NonNull final String range, final int id, final int data, @NonNull final String perm){
		String itemx = range.replace(":-", ":=");
		if (itemx.contains("-")) {
			// loop through this range and add each to the return stack.
			int data2;
			if (itemx.contains(":")) {
				final String dataString = itemx.split(":")[1];
				if (dataString.equals("*")) data2 = -1;
				else {
					try {
						data2 = Integer.parseInt(dataString.replace("=", "-"));
						//if (data2 == 0) data2 = -10; TODO convert -10
					} catch (NumberFormatException ex){
						Warning.other("You have set an invalid limiter permission \""+perm+"\":", false);
						Warning.other("Invalid data value: \"" + dataString.replace("=", "-") + "\"!", false);
						return false;
					}
				}
				
				itemx = itemx.split(":")[0];
			} else {
				data2 = -1;
			}
			
			if (data2 != -1 && data2 != data) return false;// && !(data2 == -10 && data == 0)) return false; TODO change -10
			
			final String[] t = itemx.split("-");
			final int fromId, toId;
			try {
				fromId = Integer.parseInt(t[0]);
				toId = Integer.parseInt(t[1]);
			} catch (NumberFormatException ex){
				Warning.other("You have set an invalid limiter permission \""+perm+"\":", false);
				Warning.other("Invalid range: \"" + t[0]+"-"+t[1] + "\"", false);
				return false;
			}
			
			if (id >= fromId || id <= toId) return true;
			return false;
		}
		//############################## SINGLE ID WITH DATA ###########################
		else if (itemx.contains(":")) {
			final String[] t = itemx.split(":");
			int id2 = 0, data2 = 0;
			
			try {
				if (t[1].equals("*")) data2 = -1;
				else data2 = Integer.parseInt(t[1].replace('=', '-'));
				
				//if (data2 == 0) data2 = -10; TODO convert -10
			} catch (NumberFormatException ex){
				Warning.other("You have set an invalid limiter permission \""+perm+"\":", false);
				Warning.other("Invalid data value in \""+itemx+"\"!", false);
				return false;
			}
			
			if (data2 != -1 && data2 != data) return false;// && !(data2 == -10 && data == 0)) return false; TODO convert -10
			
			if (t[0].matches("\\d+")){//ID
				try {
					id2 = Integer.parseInt(t[0]);
				} catch (NumberFormatException ex){
					Warning.other("You have set an invalid limiter permission \""+perm+"\":", false);
					Warning.other("Invalid entry: \"" + itemx + "\"!", false);
					return false;
				}
				
				if (id2 == id) return true;
				return false;
			} else {//GROUP / NAME
				boolean found = false;
				
				{
					final List<TRItem> items = TRItemProcessor2.processGroupString(t[0]);
					if (items != null){
						for (final TRItem item : items){
							if (item.compare(id, data)) return true;
						}
						found = true;
					}
				}
				
				
				TRItem item = null;
				try {
					item = processItemnameString(t[0]);
				} catch (TRException ex) {}
				
				if (item != null) return item.compare(id, data);
				
				if (found) return false;
				
				Warning.other("You have set an invalid limiter permission \""+perm+"\":", false);
				Warning.other("\""+t[0]+"\" is not a valid modgroup, permissiongroup, or itemname!", false);
				return false;
			}
		}
		
		//############################## SINGLE ID ###########################
		else {
			final int id2;
			try {
				id2 = Integer.parseInt(itemx);
				if (id2 == id) return true;
				return false;
			} catch (NumberFormatException ex){
				boolean found = false;
				{
					final List<TRItem> items = TRItemProcessor2.processGroupString(itemx);
					if (items != null){
						for (final TRItem item : items){
							if (item.compare(id, data)) return true;
						}
						found = true;
					}
				}
				
				
				TRItem item = null;
				try {
					item = processItemnameString(itemx);
				} catch (TRException ex2) {}
				
				if (item != null) return item.compare(id, data);
				if (found) return false;
				
				Warning.other("You have set an invalid limiter permission \""+perm+"\":", false);
				Warning.other("\""+itemx+"\" is not a valid ID, modgroup, permissiongroup, or itemname!", false);
				return false;
			}
		}
	}
	
	
}
