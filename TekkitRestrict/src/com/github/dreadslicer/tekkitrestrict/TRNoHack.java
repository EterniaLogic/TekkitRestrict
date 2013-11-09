package com.github.dreadslicer.tekkitrestrict;

import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import nl.taico.tekkitrestrict.listeners.NoHackFly;
import nl.taico.tekkitrestrict.listeners.NoHackForcefield;
import nl.taico.tekkitrestrict.listeners.NoHackSpeed;
import nl.taico.tekkitrestrict.objects.TREnums.HackType;

import com.github.dreadslicer.tekkitrestrict.TRConfigCache.Hacks;

public class TRNoHack {
	//public static int hacks = 0;
	public static ConcurrentHashMap<String, Integer> cmdFly = new ConcurrentHashMap<String, Integer>();
	public static ConcurrentHashMap<String, Integer> cmdForcefield = new ConcurrentHashMap<String, Integer>();
	public static ConcurrentHashMap<String, Integer> cmdSpeed = new ConcurrentHashMap<String, Integer>();
	public static void handleHack(Player player, HackType type) {
		//int x = player.getLocation().getBlockX();
		//int y = player.getLocation().getBlockY();
		//int z = player.getLocation().getBlockZ();
		//Entity veh = player.getVehicle();
		//List<Entity> nent = player.getNearbyEntities(16, 16, 16);
		//int npl = 0, nmob = 0;
		//for (Entity gx : nent) {
		//	if (gx instanceof EntityPlayer) {
		//		npl++;
		//	} else {
		//		nmob++;
		//	}
		//}
		//Vector velo = player.getVelocity();
		//DecimalFormat myFormatter = new DecimalFormat("#.##");
		//String additional = "Loc: [" + player.getWorld().getName() + "," + x
		//		+ "," + y + "," + z + "] " + "Velo: ["
		//		+ myFormatter.format(velo.getX()) + " m/s,"
		//		+ myFormatter.format(velo.getY()) + " m/s,"
		//		+ myFormatter.format(velo.getZ()) + " m/s]  "
		//		+"Vehicle: ["
		//		+(veh != null ? veh.getClass().getName() : "none") + "] "
		//		+"Entity#: [player: " + npl + ", mob: " + nmob + "]";
		String message = "";
		
		if (type == HackType.fly){
			if (Hacks.flys.useCommand){
				Integer cur = cmdFly.get(player.getName());
				if (cur == null) cur = 0;
				cur++;
				
				if (cur >= Hacks.flys.triggerAfter){
					try {
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), Hacks.flys.command.replace("{PLAYER}", player.getName()).replace("{TYPE}", "fly"));
					} catch (Exception ex) {}
					cmdFly.remove(player.getName());
				} else {
					cmdFly.put(player.getName(), cur);
				}
			}
			
			message = convert(Hacks.broadcastFormat, "Fly", player);
			if (Hacks.flys.kick) Util.kick(player, "[TRHack] Kicked for Fly-hacking!");
			if (Hacks.flys.broadcast) Bukkit.broadcast("[TRHack] " + message, "tekkitrestrict.notify.hack");
		} else if (type == HackType.forcefield){
			if (Hacks.forcefields.useCommand){
				Integer cur = cmdForcefield.get(player.getName());
				if (cur == null) cur = 0;
				cur++;
				
				if (cur >= Hacks.forcefields.triggerAfter){
					try {
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), Hacks.forcefields.command.replace("{PLAYER}", player.getName()).replace("{TYPE}", "forcefield"));
					} catch (Exception ex) {}
					cmdForcefield.remove(player.getName());
				} else {
					cmdForcefield.put(player.getName(), cur);
				}
			}
			
			message = convert(Hacks.broadcastFormat, "Forcefield", player);
			if (Hacks.forcefields.kick) Util.kick(player, "[TRHack] Kicked for Forcefield-hacking!");
			if (Hacks.forcefields.broadcast) Bukkit.broadcast("[TRHack] " + message, "tekkitrestrict.notify.hack");
		} else if (type == HackType.speed){
			if (Hacks.speeds.useCommand){
				Integer cur = cmdSpeed.get(player.getName());
				if (cur == null) cur = 0;
				cur++;
				
				if (cur >= Hacks.speeds.triggerAfter){
					try {
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), Hacks.speeds.command.replace("{PLAYER}", player.getName()).replace("{TYPE}", "movespeed"));
					} catch (Exception ex) {}
					cmdSpeed.remove(player.getName());
				} else {
					cmdSpeed.put(player.getName(), cur);
				}
			}
			
			message = convert(Hacks.broadcastFormat, "Speed", player);
			if (Hacks.speeds.kick) Util.kick(player, "[TRHack] Kicked for speed-hacking!");
			if (Hacks.speeds.broadcast) Bukkit.broadcast("[TRHack] " + message, "tekkitrestrict.notify.hack");
		}
		
		Log.Hack(message);
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

	/** Teleport the player to the highest block at his position. Will not teleport players above their current position. */
	public static void groundPlayer(Player player) {
		Block highest = player.getWorld().getHighestBlockAt(player.getLocation());
		int yblock = highest.getLocation().getBlockY();
		int yplayer = player.getLocation().getBlockY();
		if (yplayer < yblock) player.teleport(highest.getLocation());
	}

	public static void clearMaps() {
		NoHackSpeed.clearMaps();
		NoHackFly.clearMaps();
		NoHackForcefield.clearMaps();
	}

	public static void playerLogout(Player player) {
		// clears ALL lists for said player
		NoHackSpeed.playerLogout(player.getName());
		NoHackFly.playerLogout(player.getName());
		NoHackForcefield.playerLogout(player.getName());
		cmdFly.remove(player.getName());
		cmdForcefield.remove(player.getName());
		cmdSpeed.remove(player.getName());
		//TRLimitFlyThread.setGrounded(player);
	}
}
