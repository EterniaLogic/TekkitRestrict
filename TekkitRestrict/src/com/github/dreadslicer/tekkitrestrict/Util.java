package com.github.dreadslicer.tekkitrestrict;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.github.dreadslicer.tekkitrestrict.TRConfigCache.Global;
import com.github.dreadslicer.tekkitrestrict.TRConfigCache.Hacks;
import com.github.dreadslicer.tekkitrestrict.TRNoHack.HackType;

public class Util {
	public static boolean hasBypass(Player player, String type, String sub){
		return player.hasPermission("tekkitrestrict.bypass."+type+"."+sub);
	}
	public static boolean hasHackBypass(Player player, String type){
		return player.hasPermission("tekkitrestrict.bypass.hack."+type);
	}
	public static void kick(Player player, String message){
		if (Global.kickFromConsole)
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "kick " + player.getName() + " " + message);
		else
			player.kickPlayer(message);
	}
	public static boolean kickHack(HackType type){
		if (type == HackType.fly) return Hacks.kick.contains("fly");
		else if (type == HackType.forcefield) return Hacks.kick.contains("forcefield");
		else if (type == HackType.speed) return Hacks.kick.contains("speed");
		else return false;
	}
	/**
	 * Kick a player if the kick for that hacktype is enabled.<br>
	 * The kick message will be:
	 * <code>Kicked for [type]-Hacking!</code>
	 */
	public static void kickHacker(HackType type, Player player){
		if (Util.kickHack(type)) Util.kick(player, "Kicked for " + type.toString() + "-Hacking!");
	}
}
