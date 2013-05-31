package com.github.dreadslicer.tekkitrestrict;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class TRNoHack {
	public static enum HackType {
		fly, forcefield, speed
	};

	public static int hacks = 0;

	public static void reload() {
		TRHandleFly.reload();
		TRNoHackForcefield.reload();
		TRNoHackSpeed.reload();
	}

	/*public static void handleHack(Player player, HackType type) {
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
	}*/
	
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
		Log.Hack(type.toString(), player.getName());
		//Log.Debug(additional);
		Util.kickHacker(type, player);
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
