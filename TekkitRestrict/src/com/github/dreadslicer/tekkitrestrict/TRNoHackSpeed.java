package com.github.dreadslicer.tekkitrestrict;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

import com.github.dreadslicer.tekkitrestrict.TRNoHack.HackType;

public class TRNoHackSpeed {
	private static Map<Player, Double[]> tickLastLoc = new ConcurrentHashMap<Player, Double[]>();
	private static Map<Player, Integer> tickTolerance = new ConcurrentHashMap<Player, Integer>();
	private static boolean enabled = true;
	private static double maxSpeed = 2.5, tolerance = 10;

	public static void reload() {
		maxSpeed = tekkitrestrict.config.getDouble("HackMoveSpeedMax");
		tolerance = tekkitrestrict.config.getDouble("HackMoveSpeedTolerance");
		enabled = tekkitrestrict.config.getBoolean("UseAntiMovementSpeedHack");
	}

	public static void handleMove(PlayerMoveEvent e) {
		Player player = e.getPlayer();
		if (!TRPermHandler.hasPermission(player, "hack", "bypass", "")
				&& enabled) {
			// determine position of player to get velocity.
			int maxmove = 15 + 1; // above this value, forget players will be
									// kicked anyways.

			// determine XZ velocity
			if (tickLastLoc.containsKey(player)) {
				Object c = tickLastLoc.get(player);
				if (c != null) {
					Double[] XZ = (Double[]) c;
					//
					double x0 = XZ[0], z0 = XZ[1];
					double x1 = e.getPlayer().getLocation().getX(), z1 = e
							.getPlayer().getLocation().getZ();

					double xe = x0 - x1, ze = z0 - z1;
					double velo = Math
							.sqrt(Math.pow(xe, 2D) + Math.pow(ze, 2D));

					if (velo >= maxSpeed && velo <= maxmove) {
						if (tickTolerance.get(e.getPlayer()) != null) {
							tickTolerance.put(e.getPlayer(),
									tickTolerance.get(e.getPlayer()) + 1);
							if (tickTolerance.get(e.getPlayer()) > tolerance) {
								tickTolerance.remove(e.getPlayer());
								TRNoHack.handleHack(e.getPlayer(),
										HackType.speed);
								e.getPlayer().teleport(
										e.getPlayer()
												.getWorld()
												.getHighestBlockAt(
														new Double(x0)
																.intValue(),
														new Double(z0)
																.intValue())
												.getLocation());
							}
						} else {
							tickTolerance.put(e.getPlayer(), 1);
						}
					} else {
						tickTolerance.remove(e.getPlayer());
					}
				}
			}
			tickLastLoc.put(player, new Double[] {
					e.getPlayer().getLocation().getX(),
					e.getPlayer().getLocation().getZ() });
		} else {
			tickTolerance.remove(e.getPlayer());
		}
	}

	public static void clearMaps() {
		// flushes the fly locator map.
		tickLastLoc.clear();
	}

	public static void playerLogout(Player player) {
		if (tickLastLoc.containsKey(player)) {
			tickLastLoc.remove(player);
		}
	}
}
