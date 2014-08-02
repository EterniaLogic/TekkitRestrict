package nl.taico.tekkitrestrict.functions;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import nl.taico.taeirlib.TStrings;
import nl.taico.tekkitrestrict.Log;
import nl.taico.tekkitrestrict.Util;
import nl.taico.tekkitrestrict.TekkitRestrict;
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
		Bukkit.getScheduler().scheduleSyncDelayedTask(TekkitRestrict.instance, new Runnable(){
			public void run(){
				TRNoHack.handleHack(player, type);
			}
		});
	}

	//sync
	public static void handleHack(Player player, HackType type) {
		if (player == null) return;
		
		String message = "";
		
		switch (type) {
			case fly:
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
				if (Hacks.fly.kick) 		Util.kick(player, "[TRHack] Kicked for Fly-hacking!");
				if (Hacks.fly.broadcast) 	Util.broadcastNoConsole("[TRHack] " + message, "tekkitrestrict.notify.hack");
				break;
			case forcefield:
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
				if (Hacks.forcefield.kick) 		Util.kick(player, "[TRHack] Kicked for Forcefield-hacking!");
				if (Hacks.forcefield.broadcast) Util.broadcastNoConsole("[TRHack] " + message, "tekkitrestrict.notify.hack");
				break;
			case speed:
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
				if (Hacks.speed.kick) 		Util.kick(player, "[TRHack] Kicked for speed-hacking!");
				if (Hacks.speed.broadcast) 	Util.broadcastNoConsole("[TRHack] " + message, "tekkitrestrict.notify.hack");
				break;
		}
		
		Log.Hack(message);
	}
	
	private static String convert(String str, String type, Player player){
		if (str == null) return "null";
		return TStrings.replace(TStrings.convertColors(str),
				"{PLAYER}", player.getName(),
				"{TYPE}", 	type == null ? "null" : type,
				"{ID}",		"",
				"{DATA}", 	"",
				"{ITEM}", 	"",
				"  ", 		" ");
	}

	public static void clearMaps() {
		NoHackSpeed.clearMaps();
		NoHackFly.clearMaps();
		NoHackForcefield.clearMaps();
	}

	//sync
	public static void playerLogout(String name) {
		if (name == null) return;
		// clears ALL lists for said player
		NoHackSpeed.playerLogout(name);
		NoHackFly.playerLogout(name);
		NoHackForcefield.playerLogout(name);
		cmdFly.remove(name);
		cmdForcefield.remove(name);
		cmdSpeed.remove(name);
		//TRLimitFlyThread.setGrounded(player);
	}
}
