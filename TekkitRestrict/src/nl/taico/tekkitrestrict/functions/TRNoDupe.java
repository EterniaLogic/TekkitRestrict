package nl.taico.tekkitrestrict.functions;

import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.eclipse.jdt.annotation.NonNull;

import nl.taico.tekkitrestrict.Log;
import nl.taico.tekkitrestrict.Util;
import nl.taico.tekkitrestrict.Log.Warning;
import nl.taico.tekkitrestrict.TRConfigCache.Dupes;
import nl.taico.tekkitrestrict.objects.TREnums.DupeType;

public class TRNoDupe {
	public static ConcurrentHashMap<String, Integer> cmdRM = new ConcurrentHashMap<String, Integer>();
	public static ConcurrentHashMap<String, Integer> cmdBag = new ConcurrentHashMap<String, Integer>();
	public static ConcurrentHashMap<String, Integer> cmdTP = new ConcurrentHashMap<String, Integer>();
	public static ConcurrentHashMap<String, Integer> cmdTransmution = new ConcurrentHashMap<String, Integer>();
	public static ConcurrentHashMap<String, Integer> cmdTC = new ConcurrentHashMap<String, Integer>();
	public static ConcurrentHashMap<String, Integer> cmdTCG = new ConcurrentHashMap<String, Integer>();
	public static ConcurrentHashMap<String, Integer> cmdPedestal = new ConcurrentHashMap<String, Integer>();
	public static ConcurrentHashMap<String, Integer> cmdDiskdrive = new ConcurrentHashMap<String, Integer>();;
	
	public static void handleDupe(@NonNull Player player, DupeType type, int id, int data){
		String message = "";
		if (type == DupeType.alcBag){
			if (Dupes.alcBag.useCommand){
				Integer cur = cmdBag.get(player.getName());
				if (cur == null) cur = 0;
				cur++;
				
				if (cur >= Dupes.alcBag.triggerAfter){
					try {
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), convert(Dupes.alcBag.command, "AlcBag", player, id, data));
					} catch (Exception ex) {
						Warning.config("The command set for Anti-Dupes.AlchemyBagDupe returned an error!", false);
					}
					cmdBag.remove(player.getName());
				} else {
					cmdBag.put(player.getName(), cur);
				}
			}
			message = convert(Dupes.broadcastFormat, "Alchemy Bag + BHB / Void Ring", player, id, data);
			
			if (Dupes.alcBag.broadcast) Util.broadcastNoConsole("[TRDupe] " + message, "tekkitrestrict.notify.dupe");
			if (Dupes.alcBag.kick) Util.kick(player, "[TRDupe] " + message);
		}
		else if (type == DupeType.rmFurnace){
			if (Dupes.rmFurnace.useCommand){
				Integer cur = cmdRM.get(player.getName());
				if (cur == null) cur = 0;
				cur++;
				
				if (cur >= Dupes.rmFurnace.triggerAfter){
					try {
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), convert(Dupes.rmFurnace.command, "RMFurnace", player, id, data));
					} catch (Exception ex) {
						Warning.config("The command set for Anti-Dupes.RMFurnaceDupe returned an error!", false);
					}
					cmdRM.remove(player.getName());
				} else {
					cmdRM.put(player.getName(), cur);
				}
			}
			message = convert(Dupes.broadcastFormat, "Red Matter Furnace", player, id, data);
			
			if (Dupes.rmFurnace.broadcast) Util.broadcastNoConsole("[TRDupe] " + message, "tekkitrestrict.notify.dupe");
			if (Dupes.rmFurnace.kick) Util.kick(player, "[TRDupe] " + message);
		}
		else if (type == DupeType.tankCart){
			if (Dupes.tankcart.useCommand){
				Integer cur = cmdTC.get(player.getName());
				if (cur == null) cur = 0;
				cur++;
				
				if (cur >= Dupes.tankcart.triggerAfter){
					try {
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), convert(Dupes.tankcart.command, "TankCart", player, id, data));
					} catch (Exception ex) {
						Warning.config("The command set for Anti-Dupes.TankCartDupe returned an error!", false);
					}
					cmdTC.remove(player.getName());
				} else {
					cmdTC.put(player.getName(), cur);
				}
			}
			message = convert(Dupes.broadcastFormat, "Tank Cart", player, id, data);
			
			if (Dupes.tankcart.broadcast) Util.broadcastNoConsole("[TRDupe] " + message, "tekkitrestrict.notify.dupe");
			if (Dupes.tankcart.kick) Util.kick(player, "[TRDupe] " + message);
		}
		else if (type == DupeType.tankCartGlitch){
			if (Dupes.tankcartGlitch.useCommand){
				Integer cur = cmdTCG.get(player.getName());
				if (cur == null) cur = 0;
				cur++;
				
				if (cur >= Dupes.tankcartGlitch.triggerAfter){
					try {
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), convert(Dupes.tankcartGlitch.command, "TankCartGlitch", player, id, data));
					} catch (Exception ex) {
						Warning.config("The command set for Anti-Dupes.TankCartGlitch returned an error!", false);
					}
					cmdTCG.remove(player.getName());
				} else {
					cmdTCG.put(player.getName(), cur);
				}
			}
			message = convert(Dupes.broadcastFormat, "Tank Cart", player, id, data);
			
			if (Dupes.tankcartGlitch.broadcast) Util.broadcastNoConsole("[TRDupe] " + message, "tekkitrestrict.notify.dupe");
			if (Dupes.tankcartGlitch.kick) Util.kick(player, "[TRDupe] " + message);
		}
		else if (type == DupeType.transmution){
			if (Dupes.transmute.useCommand){
				Integer cur = cmdTransmution.get(player.getName());
				if (cur == null) cur = 0;
				cur++;
				
				if (cur >= Dupes.transmute.triggerAfter){
					try {
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), convert(Dupes.transmute.command, "Transmute", player, id, data));
					} catch (Exception ex) {
						Warning.config("The command set for Anti-Dupes.TransmuteDupe returned an error!", false);
					}
					cmdTransmution.remove(player.getName());
				} else {
					cmdTransmution.put(player.getName(), cur);
				}
			}
			message = convert(Dupes.broadcastFormat, "Transmution Table(t)", player, id, data);
			
			if (Dupes.transmute.broadcast) Util.broadcastNoConsole("[TRDupe] " + message, "tekkitrestrict.notify.dupe");
			if (Dupes.transmute.kick) Util.kick(player, "[TRDupe] " + message);
		}
		else if (type == DupeType.pedestal){
			if (Dupes.pedestal.useCommand){
				Integer cur = cmdPedestal.get(player.getName());
				if (cur == null) cur = 0;
				cur++;
				
				if (cur >= Dupes.pedestal.triggerAfter){
					try {
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), convert(Dupes.pedestal.command, "Pedestal", player, id, data));
					} catch (Exception ex) {
						Warning.config("The command set for Anti-Dupes.PedestalEmcGen returned an error!", false);
					}
					cmdPedestal.remove(player.getName());
				} else {
					cmdPedestal.put(player.getName(), cur);
				}
			}
			message = convert(Dupes.broadcastFormat, "DM Pedestal + Harvest Godess Band", player, id, data);
			
			if (Dupes.pedestal.broadcast) Util.broadcastNoConsole("[TRDupe] " + message, "tekkitrestrict.notify.dupe");
			if (Dupes.pedestal.kick) Util.kick(player, "[TRDupe] " + message);
		} else if (type == DupeType.diskdrive){
			if (Dupes.diskdrive.useCommand){
				Integer cur = cmdDiskdrive.get(player.getName());
				if (cur == null) cur = 0;
				cur++;
				
				if (cur >= Dupes.diskdrive.triggerAfter){
					try {
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), convert(Dupes.diskdrive.command, "Disk Drive", player, id, data));
					} catch (Exception ex) {
						Warning.config("The command set for Anti-Dupes.DiskDrive returned an error!", false);
					}
					cmdDiskdrive.remove(player.getName());
				} else {
					cmdDiskdrive.put(player.getName(), cur);
				}
			}
			message = convert(Dupes.broadcastFormat, "Disk Drive", player, id, data);
			
			if (Dupes.diskdrive.broadcast) Util.broadcastNoConsole("[TRDupe] " + message, "tekkitrestrict.notify.dupe");
			if (Dupes.diskdrive.kick) Util.kick(player, "[TRDupe] " + message);
		}
		
		Log.Dupe(message);
	}
	
	@NonNull private static String convert(@NonNull String str, @NonNull String type, @NonNull Player player, int id, int data){
		str = Log.replaceColors(str)
				.replaceAll("(?i)\\{PLAYER\\}", Matcher.quoteReplacement(player.getName()))
				.replaceAll("(?i)\\{TYPE\\}", Matcher.quoteReplacement(type));
		if (id == 0){
			str = str.replaceAll("(?i)\\{ID\\}","");
			str = str.replaceAll("(?i)\\{DATA\\}", "");
			str = str.replaceAll("(?i)\\{ITEM\\}", "");
		} else {
			str = str.replaceAll("(?i)\\{ID\\}", ""+id);
			str = str.replaceAll("(?i)\\{DATA\\}", ""+data);
			str = str.replaceAll("(?i)\\{ITEM\\}", ""+id+":"+data);
		}
		str = str.replace("  ", " ");
		return str;
	}
	
	public static void playerLogout(@NonNull Player player){
		
		String n = player.getName();
		cmdRM.remove(n);
		cmdBag.remove(n);
		cmdPedestal.remove(n);
		cmdTC.remove(n);
		cmdTCG.remove(n);
		cmdTP.remove(n);
		cmdTransmution.remove(n);
		cmdDiskdrive.remove(n);
	}
}
