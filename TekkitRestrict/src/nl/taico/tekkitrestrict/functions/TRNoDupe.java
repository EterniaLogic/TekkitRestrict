package nl.taico.tekkitrestrict.functions;

import java.util.HashMap;

import nl.taico.taeirlib.TStrings;
import nl.taico.tekkitrestrict.Log;
import nl.taico.tekkitrestrict.Log.Warning;
import nl.taico.tekkitrestrict.TRConfigCache.Dupes;
import nl.taico.tekkitrestrict.Util;
import nl.taico.tekkitrestrict.objects.TREnums.DupeType;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TRNoDupe {
	public static HashMap<String, Integer> cmdRM = new HashMap<String, Integer>();
	public static HashMap<String, Integer> cmdBag = new HashMap<String, Integer>();
	public static HashMap<String, Integer> cmdTP = new HashMap<String, Integer>();
	public static HashMap<String, Integer> cmdTransmution = new HashMap<String, Integer>();
	public static HashMap<String, Integer> cmdTC = new HashMap<String, Integer>();
	public static HashMap<String, Integer> cmdTCG = new HashMap<String, Integer>();
	public static HashMap<String, Integer> cmdPedestal = new HashMap<String, Integer>();
	public static HashMap<String, Integer> cmdDiskdrive = new HashMap<String, Integer>();;

	private static String convert(String str, String type, Player player, int id, int data){
		if (str == null) return "null";
		str = TStrings.replace(TStrings.convertColors(str),
				"{PLAYER}", player == null ? "null" : player.getName(),
						"{TYPE}", 	type == null ? "null" : type);

		if (id == 0){
			return TStrings.replace(str,
					"{ID}", 	"",
					"{DATA}", 	"",
					"{ITEM}", 	"",
					"  ", 		" ");
		} else {
			return TStrings.replace(str,
					"{ID}", 	""+id,
					"{DATA}", 	""+data,
					"{ITEM}", 	""+id+":"+data,
					"  ", 		" ");
		}
	}

	public static void handleDupe(Player player, DupeType type, int id, int data){
		if (player == null) return;

		String message = "";
		switch (type) {
		case alcBag:
			if (Dupes.alcBag.useCommand){
				Integer cur = cmdBag.get(player.getName());
				if (cur == null) cur = 0;
				cur++;

				if (cur >= Dupes.alcBag.triggerAfter){
					try {
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), convert(Dupes.alcBag.command, "AlcBag", player, id, data));
					} catch (Exception ex) {
						Warning.config("The command set for Anti-Dupes. AlchemyBagDupe returned an error!", false);
					}
					cmdBag.remove(player.getName());
				} else {
					cmdBag.put(player.getName(), cur);
				}
			}
			message = convert(Dupes.broadcastFormat, "Alchemy Bag + BHB / Void Ring", player, id, data);
			if (Dupes.alcBag.broadcast) Util.broadcastNoConsole("[TRDupe] " + message, "tekkitrestrict.notify.dupe");
			if (Dupes.alcBag.kick) Util.kick(player, "[TRDupe] " + message);
			break;
		case rmFurnace:
			if (Dupes.rmFurnace.useCommand){
				Integer cur = cmdRM.get(player.getName());
				if (cur == null) cur = 0;
				cur++;

				if (cur >= Dupes.rmFurnace.triggerAfter){
					try {
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), convert(Dupes.rmFurnace.command, "RMFurnace", player, id, data));
					} catch (Exception ex) {
						Warning.config("The command set for Anti-Dupes. RMFurnaceDupe returned an error!", false);
					}
					cmdRM.remove(player.getName());
				} else {
					cmdRM.put(player.getName(), cur);
				}
			}
			message = convert(Dupes.broadcastFormat, "Red Matter Furnace", player, id, data);
			if (Dupes.rmFurnace.broadcast) Util.broadcastNoConsole("[TRDupe] " + message, "tekkitrestrict.notify.dupe");
			if (Dupes.rmFurnace.kick) Util.kick(player, "[TRDupe] " + message);
			break;
		case tankCart:
			if (Dupes.tankcart.useCommand){
				Integer cur = cmdTC.get(player.getName());
				if (cur == null) cur = 0;
				cur++;

				if (cur >= Dupes.tankcart.triggerAfter){
					try {
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), convert(Dupes.tankcart.command, "TankCart", player, id, data));
					} catch (Exception ex) {
						Warning.config("The command set for Anti-Dupes. TankCartDupe returned an error!", false);
					}
					cmdTC.remove(player.getName());
				} else {
					cmdTC.put(player.getName(), cur);
				}
			}
			message = convert(Dupes.broadcastFormat, "Tank Cart", player, id, data);
			if (Dupes.tankcart.broadcast) Util.broadcastNoConsole("[TRDupe] " + message, "tekkitrestrict.notify.dupe");
			if (Dupes.tankcart.kick) Util.kick(player, "[TRDupe] " + message);
			break;
		case tankCartGlitch:
			if (Dupes.tankcartGlitch.useCommand){
				Integer cur = cmdTCG.get(player.getName());
				if (cur == null) cur = 0;
				cur++;

				if (cur >= Dupes.tankcartGlitch.triggerAfter){
					try {
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), convert(Dupes.tankcartGlitch.command, "TankCartGlitch", player, id, data));
					} catch (Exception ex) {
						Warning.config("The command set for Anti-Dupes. TankCartGlitch returned an error!", false);
					}
					cmdTCG.remove(player.getName());
				} else {
					cmdTCG.put(player.getName(), cur);
				}
			}
			message = convert(Dupes.broadcastFormat, "Tank Cart", player, id, data);
			if (Dupes.tankcartGlitch.broadcast) Util.broadcastNoConsole("[TRDupe] " + message, "tekkitrestrict.notify.dupe");
			if (Dupes.tankcartGlitch.kick) 		Util.kick(player, "[TRDupe] " + message);
			break;
		case transmution:
			if (Dupes.transmute.useCommand){
				Integer cur = cmdTransmution.get(player.getName());
				if (cur == null) cur = 0;
				cur++;

				if (cur >= Dupes.transmute.triggerAfter){
					try {
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), convert(Dupes.transmute.command, "Transmute", player, id, data));
					} catch (Exception ex) {
						Warning.config("The command set for Anti-Dupes. TransmuteDupe returned an error!", false);
					}
					cmdTransmution.remove(player.getName());
				} else {
					cmdTransmution.put(player.getName(), cur);
				}
			}
			message = convert(Dupes.broadcastFormat, "Transmutation Table(t)", player, id, data);
			if (Dupes.transmute.broadcast) 	Util.broadcastNoConsole("[TRDupe] " + message, "tekkitrestrict.notify.dupe");
			if (Dupes.transmute.kick) 		Util.kick(player, "[TRDupe] " + message);
			break;
		case pedestal:
			if (Dupes.pedestal.useCommand){
				Integer cur = cmdPedestal.get(player.getName());
				if (cur == null) cur = 0;
				cur++;

				if (cur >= Dupes.pedestal.triggerAfter){
					try {
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), convert(Dupes.pedestal.command, "Pedestal", player, id, data));
					} catch (Exception ex) {
						Warning.config("The command set for Anti-Dupes. PedestalEmcGen returned an error!", false);
					}
					cmdPedestal.remove(player.getName());
				} else {
					cmdPedestal.put(player.getName(), cur);
				}
			}
			message = convert(Dupes.broadcastFormat, "DM Pedestal + Harvest Godess Band", player, id, data);
			if (Dupes.pedestal.broadcast) 	Util.broadcastNoConsole("[TRDupe] " + message, "tekkitrestrict.notify.dupe");
			if (Dupes.pedestal.kick) 		Util.kick(player, "[TRDupe] " + message);
			break;
		case diskdrive:
			if (Dupes.diskdrive.useCommand){
				Integer cur = cmdDiskdrive.get(player.getName());
				if (cur == null) cur = 0;
				cur++;

				if (cur >= Dupes.diskdrive.triggerAfter){
					try {
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), convert(Dupes.diskdrive.command, "Disk Drive", player, id, data));
					} catch (Exception ex) {
						Warning.config("The command set for Anti-Dupes. DiskDrive returned an error!", false);
					}
					cmdDiskdrive.remove(player.getName());
				} else {
					cmdDiskdrive.put(player.getName(), cur);
				}
			}
			message = convert(Dupes.broadcastFormat, "Disk Drive", player, id, data);
			if (Dupes.diskdrive.broadcast) 	Util.broadcastNoConsole("[TRDupe] " + message, "tekkitrestrict.notify.dupe");
			if (Dupes.diskdrive.kick) 		Util.kick(player, "[TRDupe] " + message);
			break;
		default:
			break;
		}

		Log.Dupe(message);
	}

	public static void playerLogout(String name){
		if (name == null) return;

		cmdRM.remove(name);
		cmdBag.remove(name);
		cmdPedestal.remove(name);
		cmdTC.remove(name);
		cmdTCG.remove(name);
		cmdTP.remove(name);
		cmdTransmution.remove(name);
		cmdDiskdrive.remove(name);
	}
}
