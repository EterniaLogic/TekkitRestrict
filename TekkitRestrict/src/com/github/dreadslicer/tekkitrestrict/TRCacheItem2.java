package com.github.dreadslicer.tekkitrestrict;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.bukkit.entity.Player;

import com.github.dreadslicer.tekkitrestrict.objects.TRData;
import com.github.dreadslicer.tekkitrestrict.objects.TREnums.ConfigFile;

public class TRCacheItem2 {
	public int id, data;
	public static TreeMap<Integer, TRData> configBansNoItem = new TreeMap<Integer, TRData>();
	public static ArrayList<String> configBanGroupsNoItem = new ArrayList<String>();
	public static TreeMap<Integer, TRData> configBansCreative = new TreeMap<Integer, TRData>();
	public static ArrayList<String> configBanGroupsCreative = new ArrayList<String>();
	
	public TRCacheItem2(int id, int data){
		this.id = id;
		this.data = data;
	}
	
	/** @return If the id:data combination is banned in the config. */
	public static boolean inNoitemConfigBans(int id, int data){
		TRData configData = configBansNoItem.get(id);//Less effective the more items are banned.
		return configData == null ? false : configData.contains(data);
	}
	
	/** @return If the id:data combination is banned in the config. */
	public static boolean inCreativeConfigBans(int id, int data){
		TRData configData = configBansCreative.get(id);//Less effective the more items are banned.
		return configData == null ? false : configData.contains(data);
	}
	
	/**
	 * @return <b>False</b> if id == 0 or player has bypass.<br>
	 * <b>True</b> if the item is banned in the config, if the player has the permission or if the player has the group permission.
	 */
	public static boolean isBanned(Player player, String type, int id, int data){
		if (id == 0) return false;
		if (player.hasPermission("tekkitrestrict.bypass."+type)) return false;
		
		if (type.equals("noitem")) if (inNoitemConfigBans(id, data)) return true;
		else if (type.equals("creative")) if (inCreativeConfigBans(id, data)) return true;
		
		if (player.hasPermission("tekkitrestrict."+type+"."+id) || player.hasPermission("tekkitrestrict."+type+"."+id+"."+data)) return true;
		
		String group = Util.inGroup(id);
		if (group == null) return false;
		if (type.equals("noitem")) if (configBanGroupsNoItem.contains(group)) return true;
		else if (type.equals("creative")) if (configBanGroupsCreative.contains(group)) return true;
		
		return player.hasPermission("tekkitrestrict."+type+"."+group);
	}
	
	public static void LoadNoItemConfig(){
		List<String> Bans = tekkitrestrict.config.getStringList(ConfigFile.DisableItems, "DisableItems");
		int i = 0;
		for (String current : Bans){
			ArrayList<TRCacheItem2> cacheItems = processSimpleString(current);
			if (cacheItems == null){
				configBanGroupsNoItem.add(current);
				continue;
			}
			
			for (TRCacheItem2 item : cacheItems){
				TRData old = configBansNoItem.get(item.id);
				if (old == null) old = new TRData(item.data);
				else old.add(item.data);
				configBansNoItem.put(item.id, old);
			}

			i++;
		}
		Log.Config.Loaded("Banned Items", i);
	}
	
	public static void LoadCreativeConfig(){
		List<String> Bans = tekkitrestrict.config.getStringList(ConfigFile.LimitedCreative, "LimitedCreative");
		int i = 0;
		for (String current : Bans){
			ArrayList<TRCacheItem2> cacheItems = processSimpleString(current);
			if (cacheItems == null){
				configBanGroupsCreative.add(current);
				continue;
			}
			
			for (TRCacheItem2 item : cacheItems){
				TRData old = configBansCreative.get(item.id);
				if (old == null) old = new TRData(item.data);
				else old.add(item.data);
				configBansCreative.put(item.id, old);
			}

			i++;
		}
		Log.Config.Loaded("Limited Creative", i);
	}
	
	/**
	 * For simple strings of [ids]:[data].<br>
	 * Example:<br>
	 * input = 1, 20-23, 40 : 1-3, 30<br>
	 * ids = 1, 20, 21, 22, 23, 40<br>
	 * data = 1, 2, 3, 30<br>
	 * 1:1, 1:2, 1:3, 1:30, 20:1, 20:2, etc.<br>
	 */
	public static ArrayList<TRCacheItem2> processSimpleString(String string){
		String input = string.replace(" ", "");
		//(12, 15-20):(10, 11, 20-30, 12-13)
		ArrayList<TRCacheItem2> tbr = new ArrayList<TRCacheItem2>();
		//12, 15-20:10,20-30 = 15:10, 15:20 - 15:30, 16:10, 16:20 - 16:30 etc.
		List<Integer> allData = new ArrayList<Integer>();
		List<Integer> allIds = new ArrayList<Integer>();
		
		String ids;
		String datas;
		if (input.contains(":")){
			String whole[] = input.split(":"); //"12,15-20";"10,20-30"
			ids = whole[0]; //"12,15-20"
			datas = whole[1]; //"10,20-30"
		} else {
			ids = input;
			datas = "*";
		}
		
		if (ids.contains(",")){
			String temp[] = ids.split(",");//"12";"15-20"
			for (String current : temp){
				if (current.contains("-")){
					String temp2[] = current.split("-");//"15";"20"
					if (!temp2[0].matches("\\d+") || !temp2[1].matches("\\d+")){
						//TODO error
						return null;
					} else {
						int begin = Integer.parseInt(temp2[0]);
						int end = Integer.parseInt(temp2[1]);
						for (int i = begin; i<=end; i++){
							allIds.add(i);
						}
					}
				} else {
					if (!current.matches("\\d+")){
						//TODO error
						return null;
					} else {
						allIds.add(Integer.parseInt(current));//12
					}
				}
			}
		} else if (ids.contains("-")){
			String temp[] = ids.split("-");
			if (!temp[0].matches("\\d+") || !temp[1].matches("\\d+")){
				//TODO error
				return null;
			} else {
				int begin = Integer.parseInt(temp[0]);
				int end = Integer.parseInt(temp[1]);
				for (int i = begin; i<=end; i++){
					allIds.add(i);
				}
			}
		} else {
			if (!ids.matches("\\d+")){
				//TODO error
				return null;
			} else {
				allIds.add(Integer.parseInt(ids));
			}
		}
		
		if (datas.contains(",")){
			String temp[] = datas.split(",");//"10";"20-30"
			for (String current : temp){
				if (current.contains("-")){
					String temp2[] = current.split("-");//"20";"30"
					if (!temp2[0].matches("\\d+") || !temp2[1].matches("\\d+")){
						//TODO error
						return null;
					} else {
						int begin = Integer.parseInt(temp2[0]);
						int end = Integer.parseInt(temp2[1]);
						for (int i = begin; i<=end; i++){
							allData.add(i);//20-30
						}
					}
				} else {
					if (!current.matches("\\d+")){
						//TODO error
						return null;
					} else {
						allData.add(Integer.parseInt(current));//10
					}
				}
			}
		} else if (datas.contains("-")){
			String temp[] = datas.split("-");
			if (!temp[0].matches("\\d+") || !temp[1].matches("\\d+")){
				//TODO error
				return null;
			} else {
				int begin = Integer.parseInt(temp[0]);
				int end = Integer.parseInt(temp[1]);
				for (int i = begin; i<=end; i++){
					allData.add(i);
				}
			}
		} else if (datas.equals("*")) {
			allData.add(-1);
		} else {
			if (!datas.matches("\\d+")){
				//TODO error
				return null;
			} else {
				allData.add(Integer.parseInt(datas));
			}
		}
		
		//allIntData = 10;20;21;22...29;30
		//allIntIds = 12;15;16...19;20
		
		for (int id : allIds){
			for (int data : allData){
				tbr.add(new TRCacheItem2(id, data));
			}
		}
		
		return tbr;
	}
}
