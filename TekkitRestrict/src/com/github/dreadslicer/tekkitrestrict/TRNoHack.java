package com.github.dreadslicer.tekkitrestrict;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;

import net.minecraft.server.EntityPlayer;
import net.minecraft.server.ServerConfigurationManager;

import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class TRNoHack {
	private static List<String> KickOnHack, banOnHack;

	// public static Map<Player,Boolean> hackers = new
	// ConcurrentHashMap<Player,Boolean>();
	// private static String[] hackTypes = new
	// String[]{"fly:FlyHack","forcefield:ForcefieldHack"};
	// private static String[] hackMessage = new
	// String[]{"fly:fly hacking","forcefield:forcefield hacking"};
	public static enum HackType {
		fly, forcefield, speed
	};

	public static int hacks = 0;

	public static void reload() {
		KickOnHack = Collections.synchronizedList(tekkitrestrict.config
				.getStringList("HackKick"));
		banOnHack = Collections.synchronizedList(tekkitrestrict.config
				.getStringList("HackBan"));
		TRHandleFly.reload();
		TRNoHackForcefield.reload();
		TRNoHackSpeed.reload();
	}

	public static void handleHack(Player player, HackType type) {
		String g = type == HackType.fly ? "fly hack"
				: type == HackType.forcefield ? "forcefield hack"
						: type == HackType.speed ? "movement speed hack" : "";
		String gr = type == HackType.fly ? "FlyHack"
				: type == HackType.forcefield ? "ForcefieldHack"
						: type == HackType.speed ? "SpeedHack" : "";
		hacks++;
		// determine if player gets banned.
		int x = player.getLocation().getBlockX();
		int y = player.getLocation().getBlockY();
		int z = player.getLocation().getBlockZ();
		Entity veh = player.getVehicle();
		List<Entity> nent = player.getNearbyEntities(16, 16, 16);
		int npl = 0, nmob = 0;
		for (Entity gx : nent) {
			if (gx instanceof EntityPlayer) {
				npl++;
			} else {
				nmob++;
			}
		}
		Vector velo = player.getVelocity();
		DecimalFormat myFormatter = new DecimalFormat("#.##");
		String additonal = "Loc: [" + player.getWorld().getName() + "," + x
				+ "," + y + "," + z + "] " + "Velo: ["
				+ myFormatter.format(velo.getX()) + " m/s,"
				+ myFormatter.format(velo.getY()) + " m/s,"
				+ myFormatter.format(velo.getZ()) + " m/s]  "
				+"Vehicle: ["
				+(veh != null ? veh.getClass().getName() : "none") + "] "
				+"Entity#: [player: " + npl + ", mob: " + nmob + "]";
		if (banOnHack.contains(gr.toLowerCase())) {
			TRLogger.Log(gr, "Player [" + player.getName()
					+ "] got banned by using " + g + "! " + additonal);
			TRLogger.broadcastHack(player.getName(), gr, "got banned by using");
			ServerConfigurationManager c = ((CraftServer) tekkitrestrict
					.getInstance().getServer()).getHandle();
			c.addUserBan(player.getName());
			player.kickPlayer("[TRHack] You have been banned for " + g + "!");
		} else if (KickOnHack.contains(gr.toLowerCase())) {
			TRLogger.Log(gr, "Player [" + player.getName()
					+ "] got kicked using the " + g + "! " + additonal);
			TRLogger.broadcastHack(player.getName(), gr, "got kicked using the");
			player.kickPlayer("[TRHack] You have been kicked for " + g + "!");
		} else {
			TRLogger.Log(gr, "Player [" + player.getName() + "] tried to " + g
					+ "! " + additonal);
			TRLogger.broadcastHack(player.getName(), gr, "tried to");
		}
	}

	public static void groundPlayer(Player e) {
		// sets the player to the topmost block
		Block highest = e.getWorld().getHighestBlockAt(e.getLocation());
		e.teleport(highest.getLocation());
	}

	public static void clearMaps() {
		TRNoHackSpeed.clearMaps();
		TRHandleFly.clearMaps();
		TRNoHackForcefield.clearMaps();
	}

	public static void playerLogout(Player player) {
		// clears ALL lists for said player
		TRNoHackSpeed.playerLogout(player);
		TRHandleFly.playerLogout(player);
		TRNoHackForcefield.playerLogout(player);
		TRLimitFly.setGrounded(player);
	}
}
