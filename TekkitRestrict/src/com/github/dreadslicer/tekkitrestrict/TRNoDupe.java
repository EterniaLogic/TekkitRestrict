package com.github.dreadslicer.tekkitrestrict;

import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import nl.taico.tekkitrestict.objects.TREnums.DupeType;

import com.github.dreadslicer.tekkitrestrict.TRConfigCache.Dupes;

public class TRNoDupe {
	public static ConcurrentHashMap<String, Integer> cmdRM = new ConcurrentHashMap<String, Integer>();
	public static ConcurrentHashMap<String, Integer> cmdBag = new ConcurrentHashMap<String, Integer>();
	public static ConcurrentHashMap<String, Integer> cmdTP = new ConcurrentHashMap<String, Integer>();
	public static ConcurrentHashMap<String, Integer> cmdTransmution = new ConcurrentHashMap<String, Integer>();
	public static ConcurrentHashMap<String, Integer> cmdTC = new ConcurrentHashMap<String, Integer>();
	public static ConcurrentHashMap<String, Integer> cmdTCG = new ConcurrentHashMap<String, Integer>();
	public static ConcurrentHashMap<String, Integer> cmdPedestal = new ConcurrentHashMap<String, Integer>();
	
	public static void handleDupe(Player player, DupeType type, int id, int data){
		String message = "";
		if (type == DupeType.alcBag){
			if (Dupes.alcBags.useCommand){
				Integer cur = cmdBag.get(player.getName());
				if (cur == null) cur = 0;
				cur++;
				
				if (cur >= Dupes.alcBags.triggerAfter){
					try {
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), convert(Dupes.alcBags.command, "AlcBag", player, id, data));
					} catch (Exception ex) {}
					cmdBag.remove(player.getName());
				} else {
					cmdBag.put(player.getName(), cur);
				}
			}
			message = convert(Dupes.broadcastFormat, "Alchemy Bag + BHB / Void Ring", player, id, data);
			
			if (Dupes.alcBags.broadcast) Bukkit.broadcast("[TRDupe] " + message, "tekkitrestrict.notify.dupe");
			if (Dupes.alcBags.kick) Util.kick(player, "[TRDupe] " + message);
		}
		else if (type == DupeType.rmFurnace){
			if (Dupes.rmFurnaces.useCommand){
				Integer cur = cmdRM.get(player.getName());
				if (cur == null) cur = 0;
				cur++;
				
				if (cur >= Dupes.rmFurnaces.triggerAfter){
					try {
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), convert(Dupes.rmFurnaces.command, "RMFurnace", player, id, data));
					} catch (Exception ex) {}
					cmdRM.remove(player.getName());
				} else {
					cmdRM.put(player.getName(), cur);
				}
			}
			message = convert(Dupes.broadcastFormat, "Red Matter Furnace", player, id, data);
			
			if (Dupes.rmFurnaces.broadcast) Bukkit.broadcast("[TRDupe] " + message, "tekkitrestrict.notify.dupe");
			if (Dupes.rmFurnaces.kick) Util.kick(player, "[TRDupe] " + message);
		}
		else if (type == DupeType.tankCart){
			if (Dupes.tankcarts.useCommand){
				Integer cur = cmdTC.get(player.getName());
				if (cur == null) cur = 0;
				cur++;
				
				if (cur >= Dupes.tankcarts.triggerAfter){
					try {
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), convert(Dupes.tankcarts.command, "TankCart", player, id, data));
					} catch (Exception ex) {}
					cmdTC.remove(player.getName());
				} else {
					cmdTC.put(player.getName(), cur);
				}
			}
			message = convert(Dupes.broadcastFormat, "Tank Cart", player, id, data);
			
			if (Dupes.tankcarts.broadcast) Bukkit.broadcast("[TRDupe] " + message, "tekkitrestrict.notify.dupe");
			if (Dupes.tankcarts.kick) Util.kick(player, "[TRDupe] " + message);
		}
		else if (type == DupeType.tankCartGlitch){
			if (Dupes.tankcartGlitchs.useCommand){
				Integer cur = cmdTCG.get(player.getName());
				if (cur == null) cur = 0;
				cur++;
				
				if (cur >= Dupes.tankcartGlitchs.triggerAfter){
					try {
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), convert(Dupes.tankcartGlitchs.command, "TankCartGlitch", player, id, data));
					} catch (Exception ex) {}
					cmdTCG.remove(player.getName());
				} else {
					cmdTCG.put(player.getName(), cur);
				}
			}
			message = convert(Dupes.broadcastFormat, "Tank Cart", player, id, data);
			
			if (Dupes.tankcartGlitchs.broadcast) Bukkit.broadcast("[TRDupe] " + message, "tekkitrestrict.notify.dupe");
			if (Dupes.tankcartGlitchs.kick) Util.kick(player, "[TRDupe] " + message);
		}
		else if (type == DupeType.transmution){
			if (Dupes.transmutes.useCommand){
				Integer cur = cmdTransmution.get(player.getName());
				if (cur == null) cur = 0;
				cur++;
				
				if (cur >= Dupes.transmutes.triggerAfter){
					try {
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), convert(Dupes.transmutes.command, "Transmute", player, id, data));
					} catch (Exception ex) {}
					cmdTransmution.remove(player.getName());
				} else {
					cmdTransmution.put(player.getName(), cur);
				}
			}
			message = convert(Dupes.broadcastFormat, "Transmution Table(t)", player, id, data);
			
			if (Dupes.transmutes.broadcast) Bukkit.broadcast("[TRDupe] " + message, "tekkitrestrict.notify.dupe");
			if (Dupes.transmutes.kick) Util.kick(player, "[TRDupe] " + message);
		}
		else if (type == DupeType.pedestal){
			if (Dupes.pedestals.useCommand){
				Integer cur = cmdPedestal.get(player.getName());
				if (cur == null) cur = 0;
				cur++;
				
				if (cur >= Dupes.pedestals.triggerAfter){
					try {
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), convert(Dupes.pedestals.command, "Pedestal", player, id, data));
					} catch (Exception ex) {}
					cmdPedestal.remove(player.getName());
				} else {
					cmdPedestal.put(player.getName(), cur);
				}
			}
			message = convert(Dupes.broadcastFormat, "DM Pedestal + Harvest Godess Band", player, id, data);
			
			if (Dupes.pedestals.broadcast) Bukkit.broadcast("[TRDupe] " + message, "tekkitrestrict.notify.dupe");
			if (Dupes.pedestals.kick) Util.kick(player, "[TRDupe] " + message);
		}
		else if (type == DupeType.teleport){
			if (Dupes.teleports.useCommand){
				Integer cur = cmdTP.get(player.getName());
				if (cur == null) cur = 0;
				cur++;
				
				if (cur >= Dupes.teleports.triggerAfter){
					try {
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), convert(Dupes.teleports.command, "Teleport", player));
					} catch (Exception ex) {}
					cmdTP.remove(player.getName());
				} else {
					cmdTP.put(player.getName(), cur);
				}
			}
			message = convert(Dupes.broadcastFormat, "Teleportation", player);
			
			if (Dupes.teleports.broadcast) Bukkit.broadcast("[TRDupe] " + message, "tekkitrestrict.notify.dupe");
			if (Dupes.teleports.kick) Util.kick(player, "[TRDupe] " + message);
		}
		
		Log.Dupe(message);
	}
	
	private static String convert(String str, String type, Player player, int id, int data){
		str = Log.replaceColors(str);
		str = str.replace("{PLAYER}", player.getName());
		str = str.replace("{TYPE}", type);
		if (id == 0){
			str = str.replace("{ID}","");
			str = str.replace("{DATA}", "");
			str = str.replace("{ITEM}", "");
		} else {
			str = str.replace("{ID}", ""+id);
			str = str.replace("{DATA}", ""+data);
			str = str.replace("{ITEM}", ""+id+":"+data);
		}
		str = str.replace("  ", " ");
		return str;
	}
	
	private static String convert(String str, String type, Player player){
		str = Log.replaceColors(str);
		str = str.replace("{PLAYER}", player.getName());
		str = str.replace("{TYPE}", type);
		str = str.replace("{ID}","");
		str = str.replace("{DATA}", "");
		str = str.replace("{ITEM}", "");
		str = str.replace("  ", " ");
		return str;
	}
	
	public static void playerLogout(Player player){
		String n = player.getName();
		cmdRM.remove(n);
		cmdBag.remove(n);
		cmdPedestal.remove(n);
		cmdTC.remove(n);
		cmdTCG.remove(n);
		cmdTP.remove(n);
		cmdTransmution.remove(n);
	}
}
