package com.github.dreadslicer.tekkitrestrict;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.github.dreadslicer.tekkitrestrict.listeners.NoHackFly;
import com.github.dreadslicer.tekkitrestrict.listeners.NoHackForcefield;
import com.github.dreadslicer.tekkitrestrict.listeners.NoHackSpeed;
import com.github.dreadslicer.tekkitrestrict.objects.TREnums.HackType;

public class TRNoHack {
	//public static int hacks = 0;
	
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
		//TRLimitFlyThread.setGrounded(player);
	}
}
