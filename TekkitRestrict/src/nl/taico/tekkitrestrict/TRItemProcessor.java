package nl.taico.tekkitrestrict;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.configuration.ConfigurationSection;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import nl.taico.tekkitrestrict.Log.Warning;
import nl.taico.tekkitrestrict.functions.TRNoItem;
import nl.taico.tekkitrestrict.objects.TRItem;
import nl.taico.tekkitrestrict.objects.TREnums.ConfigFile;

public class TRItemProcessor {
	//Block ID's Forge: 0-4095
	//Item ID's: 4096-32000
	//ID's: 5 chars
	//Block Dmg values: 0-15 (4bits)
	//Item Dmg values: 0-65536 (2 bytes)
	//Data: 5 chars
	public static ConcurrentHashMap<String, List<TRItem>> groups = new ConcurrentHashMap<String, List<TRItem>>();
	private static final String[] modItems = new String[] {
		"ee|equivalentexchange=27520-27599;126-130",
		"buildcraft=153-174;4056-4066;4298-4324",
		"additionalpipes=4299-4305;179",
		"industrialcraft|ic2=219-223;225-250;30171-30256",
		"nuclearcontrol=192;31256-31260",
		"powerconverters=190",
		"compactsolars=183",
		"chargingbench=187",
		"advancedmachines=253-254;188-191",
		"redpowercore=136",
		"redpowerlogic=138;1258-1328",
		"redpowercontrol=133-134;148",
		"redpowermachine|redpowermachines=137;150-151",
		"redpowerlighting=147",
		"wirelessredstone=177;6358-6363;6406;6408-6412",
		"mffs=253-254;11366-11374",
		"railcraft=206-215;7256-7316",
		"tubestuffs|tubestuff=194",
		"ironchests=19727-19762;181",
		"balkonweaponmod|weaponmod|balkonsweaponmod=26483-26530",
		"enderchest=178;7493",
		"chunkloaders=4095;214;7303;179"
	};

	public static void reload() {
		groups.clear();
		for (final String s : modItems) {
			if (s.contains("=")) {
				final String[] gg = s.split("=");
				final String mod = gg[0];
				if (mod.contains("|")){
					final String[] gg2 = mod.split("\\|");
					for (final String mod2 : gg2){
						try {
							groups.put(mod2, processMultiString(gg[1]));
						} catch (final TRException ex) {
							Warning.config(ex.toString(), false);
						}
					}
				} else {
					try {
						groups.put(mod, processMultiString(gg[1]));
					} catch (final TRException ex) {
						Warning.config(ex.toString(), false);
					}
				}
			}
		}

		// pre-load variables
		final ConfigurationSection cs = tekkitrestrict.config.getConfigurationSection(ConfigFile.GroupPermissions, "PermissionGroups");
		if (cs != null) {
			final Set<String> keys = cs.getKeys(true);
			final Iterator<String> keyIterator = keys.iterator();
			while (keyIterator.hasNext()) {
				try {
					final String groupName = keyIterator.next().toLowerCase();
					final String value = cs.getString(groupName);

					if (value == null || value.isEmpty()) continue;
					
					if (value.contains(" ")) {
						Log.Warning.config("Invalid value in PermissionGroups: Invalid value \""+value+"\"!", false);
						continue;
					}
					groups.put(groupName, processMultiString(value));

				} catch (final Exception ex) {
					Warning.other("Error in PermissionGroups: " + ex.toString(), false);
					Log.Exception(ex, false);
				}
			}
		}
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
						if (data2 == 0) data2 = -10;
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
			
			if (data2 != -1 && data2 != data && !(data2 == -10 && data == 0)) return false;
			
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
				
				if (data2 == 0) data2 = -10;
			} catch (NumberFormatException ex){
				Warning.other("You have set an invalid limiter permission \""+perm+"\":", false);
				Warning.other("Invalid data value in \""+itemx+"\"!", false);
				return false;
			}
			
			if (data2 != -1 && data2 != data && !(data2 == -10 && data == 0)) return false;
			
			if (t[0].matches("\\d+")){//ID
				try {
					id2 = Integer.parseInt(t[0]);
				} catch (final NumberFormatException ex){
					Warning.other("You have set an invalid limiter permission \""+perm+"\":", false);
					Warning.other("Invalid entry: \"" + itemx + "\"!", false);
					return false;
				}
				
				if (id2 == id) return true;
				return false;
			} else {//GROUP / NAME
				boolean found = false;
				List<TRItem> items = groups.get(t[0]);
				if (items != null){
					for (final TRItem item : items){
						if (item.compare(id, data)) return true;
					}
					found = true;
				}
				
				try {
					items = processItemName(t[0], data2);
				} catch (final TRException ex) {}
				
				if (items != null){
					for (final TRItem item : items){
						if (item.compare(id, data)) return true;
					}
					found = true;
				}
				
				if (found) return false;
				
				Warning.other("You have set an invalid limiter permission \""+perm+"\":", false);
				Warning.other("\""+t[0]+"\" is not a valid modgroup, permissiongroup, EE or IC2 itemname!", false);
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
				List<TRItem> items = groups.get(itemx);
				if (items != null){
					for (final TRItem item : items){
						if (item.compare(id, data)) return true;
					}
					found = true;
				}
				
				try {
				items = processItemName(itemx, -1);
				} catch (final TRException ex2) {}
				
				if (items != null){
					for (final TRItem item : items){
						if (item.compare(id, data)) return true;
					}
					found = true;
				}
				
				if (found) return false;
				
				Warning.other("You have set an invalid limiter permission \""+perm+"\":", false);
				Warning.other("\""+itemx+"\" is not a valid ID, modgroup, permissiongroup, EE itemname or IC2 itemname!", false);
				return false;
			}
		}
	}

	/**
	 * Processes an item string that contains ;'s.<br>
	 * This uses {@link #processItemString(String, boolean)} to process each item string.
	 */
	@NonNull private static List<TRItem> processMultiString(@NonNull final String ins) throws TRException {
		if (ins.contains(";")) {
			final String[] itemsStr = ins.split(";");
			final List<TRItem> l = new LinkedList<TRItem>();
			for (String itemStr : itemsStr) {
				l.addAll(processItemString(itemStr));
			}
			return l;
		} else if (ins.length() > 0) {
			return processItemString(ins);
		}
		return new LinkedList<TRItem>();
	}
	/**
	 * Processes an item string that contains ;'s.<br>
	 * This uses {@link #processItemString(String, boolean, String)} to process each item string.
	 */
	@NonNull private static List<TRItem> processMultiString(@NonNull final String ins, @Nullable final String message) throws TRException {
		final String msg;
		if (message == null || message.isEmpty()) msg = "";
		else msg = " {"+message+"}";
		if (ins.contains(";")) {
			String[] itemsStr = ins.split(";");
			List<TRItem> l = new LinkedList<TRItem>();
			for (String itemStr : itemsStr) {
				l.addAll(processItemString(itemStr+msg));
			}
			return l;
		} else if (ins.length() > 0) {
			return processItemString(ins+msg);
		}
		return new LinkedList<TRItem>();
	}

	@Nullable public static List<TRItem> processItemName(@NonNull final String name, final int data) throws TRException {
		List<TRItem> tbr = processIC2Item(name, data);
		if (tbr == null) tbr = processEEItem(name, data);
		
		return tbr;
	}
	@Nullable public static List<TRItem> processIC2Item(@NonNull final String name, final int data) throws TRException {
		final List<TRItem> tbr = new LinkedList<TRItem>();
		switch (name.toLowerCase()){
			case "quantumhelmet":
				tbr.add(TRItem.parseItem(30171, data));
				return tbr;
			case "quantumchestplate":
			case "quantumchest":
			case "quantumbody":
			case "quantumbodyarmor":
				tbr.add(TRItem.parseItem(30172, data));
				return tbr;
			case "quantumleggings":
			case "quantumpants":
			case "quantumlegs":
				tbr.add(TRItem.parseItem(30173, data));
				return tbr;
			case "quantumboots":
			case "quantumshoes":
				tbr.add(TRItem.parseItem(30174, data));
				return tbr;
			
			case "quantumarmor":
			case "quantumsuit":
				return processItemString("30171-30174:"+data);
				
			case "nanohelmet":
				tbr.add(TRItem.parseItem(30178, data));
				return tbr;
			case "nanochestplate":
			case "nanochest":
			case "nanobody":
			case "nanobodyarmor":
				tbr.add(TRItem.parseItem(30177, data));
				return tbr;
			case "nanoleggings":
			case "nanolegs":
			case "nanopants":
				tbr.add(TRItem.parseItem(30176, data));
				return tbr;
			case "nanoboots":
			case "nanoshoes":
				tbr.add(TRItem.parseItem(30175, data));
				return tbr;
				
			case "nanoarmor":
			case "nanosuit":
				return processItemString("30175-30178:"+data);
				
			case "jetpack":
			case "electricjetpack":
				tbr.add(TRItem.parseItem(30209, data));
				return tbr;
				
			case "batpack":
			case "batterypack":
				tbr.add(TRItem.parseItem(30180, data));
				return tbr;
			case "lappack":
				tbr.add(TRItem.parseItem(30127, data));
				return tbr;
				
			case "chainsaw":
				tbr.add(TRItem.parseItem(30233, data));
				return tbr;
			case "miningdrill":
			case "drill":
				tbr.add(TRItem.parseItem(30235, data));
				return tbr;
			case "ddrill":
			case "diamonddrill":
				tbr.add(TRItem.parseItem(30234, data));
				return tbr;
				
			case "electrichoe":
				tbr.add(TRItem.parseItem(30119, data));
				return tbr;
			case "electricwrench":
				tbr.add(TRItem.parseItem(30141, data));
				return tbr;
			case "electrictreetap":
				tbr.add(TRItem.parseItem(30124, data));
				return tbr;
				
			case "nanosaber":
				tbr.add(TRItem.parseItem(30148, data));
				return tbr;
				
			case "mininglaser":
				tbr.add(TRItem.parseItem(30208, data));
				return tbr;
				
			case "rebattery":
			case "re-battery":
				tbr.add(TRItem.parseItem(30242, data));
				return tbr;
			case "energycrystal":
				tbr.add(TRItem.parseItem(30241, data));
				return tbr;
			case "lapatronctrystal":
				tbr.add(TRItem.parseItem(30240, data));
				return tbr;
				
			case "scanner":
			case "od-scanner":
			case "odscanner":
				tbr.add(TRItem.parseItem(30220, data));
				return tbr;
			case "ov-scanner":
			case "ovscanner":
				tbr.add(TRItem.parseItem(30219, data));
				return tbr;
				
			case "digitalthermometer":
				tbr.add(TRItem.parseItem(31257, data));
				return tbr;
				
			default:
				return null;
		}
	}
	@Nullable public static List<TRItem> processEEItem(@NonNull final String name, final int data) throws TRException {
		final List<TRItem> tbr = new LinkedList<TRItem>();
		switch (name.toLowerCase()){
			case "dmpickaxe":
				tbr.add(TRItem.parseItem(27543, data));
				return tbr;
			case "dmspade":
			case "dmshovel":
				tbr.add(TRItem.parseItem(27544, data));
				return tbr;
			case "dmhoe":
				tbr.add(TRItem.parseItem(27545, data));
				return tbr;
			case "dmsword":
				tbr.add(TRItem.parseItem(27546, data));
				return tbr;
			case "dmaxe":
				tbr.add(TRItem.parseItem(27547, data));
				return tbr;
			case "dmshears":
				tbr.add(TRItem.parseItem(27548, data));
				return tbr;
			case "dmhammer":
				tbr.add(TRItem.parseItem(27555, data));
				return tbr;
			
			case "dmtools":
				return processMultiString("27543-27548:"+data+";27555:"+data);
	
			case "rmpickaxe":
				tbr.add(TRItem.parseItem(27564, data));
				return tbr;
			case "rmshovel":
			case "rmspade":
				tbr.add(TRItem.parseItem(27565, data));
				return tbr;
			case "rmhoe":
				tbr.add(TRItem.parseItem(27566, data));
				return tbr;
			case "rmsword":
				tbr.add(TRItem.parseItem(27567, data));
				return tbr;
			case "rmaxe":
				tbr.add(TRItem.parseItem(27568, data));
				return tbr;
			case "rmshears":
				tbr.add(TRItem.parseItem(27569, data));
				return tbr;
			case "rmhammer":
				tbr.add(TRItem.parseItem(27570, data));
				return tbr;
			
			case "rmtools":
				return processItemString("27564-27570:"+data);
			
			case "rmkatar": 
			case "redkatar": 
			case "katar":
				tbr.add(TRItem.parseItem(27572, data));
				return tbr;
			case "rmmorningstar":
			case "redmorningstar":
			case "morningstar":
				tbr.add(TRItem.parseItem(27573, data));
				return tbr;
			
			case "destructioncatalyst":
				tbr.add(TRItem.parseItem(27527, data));
				return tbr;
			case "hyperkineticlens": 
			case "hyperlens":
				tbr.add(TRItem.parseItem(27535, data));
				return tbr;
			case "cataclycticlens": 
			case "catalyticlens":
				tbr.add(TRItem.parseItem(27556, data));
				return tbr;
			
			case "evertideamulet":
				tbr.add(TRItem.parseItem(27530, data));
				return tbr;
			case "volcaniteamulet":
				tbr.add(TRItem.parseItem(27531, data));
				return tbr;
			
			case "amulets":
				return processItemString("27530-27531:"+data);
			
			case "zeroring": 
			case "freezering": 
			case "snowring":
				tbr.add(TRItem.parseItem(27574, data));
				return tbr;
			case "ringofignition": 
			case "ignitionring": 
			case "firering":
				tbr.add(TRItem.parseItem(27533, data));
				return tbr;
			
			case "ringofarcana": 
			case "arcanaring":
				tbr.add(TRItem.parseItem(27584, data));
				return tbr;
			case "voidring":
				tbr.add(TRItem.parseItem(27593, data));
				return tbr;
			case "harvestring": 
			case "harvestgodessband":
				tbr.add(TRItem.parseItem(27537, data));
				return tbr;
			case "blackholeband": 
			case "bhb":
				tbr.add(TRItem.parseItem(27532, data));
				return tbr;
			case "archangelsmite": 
			case "archangelssmite": 
			case "archangelring": 
			case "archangelsring":
				tbr.add(TRItem.parseItem(27534, data));
				return tbr;
			case "swiftwolfsrendinggale": 
			case "swiftwolfrendinggale": 
			case "swiftwolfsring":
			case "swiftwolfring":
			case "SWRG":
				tbr.add(TRItem.parseItem(27536, data));
				return tbr;
			
			case "rings":
				return processMultiString("27532-27534:"+data+";27536:"+data+";27537:"+data+";27574:"+data+";27584:"+data+";27593:"+data);
			
			case "philosopherstone":
			case "philosophersstone":
				tbr.add(TRItem.parseItem(27526, data));
				return tbr;
			case "watchofflowingtime": 
			case "watchoftime": 
			case "watch":
				tbr.add(TRItem.parseItem(27538, data));
				return tbr;
			case "mercurialeye":
				tbr.add(TRItem.parseItem(27583, data));
				return tbr;
			
			case "dmchest": 
			case "dmchestplate":
				tbr.add(TRItem.parseItem(27549, data));
				return tbr;
			case "dmhelmet":
				tbr.add(TRItem.parseItem(27550, data));
				return tbr;
			case "dmleggings": 
			case "dmgreaves":
				tbr.add(TRItem.parseItem(27551, data));
				return tbr;
			case "dmboots":
				tbr.add(TRItem.parseItem(27552, data));
				return tbr;
			
			case "dmarmor":
			case "dmsuit":
				return processItemString("27549-27552:"+data);
			
			case "rmchest":
			case "rmchestplate":
				tbr.add(TRItem.parseItem(27575, data));
				return tbr;
			case "rmhelmet":
				tbr.add(TRItem.parseItem(27576, data));
				return tbr;
			case "rmleggings": 
			case "rmgreaves":
				tbr.add(TRItem.parseItem(27577, data));
				return tbr;
			case "rmboots":
				tbr.add(TRItem.parseItem(27578, data));
				return tbr;
			
			case "rmarmor":
			case "rmsuit":
				return processItemString("27575-27578:"+data);
			
			case "infernalarmor":
			case "gemchest":
			case "gemchestplate":
				tbr.add(TRItem.parseItem(27579, data));
				return tbr;
			case "abysshelmet":
			case "gemhelmet":
				tbr.add(TRItem.parseItem(27580, data));
				return tbr;
			case "gravitygreaves": 
			case "gemgreaves": 
			case "gemleggings":
				tbr.add(TRItem.parseItem(27581, data));
				return tbr;
			case "hurricaneboots": 
			case "gemboots":
				tbr.add(TRItem.parseItem(27582, data));
				return tbr;
			
			case "gemarmor":
			case "gemsuit":
				return processItemString("27579-27582:"+data);
			
			case "gemofeternaldensity":
				tbr.add(TRItem.parseItem(27553, data));
				return tbr;
			case "repairtalisman":
				tbr.add(TRItem.parseItem(27554, data));
				return tbr;
			
			case "soulstone":
				tbr.add(TRItem.parseItem(27529, data));
				return tbr;
			case "bodystone":
				tbr.add(TRItem.parseItem(27588, data));
				return tbr;
			case "lifestone":
				tbr.add(TRItem.parseItem(27589, data));
				return tbr;
			case "mindstone":
				tbr.add(TRItem.parseItem(27590, data));
				return tbr;
			
			case "diviningrod":
				tbr.add(TRItem.parseItem(27585, data));
				return tbr;
			
			case "transmutationtablet":
				tbr.add(TRItem.parseItem(27592, data));
				return tbr;
			
			case "kleinstarein": 
			case "kleinstar1":
				tbr.add(TRItem.parseItem(27557, data));
				return tbr;
			case "kleinstarzwei": 
			case "kleinstar2":
				tbr.add(TRItem.parseItem(27558, data));
				return tbr;
			case "kleinstardrei": 
			case "kleinstar3":
				tbr.add(TRItem.parseItem(27559, data));
				return tbr;
			case "kleinstarvier": 
			case "kleinstar4":
				tbr.add(TRItem.parseItem(27560, data));
				return tbr;
			case "kleinstarsphere": 
			case "kleinstar5":
				tbr.add(TRItem.parseItem(27561, data));
				return tbr;
			case "kleinstaromega": 
			case "kleinstar6":
				tbr.add(TRItem.parseItem(27591, data));
				return tbr;
			case "alchemybag":
				tbr.add(TRItem.parseItem(27562, data));
				return tbr;
			
			default:
				return null;
		}
	}
	@Nullable private static List<TRItem> processItemName(@NonNull final String name, final int data, @Nullable final String message) throws TRException {
		List<TRItem> tbr = processIC2Item(name, data, message);
		if (tbr == null) tbr = processEEItem(name, data, message);
		
		return tbr;
	}
	@Nullable private static List<TRItem> processIC2Item(@NonNull final String name, final int data, @Nullable final String message) throws TRException {
		final String msg;
		if (message == null || message.isEmpty()) msg = "";
		else msg = " {"+message+"}";
		final List<TRItem> tbr = new LinkedList<TRItem>();
		switch (name.toLowerCase()){
			case "quantumhelmet":
				tbr.add(TRItem.parseItem(30171, data, msg));
				return tbr;
			case "quantumchestplate":
			case "quantumchest":
			case "quantumbody":
			case "quantumbodyarmor":
				tbr.add(TRItem.parseItem(30172, data, msg));
				return tbr;
			case "quantumleggings":
			case "quantumpants":
			case "quantumlegs":
				tbr.add(TRItem.parseItem(30173, data, msg));
				return tbr;
			case "quantumboots":
			case "quantumshoes":
				tbr.add(TRItem.parseItem(30174, data, msg));
				return tbr;
			
			case "quantumarmor":
			case "quantumsuit":
				return processItemString("30171-30174:"+data+msg);
				
			case "nanohelmet":
				tbr.add(TRItem.parseItem(30178, data, msg));
				return tbr;
			case "nanochestplate":
			case "nanochest":
			case "nanobody":
			case "nanobodyarmor":
				tbr.add(TRItem.parseItem(30177, data, msg));
				return tbr;
			case "nanoleggings":
			case "nanolegs":
			case "nanopants":
				tbr.add(TRItem.parseItem(30176, data, msg));
				return tbr;
			case "nanoboots":
			case "nanoshoes":
				tbr.add(TRItem.parseItem(30175, data, msg));
				return tbr;
				
			case "nanoarmor":
			case "nanosuit":
				return processItemString("30175-30178:"+data+msg);
				
			case "jetpack":
			case "electricjetpack":
				tbr.add(TRItem.parseItem(30209, data, msg));
				return tbr;
				
			case "batpack":
			case "batterypack":
				tbr.add(TRItem.parseItem(30180, data, msg));
				return tbr;
			case "lappack":
				tbr.add(TRItem.parseItem(30127, data, msg));
				return tbr;
				
			case "chainsaw":
				tbr.add(TRItem.parseItem(30233, data, msg));
				return tbr;
			case "miningdrill":
			case "drill":
				tbr.add(TRItem.parseItem(30235, data, msg));
				return tbr;
			case "ddrill":
			case "diamonddrill":
				tbr.add(TRItem.parseItem(30234, data, msg));
				return tbr;
				
			case "electrichoe":
				tbr.add(TRItem.parseItem(30119, data, msg));
				return tbr;
			case "electricwrench":
				tbr.add(TRItem.parseItem(30141, data, msg));
				return tbr;
			case "electrictreetap":
				tbr.add(TRItem.parseItem(30124, data, msg));
				return tbr;
				
			case "nanosaber":
				tbr.add(TRItem.parseItem(30148, data, msg));
				return tbr;
				
			case "mininglaser":
				tbr.add(TRItem.parseItem(30208, data, msg));
				return tbr;
				
			case "rebattery":
			case "re-battery":
				tbr.add(TRItem.parseItem(30242, data, msg));
				return tbr;
			case "energycrystal":
				tbr.add(TRItem.parseItem(30241, data, msg));
				return tbr;
			case "lapatronctrystal":
				tbr.add(TRItem.parseItem(30240, data, msg));
				return tbr;
				
			case "scanner":
			case "od-scanner":
			case "odscanner":
				tbr.add(TRItem.parseItem(30220, data, msg));
				return tbr;
			case "ov-scanner":
			case "ovscanner":
				tbr.add(TRItem.parseItem(30219, data, msg));
				return tbr;
				
			case "digitalthermometer":
				tbr.add(TRItem.parseItem(31257, data, msg));
				return tbr;
				
			default:
				return null;
		}
	}
	@Nullable private static List<TRItem> processEEItem(@NonNull final String name, final int data, @Nullable final String message) throws TRException {
		final List<TRItem> tbr = new LinkedList<TRItem>();
		final String msg;
		if (message == null || message.isEmpty()) msg = "";
		else msg = " {"+message+"}";
		switch (name.toLowerCase()){
			case "dmpickaxe":
				tbr.add(TRItem.parseItem(27543, data, msg));
				return tbr;
			case "dmspade":
			case "dmshovel":
				tbr.add(TRItem.parseItem(27544, data, msg));
				return tbr;
			case "dmhoe":
				tbr.add(TRItem.parseItem(27545, data, msg));
				return tbr;
			case "dmsword":
				tbr.add(TRItem.parseItem(27546, data, msg));
				return tbr;
			case "dmaxe":
				tbr.add(TRItem.parseItem(27547, data, msg));
				return tbr;
			case "dmshears":
				tbr.add(TRItem.parseItem(27548, data, msg));
				return tbr;
			case "dmhammer":
				tbr.add(TRItem.parseItem(27555, data, msg));
				return tbr;
			
			case "dmtools":
				return processMultiString("27543-27548:"+data+";27555:"+data, msg);
	
			case "rmpickaxe":
				tbr.add(TRItem.parseItem(27564, data, msg));
				return tbr;
			case "rmshovel":
			case "rmspade":
				tbr.add(TRItem.parseItem(27565, data, msg));
				return tbr;
			case "rmhoe":
				tbr.add(TRItem.parseItem(27566, data, msg));
				return tbr;
			case "rmsword":
				tbr.add(TRItem.parseItem(27567, data, msg));
				return tbr;
			case "rmaxe":
				tbr.add(TRItem.parseItem(27568, data, msg));
				return tbr;
			case "rmshears":
				tbr.add(TRItem.parseItem(27569, data, msg));
				return tbr;
			case "rmhammer":
				tbr.add(TRItem.parseItem(27570, data, msg));
				return tbr;
			
			case "rmtools":
				return processItemString("27564-27570:"+data+msg);
			
			case "rmkatar": 
			case "redkatar": 
			case "katar":
				tbr.add(TRItem.parseItem(27572, data, msg));
				return tbr;
			case "rmmorningstar":
			case "redmorningstar":
			case "morningstar":
				tbr.add(TRItem.parseItem(27573, data, msg));
				return tbr;
			
			case "destructioncatalyst":
				tbr.add(TRItem.parseItem(27527, data, msg));
				return tbr;
			case "hyperkineticlens": 
			case "hyperlens":
				tbr.add(TRItem.parseItem(27535, data, msg));
				return tbr;
			case "cataclycticlens": 
			case "catalyticlens":
				tbr.add(TRItem.parseItem(27556, data, msg));
				return tbr;
			
			case "evertideamulet":
				tbr.add(TRItem.parseItem(27530, data, msg));
				return tbr;
			case "volcaniteamulet":
				tbr.add(TRItem.parseItem(27531, data, msg));
				return tbr;
			
			case "amulets":
				return processItemString("27530-27531:"+data+msg);
			
			case "zeroring": 
			case "freezering": 
			case "snowring":
				tbr.add(TRItem.parseItem(27574, data, msg));
				return tbr;
			case "ringofignition": 
			case "ignitionring": 
			case "firering":
				tbr.add(TRItem.parseItem(27533, data, msg));
				return tbr;
			
			case "ringofarcana": 
			case "arcanaring":
				tbr.add(TRItem.parseItem(27584, data, msg));
				return tbr;
			case "voidring":
				tbr.add(TRItem.parseItem(27593, data, msg));
				return tbr;
			case "harvestring": 
			case "harvestgodessband":
				tbr.add(TRItem.parseItem(27537, data, msg));
				return tbr;
			case "blackholeband": 
			case "bhb":
				tbr.add(TRItem.parseItem(27532, data, msg));
				return tbr;
			case "archangelsmite": 
			case "archangelssmite": 
			case "archangelring": 
			case "archangelsring":
				tbr.add(TRItem.parseItem(27534, data, msg));
				return tbr;
			case "swiftwolfsrendinggale": 
			case "swiftwolfrendinggale": 
			case "swiftwolfsring":
			case "swiftwolfring":
			case "SWRG":
				tbr.add(TRItem.parseItem(27536, data, msg));
				return tbr;
			
			case "rings":
				return processMultiString("27532-27534:"+data+";27536:"+data+";27537:"+data+";27574:"+data+";27584:"+data+";27593:"+data, msg);
			
			case "philosopherstone":
			case "philosophersstone":
				tbr.add(TRItem.parseItem(27526, data, msg));
				return tbr;
			case "watchofflowingtime": 
			case "watchoftime": 
			case "watch":
				tbr.add(TRItem.parseItem(27538, data, msg));
				return tbr;
			case "mercurialeye":
				tbr.add(TRItem.parseItem(27583, data, msg));
				return tbr;
			
			case "dmchest": 
			case "dmchestplate":
				tbr.add(TRItem.parseItem(27549, data, msg));
				return tbr;
			case "dmhelmet":
				tbr.add(TRItem.parseItem(27550, data, msg));
				return tbr;
			case "dmleggings": 
			case "dmgreaves":
				tbr.add(TRItem.parseItem(27551, data, msg));
				return tbr;
			case "dmboots":
				tbr.add(TRItem.parseItem(27552, data, msg));
				return tbr;
			
			case "dmarmor":
			case "dmsuit":
				return processItemString("27549-27552:"+data+msg);
			
			case "rmchest":
			case "rmchestplate":
				tbr.add(TRItem.parseItem(27575, data, msg));
				return tbr;
			case "rmhelmet":
				tbr.add(TRItem.parseItem(27576, data, msg));
				return tbr;
			case "rmleggings": 
			case "rmgreaves":
				tbr.add(TRItem.parseItem(27577, data, msg));
				return tbr;
			case "rmboots":
				tbr.add(TRItem.parseItem(27578, data, msg));
				return tbr;
			
			case "rmarmor":
			case "rmsuit":
				return processItemString("27575-27578:"+data+msg);
			
			case "infernalarmor":
			case "gemchest":
			case "gemchestplate":
				tbr.add(TRItem.parseItem(27579, data, msg));
				return tbr;
			case "abysshelmet":
			case "gemhelmet":
				tbr.add(TRItem.parseItem(27580, data, msg));
				return tbr;
			case "gravitygreaves": 
			case "gemgreaves": 
			case "gemleggings":
				tbr.add(TRItem.parseItem(27581, data, msg));
				return tbr;
			case "hurricaneboots": 
			case "gemboots":
				tbr.add(TRItem.parseItem(27582, data, msg));
				return tbr;
			
			case "gemarmor":
			case "gemsuit":
				return processItemString("27579-27582:"+data+msg);
			
			case "gemofeternaldensity":
				tbr.add(TRItem.parseItem(27553, data, msg));
				return tbr;
			case "repairtalisman":
				tbr.add(TRItem.parseItem(27554, data, msg));
				return tbr;
			
			case "soulstone":
				tbr.add(TRItem.parseItem(27529, data, msg));
				return tbr;
			case "bodystone":
				tbr.add(TRItem.parseItem(27588, data, msg));
				return tbr;
			case "lifestone":
				tbr.add(TRItem.parseItem(27589, data, msg));
				return tbr;
			case "mindstone":
				tbr.add(TRItem.parseItem(27590, data, msg));
				return tbr;
			
			case "diviningrod":
				tbr.add(TRItem.parseItem(27585, data, msg));
				return tbr;
			
			case "transmutationtablet":
				tbr.add(TRItem.parseItem(27592, data, msg));
				return tbr;
			
			case "kleinstarein": 
			case "kleinstar1":
				tbr.add(TRItem.parseItem(27557, data, msg));
				return tbr;
			case "kleinstarzwei": 
			case "kleinstar2":
				tbr.add(TRItem.parseItem(27558, data, msg));
				return tbr;
			case "kleinstardrei": 
			case "kleinstar3":
				tbr.add(TRItem.parseItem(27559, data, msg));
				return tbr;
			case "kleinstarvier": 
			case "kleinstar4":
				tbr.add(TRItem.parseItem(27560, data, msg));
				return tbr;
			case "kleinstarsphere": 
			case "kleinstar5":
				tbr.add(TRItem.parseItem(27561, data, msg));
				return tbr;
			case "kleinstaromega": 
			case "kleinstar6":
				tbr.add(TRItem.parseItem(27591, data, msg));
				return tbr;
			case "alchemybag":
				tbr.add(TRItem.parseItem(27562, data, msg));
				return tbr;
			
			default:
				return null;
		}
	}
	
	public static int getIdFromIC2Name(@NonNull final String name){
		switch (name.toLowerCase()){
			case "quantumhelmet":
				return 30171;
			case "quantumchestplate":
			case "quantumchest":
			case "quantumbody":
			case "quantumbodyarmor":
				return 30172;
			case "quantumleggings":
			case "quantumpants":
			case "quantumlegs":
				return 30173;
			case "quantumboots":
			case "quantumshoes":
				return 30174;
				
			case "nanohelmet":
				return 30178;
			case "nanochestplate":
			case "nanochest":
			case "nanobody":
			case "nanobodyarmor":
				return 30177;
			case "nanoleggings":
			case "nanolegs":
			case "nanopants":
				return 30176;
			case "nanoboots":
			case "nanoshoes":
				return 30175;
				
			case "jetpack":
			case "electricjetpack":
				return 30209;
				
			case "batpack":
			case "batterypack":
				return 30180;
			case "lappack":
				return 30127;
				
			case "chainsaw":
				return 30233;
			case "miningdrill":
			case "drill":
				return 30235;
			case "ddrill":
			case "diamonddrill":
				return 30234;
				
			case "electrichoe":
				return 30119;
			case "electricwrench":
				return 30141;
			case "electrictreetap":
				return 30124;
				
			case "nanosaber":
				return 30148;
				
			case "mininglaser":
				return 30208;
				
			case "rebattery":
			case "re-battery":
				return 30242;
			case "energycrystal":
				return 30241;
			case "lapatronctrystal":
				return 30240;
				
			case "scanner":
			case "od-scanner":
			case "odscanner":
				return 30220;
			case "ov-scanner":
			case "ovscanner":
				return 30219;
				
			case "digitalthermometer":
				return 31257;
				
			default:
				return -1;
		}
	}

	/**
	 * Processes an item string of the following kinds:<br>
	 * - Ranges + 1 Damage value (1-5:1)<br>
	 * - Ranges without damage value (1-5)<br>
	 * - Single item + 1 Damage value (1:2)<br>
	 * - Single item without damage value (3)<br>
	 * - Group names (data is always *) (ee)<br>
	 * - EE2 and IC2 item names + 1 Damage value (rmaxe:1)<br>
	 * - EE2 and IC2 item names without damage value (quantumhelmet)<br>
	 */
	@NonNull public static List<TRItem> processItemString(@NonNull final String item) throws TRException {
		String itemx;
		String message = "";
		if (item.contains("{")){
			String temp[] = item.split("\\{");
			itemx = temp[0].replace(" ", "");
			message = temp[1].replace("}", "");
			message = Log.replaceColors(message);
		} else {
			itemx = item;
			//message = ChatColor.RED + "You are not allowed to modify/obtain this item!";
		}
		itemx = itemx.toLowerCase().replace(":-", ":=");
		final LinkedList<TRItem> tci = new LinkedList<TRItem>();
		if (itemx.contains(";")){
			throw new TRException("You cannot use ; to separate items in a single item string. You can only use ranges, single items, itemnames or groups.");
		}
		// converts a variable string into a list of data.

		//############################## RANGE OF ITEMS ###########################
		if (itemx.contains("-")) {
			// loop through this range and add each to the return stack.
			int data = 0;
			if (itemx.contains(":")) {
				final String dataString = itemx.split(":")[1];
				if (dataString.equals("*")) data = -1;
				else {
					try {
						data = Integer.parseInt(dataString.replace("=", "-"));
						if (data == 0) data = -10;
					} catch (final NumberFormatException ex){
						throw new TRException("Invalid data value: \"" + dataString + "\" in \"" + itemx + "\"!");//Throw exception
					}
				}
				
				itemx = itemx.split(":")[0];
			} else {
				data = -1;
			}
			
			final String[] t = itemx.split("-");
			final int fromId, toId;
			try {
				fromId = Integer.parseInt(t[0]);
				toId = Integer.parseInt(t[1]);
			} catch (final NumberFormatException ex){
				throw new TRException("Invalid range: \"" + t[0]+"-"+t[1] + "\"");
			}

			for (int i = fromId; i <= toId; i++) {
				tci.add(TRItem.parseItem(i, data, message));
			}
			return tci;
		}
		
		//############################## SINGLE ID WITH DATA ###########################
		else if (itemx.contains(":")) {
			final String[] t = itemx.split(":");
			int id = 0, data = 0;
			
			try {
				if (t[1].equals("*"))
					data = -1;
				else
					data = Integer.parseInt(t[1].replace('=', '-'));
				
			} catch (final NumberFormatException ex){
				throw new TRException("Invalid data value in \""+itemx+"\"!");
			}
			
			if (data == 0) { //If :0, then :-10
				data = -10;
			}
			
			if (t[0].matches("\\d+")){//ID
				try {
					id = Integer.parseInt(t[0]);
				} catch (final NumberFormatException ex){
					throw new TRException("Invalid entry: \"" + itemx + "\"!");
				}
				
				tci.add(TRItem.parseItem(id, data, message));
				return tci;
			} else {//GROUP / NAME
				List<TRItem> items = groups.get(t[0]);
				
				if (items != null){
					for (final TRItem it : items){
						final TRItem it2 = (TRItem) it.clone();
						it2.msg = message;
						tci.add(it2);
					}
					return tci; //All :*
				}
				
				items = processItemName(t[0], data, message);
				if (items != null) return items;
				
				final TRItem it = NameProcessor.getItem(t[0]);
				if (it != null){
					tci.add(it);
					return tci;
				}
				
				throw new TRException("\""+t[0]+"\" is not a valid modgroup, permissiongroup or itemname!");
			}
		}
		
		//############################## ALL ITEMS ###########################
		else if(itemx.equals("*")) {
			throw new TRException("Using * to define all items is not supported. Please give the permission tekkitrestrict.[feature].blockall instead.");
		}
		
		//############################## SINGLE ID ###########################
		else {
			final int id;
			try {
				id = Integer.parseInt(itemx);
			} catch (NumberFormatException ex){
				List<TRItem> items = groups.get(itemx);
				if (items != null){
					for (final TRItem it : items){
						final TRItem it2 = (TRItem) it.clone();
						it2.msg = message;
						tci.add(it2);
					}
					return tci; //All :*
				}
				
				final TRItem it = NameProcessor.getItem(itemx);
				if (it != null){
					tci.add(it);
					return tci;
				}
				
				items = processItemName(itemx, -1, message);
				if (items != null) return items;
				
				throw new TRException("\""+itemx+"\" is not a valid ID, modgroup, permissiongroup or itemname!");
			}

			tci.add(TRItem.parseItem(id, -1, message));
			return tci;
		}
	}

	@NonNull public static List<TRItem> processNoItemString(@NonNull final String item) throws TRException {
		String itemx;
		String message = "";
		if (item.contains("{")){
			String temp[] = item.split("\\{");
			itemx = temp[0].replace(" ", "");
			message = temp[1].replace("}", "");
			message = Log.replaceColors(message);
		} else {
			itemx = item;
			//message = ChatColor.RED + "You are not allowed to modify/obtain this item!";
		}
		itemx = itemx.toLowerCase().replace(":-", ":=");
		final LinkedList<TRItem> tci = new LinkedList<TRItem>();
		if (itemx.contains(";")){
			throw new TRException("You cannot use ; to separate items in a single item string. You can only use ranges, single items, itemnames or groups.");
		}
		// converts a variable string into a list of data.

		//############################## RANGE OF ITEMS ###########################
		if (itemx.contains("-")) {
			// loop through this range and add each to the return stack.
			int data = 0;
			if (itemx.contains(":")) {
				final String dataString = itemx.split(":")[1];
				if (dataString.equals("*")) data = -1;
				else {
					try {
						data = Integer.parseInt(dataString.replace("=", "-"));
						if (data == 0) data = -10;
					} catch (final NumberFormatException ex){
						throw new TRException("Invalid data value: \"" + dataString + "\" in \"" + itemx + "\"!");//Throw exception
					}
				}
				
				itemx = itemx.split(":")[0];
			} else {
				data = -1;
			}
			
			final String[] t = itemx.split("-");
			final int fromId, toId;
			try {
				fromId = Integer.parseInt(t[0]);
				toId = Integer.parseInt(t[1]);
			} catch (final NumberFormatException ex){
				throw new TRException("Invalid range: \"" + t[0]+"-"+t[1] + "\"");
			}

			for (int i = fromId; i <= toId; i++) {
				tci.add(TRItem.parseItem(i, data, message));
			}
			return tci;
		}
		
		//############################## SINGLE ID WITH DATA ###########################
		else if (itemx.contains(":")) {
			final String[] t = itemx.split(":");
			int id = 0, data = 0;
			
			try {
				if (t[1].equals("*"))
					data = -1;
				else
					data = Integer.parseInt(t[1].replace('=', '-'));
				
			} catch (final NumberFormatException ex){
				throw new TRException("Invalid data value in \""+itemx+"\"!");
			}
			
			if (data == 0) { //If :0, then :-10
				data = -10;
			}
			
			if (t[0].matches("\\d+")){//ID
				try {
					id = Integer.parseInt(t[0]);
				} catch (NumberFormatException ex){
					throw new TRException("Invalid entry: \"" + itemx + "\"!");
				}
				
				tci.add(TRItem.parseItem(id, data, message));
				return tci;
			} else {//GROUP / NAME
				List<TRItem> items = groups.get(t[0]);
				
				if (items != null){
					for (final TRItem it : items){
						final TRItem it2 = (TRItem) it.clone();
						it2.msg = message;
						tci.add(it2);
					}
					TRNoItem.DisabledItemGroups.add(t[0]);
					return tci; //All :*
				}
				
				items = processItemName(t[0], data, message);
				if (items != null) return items;
				
				final TRItem it = NameProcessor.getItem(t[0]);
				if (it != null){
					tci.add(it);
					return tci;
				}
				
				throw new TRException("\""+t[0]+"\" is not a valid modgroup, permissiongroup or itemname!");
			}
		}
		
		//############################## ALL ITEMS ###########################
		else if(itemx.equals("*")) {
			throw new TRException("Using * to define all items is not supported. Please give the permission tekkitrestrict.[feature].blockall instead.");
		}
		
		//############################## SINGLE ID ###########################
		else {
			final int id;
			try {
				id = Integer.parseInt(itemx);
			} catch (NumberFormatException ex){
				List<TRItem> items = groups.get(itemx);
				if (items != null){
					for (final TRItem it : items){
						final TRItem it2 = (TRItem) it.clone();
						it2.msg = message;
						tci.add(it2);
					}
					TRNoItem.DisabledItemGroups.add(itemx);
					return tci; //All :*
				}
				
				items = processItemName(itemx, -1, message);
				if (items != null) return items;
				
				final TRItem it = NameProcessor.getItem(itemx);
				if (it != null){
					tci.add(it);
					return tci;
				}
				
				throw new TRException("\""+itemx+"\" is not a valid ID, modgroup, permissiongroup or itemname!");
			}

			tci.add(TRItem.parseItem(id, -1, message));
			return tci;
		}
	}

	
	
	/**
	 * Processes an item string of the following kinds:<br>
	 * - Ranges + 1 Damage value (1-5:1)<br>
	 * - Ranges without damage value (1-5)<br>
	 * - Single item + 1 Damage value (1:2)<br>
	 * - Single item without damage value (3)<br>
	 * - Group names (data is always *) (ee)<br>
	 * - EE2 and IC2 item names + 1 Damage value (rmaxe:1)<br>
	 * - EE2 and IC2 item names without damage value (quantumhelmet)<br>
	 */
	@NonNull public static List<TRItem> processItemStringAndAddToLIst(@NonNull final String item, @Nullable final List<String> addedGroups) throws TRException {
		String itemx;
		String message = "";
		if (item.contains("{")){
			String temp[] = item.split("\\{");
			itemx = temp[0].replace(" ", "");
			message = temp[1].replace("}", "");
			message = Log.replaceColors(message);
		} else {
			itemx = item;
			//message = ChatColor.RED + "You are not allowed to modify/obtain this item!";
		}
		itemx = itemx.toLowerCase().replace(":-", ":=");
		final LinkedList<TRItem> tci = new LinkedList<TRItem>();
		if (itemx.contains(";")){
			throw new TRException("You cannot use ; to separate items in a single item string. You can only use ranges, single items, itemnames or groups.");
		}
		// converts a variable string into a list of data.

		//############################## RANGE OF ITEMS ###########################
		if (itemx.contains("-")) {
			// loop through this range and add each to the return stack.
			int data = 0;
			if (itemx.contains(":")) {
				final String dataString = itemx.split(":")[1];
				if (dataString.equals("*")) data = -1;
				else {
					try {
						data = Integer.parseInt(dataString.replace("=", "-"));
						if (data == 0) data = -10;
					} catch (NumberFormatException ex){
						throw new TRException("Invalid data value: \"" + dataString + "\" in \"" + itemx + "\"!");//Throw exception
					}
				}
				
				itemx = itemx.split(":")[0];
			} else {
				data = -1;
			}
			
			final String[] t = itemx.split("-");
			final int fromId, toId;
			try {
				fromId = Integer.parseInt(t[0]);
				toId = Integer.parseInt(t[1]);
			} catch (NumberFormatException ex){
				throw new TRException("Invalid range: \"" + t[0]+"-"+t[1] + "\"");
			}

			for (int i = fromId; i <= toId; i++) {
				tci.add(TRItem.parseItem(i, data, message));
			}
			return tci;
		}
		
		//############################## SINGLE ID WITH DATA ###########################
		else if (itemx.contains(":")) {
			final String[] t = itemx.split(":");
			int id = 0, data = 0;
			
			try {
				if (t[1].equals("*"))
					data = -1;
				else
					data = Integer.parseInt(t[1].replace('=', '-'));
				
			} catch (final NumberFormatException ex){
				throw new TRException("Invalid data value in \""+itemx+"\"!");
			}
			
			if (data == 0) { //If :0, then :-10
				data = -10;
			}
			
			if (t[0].matches("\\d+")){//ID
				try {
					id = Integer.parseInt(t[0]);
				} catch (final NumberFormatException ex){
					throw new TRException("Invalid entry: \"" + itemx + "\"!");
				}
				
				tci.add(TRItem.parseItem(id, data, message));
				return tci;
			} else {//GROUP / NAME
				List<TRItem> items = groups.get(t[0]);
				
				if (items != null){
					for (final TRItem it : items){
						final TRItem it2 = (TRItem) it.clone();
						it2.msg = message;
						tci.add(it2);
					}
					if (addedGroups != null) addedGroups.add(t[0]);
					return tci; //All :*
				}
				
				items = processItemName(t[0], data, message);
				if (items != null) return items;
				
				throw new TRException("\""+t[0]+"\" is not a valid modgroup, permissiongroup, EE or IC2 itemname!");
			}
		}
		
		//############################## ALL ITEMS ###########################
		else if(itemx.equals("*")) {
			throw new TRException("Using * to define all items is no longer supported. Please give the permission tekkitrestrict.[feature].blockall instead.");
		}
		
		//############################## SINGLE ID ###########################
		else {
			int id = 0;
			try {
				id = Integer.parseInt(itemx);
			} catch (NumberFormatException ex){
				List<TRItem> items = groups.get(itemx);
				if (items != null){
					for (final TRItem it : items){
						final TRItem it2 = (TRItem) it.clone();
						it2.msg = message;
						tci.add(it2);
					}
					if (addedGroups != null) addedGroups.add(itemx);
					return tci; //All :*
				}
				
				items = processItemName(itemx, -1, message);
				if (items != null) return items;
				
				throw new TRException("\""+itemx+"\" is not a valid ID, modgroup, permissiongroup, EE itemname or IC2 itemname!");
			}

			tci.add(TRItem.parseItem(id, -1, message));
			return tci;
		}
	}
	
}
