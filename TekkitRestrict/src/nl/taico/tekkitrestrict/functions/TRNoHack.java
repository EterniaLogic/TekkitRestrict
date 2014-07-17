package nl.taico.tekkitrestrict.functions;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.eclipse.jdt.annotation.NonNull;

import nl.taico.tekkitrestrict.Log;
import nl.taico.tekkitrestrict.Util;
import nl.taico.tekkitrestrict.tekkitrestrict;
import nl.taico.tekkitrestrict.Log.Warning;
import nl.taico.tekkitrestrict.TRConfigCache.Hacks;
import nl.taico.tekkitrestrict.listeners.NoHackFly;
import nl.taico.tekkitrestrict.listeners.NoHackForcefield;
import nl.taico.tekkitrestrict.listeners.NoHackSpeed;
import nl.taico.tekkitrestrict.objects.TREnums.HackType;

public class TRNoHack {
	//public static int hacks = 0;
	public static HashMap<String, Integer> cmdFly = new HashMap<String, Integer>();
	public static HashMap<String, Integer> cmdForcefield = new HashMap<String, Integer>();
	public static HashMap<String, Integer> cmdSpeed = new HashMap<String, Integer>();
	
	public static void handleHackAsync(final Player player, final HackType type){
		Bukkit.getScheduler().scheduleSyncDelayedTask(tekkitrestrict.instance, new Runnable(){
			public void run(){
				TRNoHack.handleHack(player, type);
			}
		});
	}

	//sync
	public static void handleHack(@NonNull Player player, HackType type) {
		String message = "";
		
		if (type == HackType.fly){
			if (Hacks.fly.useCommand){
				Integer cur = cmdFly.get(player.getName());
				if (cur == null) cur = 0;
				cur++;
				
				if (cur >= Hacks.fly.triggerAfter){
					try {
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), Hacks.fly.command.replace("{PLAYER}", player.getName()).replace("{TYPE}", "fly"));
					} catch (Exception ex) {
						Warning.config("The command set for Anti-Hacks.Fly returned an error!", false);
					}
					cmdFly.remove(player.getName());
				} else {
					cmdFly.put(player.getName(), cur);
				}
			}
			
			message = convert(Hacks.broadcastFormat, "Fly", player);
			if (Hacks.fly.kick) Util.kick(player, "[TRHack] Kicked for Fly-hacking!");
			if (Hacks.fly.broadcast) Util.broadcastNoConsole("[TRHack] " + message, "tekkitrestrict.notify.hack");
		} else if (type == HackType.forcefield){
			if (Hacks.forcefield.useCommand){
				Integer cur = cmdForcefield.get(player.getName());
				if (cur == null) cur = 0;
				cur++;
				
				if (cur >= Hacks.forcefield.triggerAfter){
					try {
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), Hacks.forcefield.command.replace("{PLAYER}", player.getName()).replace("{TYPE}", "forcefield"));
					} catch (Exception ex) {
						Warning.config("The command set for Anti-Hacks.Forcefield returned an error!", false);
					}
					cmdForcefield.remove(player.getName());
				} else {
					cmdForcefield.put(player.getName(), cur);
				}
			}
			
			message = convert(Hacks.broadcastFormat, "Forcefield", player);
			if (Hacks.forcefield.kick) Util.kick(player, "[TRHack] Kicked for Forcefield-hacking!");
			if (Hacks.forcefield.broadcast) Util.broadcastNoConsole("[TRHack] " + message, "tekkitrestrict.notify.hack");
		} else if (type == HackType.speed){
			if (Hacks.speed.useCommand){
				Integer cur = cmdSpeed.get(player.getName());
				if (cur == null) cur = 0;
				cur++;
				
				if (cur >= Hacks.speed.triggerAfter){
					try {
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), Hacks.speed.command.replace("{PLAYER}", player.getName()).replace("{TYPE}", "movespeed"));
					} catch (Exception ex) {
						Warning.config("The command set for Anti-Hacks.MoveSpeed returned an error!", false);
					}
					cmdSpeed.remove(player.getName());
				} else {
					cmdSpeed.put(player.getName(), cur);
				}
			}
			
			message = convert(Hacks.broadcastFormat, "Speed", player);
			if (Hacks.speed.kick) Util.kick(player, "[TRHack] Kicked for speed-hacking!");
			if (Hacks.speed.broadcast) Util.broadcastNoConsole("[TRHack] " + message, "tekkitrestrict.notify.hack");
		}
		
		Log.Hack(message);
	}
	
	@NonNull private static String convert(@NonNull String str, @NonNull String type, @NonNull Player player){
		return Log.replaceColors(str)
				.replace("{PLAYER}", player.getName())
				.replace("{TYPE}", type)
				.replace("{ID}","")
				.replace("{DATA}", "")
				.replace("{ITEM}", "")
				.replace("  ", " ");
	}

	public static void clearMaps() {
		NoHackSpeed.clearMaps();
		NoHackFly.clearMaps();
		NoHackForcefield.clearMaps();
	}

	//sync
	public static void playerLogout(@NonNull Player player) {
		// clears ALL lists for said player
		String n = player.getName();
		NoHackSpeed.playerLogout(n);
		NoHackFly.playerLogout(n);
		NoHackForcefield.playerLogout(n);
		cmdFly.remove(n);
		cmdForcefield.remove(n);
		cmdSpeed.remove(n);
		//TRLimitFlyThread.setGrounded(player);
	}
}
